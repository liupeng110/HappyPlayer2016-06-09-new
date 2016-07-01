package com.happy.adapter;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.manage.MediaManage;
import com.happy.model.app.Category;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.DataUtil;
import com.happy.util.DateUtil;
import com.happy.util.DownloadManage;
import com.happy.util.DownloadThreadManage;
import com.happy.util.DownloadThreadPool;
import com.happy.util.DownloadThreadPool.IDownloadTaskEventCallBack;
import com.happy.util.HttpUtil;
import com.happy.widget.AlartTwoButtonDialog.TwoButtonDialogListener;
import com.happy.widget.AlartTwoButtonDialogTitle;
import com.happy.widget.ListItemRelativeLayout;
import com.happy.widget.MainTextView;
import com.happy.widget.PopdownItemRelativeLayout;

public class DownloadAdapter extends Adapter<ViewHolder> implements Observer {
	/**
	 * 标题
	 */
	public final static int CATEGORYTITLE = 0;
	/**
	 * item正在下载
	 */
	public final static int ITEMDownloading = 1;
	/**
	 * item已经下载
	 */
	public final static int ITEMDownloaded = 2;

	private List<Category> categorys;
	private Context context;

	/**
	 * item展开索引
	 */
	private int expandIndex = -1;
	/**
	 * 播放歌曲索引
	 */
	private int playIndexPosition = -1;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			SongMessage songMessage = (SongMessage) msg.obj;
			if (songMessage.getType() == SongMessage.DOWNLOADADDMUSIC) {
				addDownloadSong(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.INITMUSIC) {
				if (MediaManage.getMediaManage(context).getPlayListType() != MediaManage.PLAYLISTTYPE_DOWNLOADLIST) {
					if (playIndexPosition != -1) {
						notifyItemChanged(playIndexPosition);
						playIndexPosition = -1;
					}
				} else {
					if (playIndexPosition != -1) {
						notifyItemChanged(playIndexPosition);
						playIndexPosition = -1;
					}
					if (songMessage.getSongInfo() != null)
						updateSong(songMessage.getSongInfo());
				}
			}

		}
	};

	private Handler downloadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DownloadTask task = (DownloadTask) msg.obj;
			if (task != null) {
				SongInfo songInfo = getSongInfoForSid(task.getTid());
				if (songInfo != null) {

					songInfo.setDownloadProgress(task.getDownloadedSize());
					if (songInfo.getDownloadProgress() == songInfo.getSize()) {
						updateDownloadSong(songInfo);
					} else {
						updateSong(songInfo);
					}
				}
			}
		}

	};

	public DownloadAdapter(Context context, List<Category> categorys) {
		this.context = context;
		this.categorys = categorys;

		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	public int getItemCount() {
		int count = 0;

		if (null != categorys) {
			// 所有分类中item的总和是ListVIew Item的总个数
			for (Category category : categorys) {
				count += category.getItemCount();
			}
		}
		// 添加了底部的菜单，所以加多一个item
		return count;
	}

	/**
	 * 获取有效item的个数
	 * 
	 * @return
	 */
	public int getmCategoryItemCount() {
		int count = 0;

		if (null != categorys) {
			// 所有分类中item的总和是ListVIew Item的总个数
			for (Category category : categorys) {
				count += category.getmCategoryItemCount();
			}
		}
		// 添加了底部的菜单，所以加多一个item
		return count;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		if (holder instanceof CategoryViewHolder) {
			CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
			String mCategoryName = (String) getItem(position);

			categoryViewHolder.getCategoryTextTextView().setText(
					mCategoryName + "("
							+ getItemSizeByCategoryName(mCategoryName) + ")");
			categoryViewHolder.getCategoryTextTextView().setTextColor(
					Constants.skinInfo.getIndicatorLineBackgroundColor());
			categoryViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());

		} else if (holder instanceof ItemDownloadedViewHolder) {
			ItemDownloadedViewHolder itemViewHolder = (ItemDownloadedViewHolder) holder;
			SongInfo songInfo = (SongInfo) getItem(position);
			itemViewHolder.getSongNameTextView().setText(
					songInfo.getDisplayName());
			itemViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());

			itemViewHolder.getStatus().setBackgroundColor(
					Constants.skinInfo.getIndicatorLineBackgroundColor());

			reshViewHolder(position, itemViewHolder, songInfo);

		} else if (holder instanceof ItemDownloadingViewHolder) {
			ItemDownloadingViewHolder itemViewHolder = (ItemDownloadingViewHolder) holder;
			SongInfo songInfo = (SongInfo) getItem(position);
			itemViewHolder.getSongNameTextView().setText(
					songInfo.getDisplayName());
			itemViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());
			reshViewHolder(position, itemViewHolder, songInfo);

		}
	}

	/**
	 * 通过种类名称来获取该分类下的歌曲数目
	 * 
	 * @param mCategoryName
	 * @return
	 */
	private int getItemSizeByCategoryName(String mCategoryName) {
		int count = 0;

		if (null != categorys) {
			// 所有分类中item的总和是ListVIew Item的总个数
			for (Category category : categorys) {
				if (category.getmCategoryName().equals(mCategoryName)) {
					count = category.getmCategoryItemCount();
					break;
				}
			}
		}
		return count;
	}

	/**
	 * 刷新item ui
	 * 
	 * @param position
	 * @param itemViewHolder
	 * @param songInfo
	 */
	private void reshViewHolder(final int position,
			final ItemDownloadedViewHolder itemViewHolder,
			final SongInfo songInfo) {
		itemViewHolder.getArrowDownImageView().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (expandIndex != -1 && expandIndex != position) {
							reshViewHolderUI(expandIndex);
						}
						expandIndex = position;
						itemViewHolder.getLocalPopdownLinearLayout()
								.setVisibility(View.VISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.VISIBLE);
					}
				});
		itemViewHolder.getArrowUpImageView().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (expandIndex != -1 && expandIndex != position) {
							reshViewHolderUI(expandIndex);
						} else if (expandIndex == position) {
							expandIndex = -1;
						} else {
							expandIndex = position;
						}
						itemViewHolder.getLocalPopdownLinearLayout()
								.setVisibility(View.GONE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.INVISIBLE);
					}
				});
		if (expandIndex == position) {
			itemViewHolder.getLocalPopdownLinearLayout().setVisibility(
					View.VISIBLE);
			itemViewHolder.getArrowDownImageView()
					.setVisibility(View.INVISIBLE);
			itemViewHolder.getArrowUpImageView().setVisibility(View.VISIBLE);
		} else {
			itemViewHolder.getLocalPopdownLinearLayout().setVisibility(
					View.GONE);
			itemViewHolder.getArrowDownImageView().setVisibility(View.VISIBLE);
			itemViewHolder.getArrowUpImageView().setVisibility(View.INVISIBLE);
		}

		itemViewHolder.getDeletePopdownItemRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// showTwoAlert(songInfo, position);
					}
				});

		// 设置播放状态
		if (MediaManage.getMediaManage(context).getPlayListType() == MediaManage.PLAYLISTTYPE_DOWNLOADLIST
				&& Constants.playInfoID.equals(songInfo.getSid())) {

			playIndexPosition = position;

			itemViewHolder.getListitemBG().setSelect(true);
			itemViewHolder.getStatus().setVisibility(View.VISIBLE);
		} else {
			// playIndexPosition = -1;
			itemViewHolder.getListitemBG().setSelect(false);
			itemViewHolder.getStatus().setVisibility(View.INVISIBLE);
		}

		itemViewHolder.getListitemBG().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (playIndexPosition == position) {

							if (MediaManage.getMediaManage(context)
									.getPlayStatus() == MediaManage.PLAYING) {
								// 当前正在播放，发送暂停
								SongMessage msg = new SongMessage();
								msg.setSongInfo(songInfo);
								msg.setType(SongMessage.PAUSEMUSIC);
								ObserverManage.getObserver().setMessage(msg);
							} else {

								SongMessage songMessage = new SongMessage();
								songMessage.setType(SongMessage.PLAYMUSIC);
								// 通知
								ObserverManage.getObserver().setMessage(
										songMessage);
							}

						} else {
							itemViewHolder.getListitemBG().setSelect(true);
							itemViewHolder.getStatus().setVisibility(
									View.VISIBLE);
							if (playIndexPosition != -1) {
								notifyItemChanged(playIndexPosition);
							}
							playIndexPosition = position;
							Constants.playInfoID = songInfo.getSid();

							if (MediaManage.getMediaManage(context)
									.getPlayListType() != MediaManage.PLAYLISTTYPE_DOWNLOADLIST) {

								Constants.playListType = MediaManage.PLAYLISTTYPE_DOWNLOADLIST;
								MediaManage
										.getMediaManage(context)
										.initPlayListData(
												MediaManage.PLAYLISTTYPE_DOWNLOADLIST);
							}

							// 发送播放
							SongMessage msg = new SongMessage();
							msg.setSongInfo(songInfo);
							msg.setType(SongMessage.PLAYINFOMUSIC);
							ObserverManage.getObserver().setMessage(msg);

							DataUtil.saveValue(context,
									Constants.playInfoID_KEY,
									Constants.playInfoID);
						}
					}
				});
	}

	/**
	 * 刷新item ui
	 * 
	 * @param position
	 * @param itemViewHolder
	 * @param songInfo
	 */
	private void reshViewHolder(final int position,
			final ItemDownloadingViewHolder itemViewHolder,
			final SongInfo songInfo) {
		itemViewHolder.getArrowDownImageView().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (expandIndex != -1 && expandIndex != position) {
							reshViewHolderUI(expandIndex);
						}
						expandIndex = position;
						itemViewHolder.getLocalPopdownLinearLayout()
								.setVisibility(View.VISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.VISIBLE);
					}
				});
		itemViewHolder.getArrowUpImageView().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (expandIndex != -1 && expandIndex != position) {
							reshViewHolderUI(expandIndex);
						} else if (expandIndex == position) {
							expandIndex = -1;
						} else {
							expandIndex = position;
						}
						itemViewHolder.getLocalPopdownLinearLayout()
								.setVisibility(View.GONE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.INVISIBLE);
						itemViewHolder.getArrowDownImageView().setVisibility(
								View.VISIBLE);
						itemViewHolder.getArrowUpImageView().setVisibility(
								View.INVISIBLE);
					}
				});
		if (expandIndex == position) {
			itemViewHolder.getLocalPopdownLinearLayout().setVisibility(
					View.VISIBLE);
			itemViewHolder.getArrowDownImageView()
					.setVisibility(View.INVISIBLE);
			itemViewHolder.getArrowUpImageView().setVisibility(View.VISIBLE);
		} else {
			itemViewHolder.getLocalPopdownLinearLayout().setVisibility(
					View.GONE);
			itemViewHolder.getArrowDownImageView().setVisibility(View.VISIBLE);
			itemViewHolder.getArrowUpImageView().setVisibility(View.INVISIBLE);
		}
		// 更新进度
		String progress = (int) (songInfo.getDownloadProgress() * 1.00
				/ songInfo.getSize() * 100)
				+ "%";
		itemViewHolder.getProgressTextView().setText(progress);

		// 更新提示语
		DownloadThreadPool dp = DownloadManage.getDownloadSongTM(context);
		DownloadTask task = dp.getDownloadTask(songInfo.getSid());
		String tipText = "";
		if (task == null) {
			tipText = "点击下载";
		} else {
			DownloadThreadManage dtm = task.getDownloadThreadManage();
			if (dtm.isCancel() || dtm.isError() || dtm.isPause()) {
				tipText = "点击下载";
			} else if (dtm.isFinish()) {
				tipText = "下载完成";
			} else if (dtm.isDownloading()) {
				tipText = "点击暂停";
			} else {
				tipText = "等待下载(点击暂停下载)";
			}
		}
		itemViewHolder.getTipTextView().setText(tipText);

		itemViewHolder.getDeletePopdownItemRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// showTwoAlert(songInfo, position);
					}
				});

		itemViewHolder.getListitemBG().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						DownloadThreadPool dp = DownloadManage
								.getDownloadSongTM(context);
						DownloadTask task = dp.getDownloadTask(songInfo
								.getSid());
						if (task == null) {
							// 点击下载
							downloadSong(songInfo);
						} else {
							DownloadThreadManage dtm = task
									.getDownloadThreadManage();
							if (dtm.isCancel() || dtm.isError()
									|| dtm.isPause()) {
								// 点击下载
								downloadSong(songInfo);
							} else if (dtm.isFinish()) {
								// 下载完成
							} else if (dtm.isDownloading()) {
								// 点击暂停
								dtm.pause();
							} else {
								// 等待下载(点击暂停下载)
								dp.cancelWaiting(songInfo.getSid());
							}
						}

					}
				});
	}

	/**
	 * 下载歌曲
	 * 
	 * @param songInfo
	 */
	protected void downloadSong(SongInfo songInfo) {
		String filePath = Constants.PATH_MP3TEMP + File.separator
				+ songInfo.getSid() + ".temp";

		String sid = songInfo.getSid();
		DownloadTask task = new DownloadTask();
		String url = HttpUtil.getSongInfoDataByID(sid);
		task.setTid(sid);
		task.setStatus(DownloadTask.INT);
		task.setDownloadUrl(url);
		task.setFilePath(filePath);
		task.setFileSize(songInfo.getSize());
		task.setDownloadedSize(songInfo.getDownloadProgress());
		task.setAddTime(songInfo.getCreateTime());
		task.setType(DownloadTask.SONG_NET_DOWNLOAD);

		DownloadThreadManage dtm = new DownloadThreadManage(task, 20, 100);
		task.setDownloadThreadManage(dtm);
		DownloadThreadPool dp = DownloadManage.getDownloadSongTM(context);
		dp.setEvent(downloadSongCallBack);
		dp.addDownloadSongTask(task);
	}

	private IDownloadTaskEventCallBack downloadSongCallBack = new IDownloadTaskEventCallBack() {

		@Override
		public void waiting(DownloadTask task) {
			task.setStatus(DownloadTask.WAITING);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void downloading(DownloadTask task, int downloadSize) {

			SongDB.getSongInfoDB(context).updateSongDownloadProgress(
					task.getTid(), downloadSize, SongInfo.DOWNLOADSONG);

			task.setStatus(DownloadTask.DOWNLOING);
			task.setDownloadedSize(downloadSize);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void threadDownloading(DownloadTask task, int downloadSize,
				int threadIndex, int threadNum, int startIndex, int endIndex) {
		}

		@Override
		public void pauseed(DownloadTask task, int downloadSize) {

			SongDB.getSongInfoDB(context).updateSongDownloadProgress(
					task.getTid(), downloadSize, SongInfo.DOWNLOADSONG);

			task.setStatus(DownloadTask.DOWNLOAD_PAUSE);
			task.setDownloadedSize(downloadSize);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void canceled(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_CANCEL);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void finished(DownloadTask task) {

			SongInfo songInfo = SongDB.getSongInfoDB(context).getSongInfo(
					task.getTid(), SongInfo.DOWNLOADSONG);
			SongDB.getSongInfoDB(context).updateNetSongDownloaded(songInfo);

			task.setDownloadedSize(songInfo.getSize());
			task.setStatus(DownloadTask.DOWNLOAD_FINISH);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void error(DownloadTask task) {
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}

		@Override
		public void cancelWaiting(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_CANCELWAITING);
			// ObserverManage.getObserver().setMessage(task);
			Message msg = new Message();
			msg.obj = task;
			downloadHandler.sendMessage(msg);
		}
	};
	private AlartTwoButtonDialogTitle alartTwoButtonDialog;

	/**
	 * 更新歌曲状态
	 * 
	 * @param songInfo
	 */
	protected void updateSong(SongInfo songInfo, boolean isLikeAdapter) {

		if (null == categorys) {
			return;
		}
		// notifyItemRemoved(position);
		int count = 0;
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			boolean isRemove = false;
			int j = 0;
			for (; j < songInfos.size(); j++) {
				if (songInfos.get(j).getSid().equals(songInfo.getSid())) {
					if (songInfos.get(j).getSid().equals(Constants.playInfoID)) {
						if (playIndexPosition != -1) {
							// notifyItemChanged(playIndexPosition);
							playIndexPosition = -1;
						}
					}
					songInfos.remove(j);
					isRemove = true;
					break;
				}
			}
			if (isRemove) {
				if (songInfos.size() == 0
						&& !category.getmCategoryName().equals("#")) {
					notifyItemChanged(getItemCount());
					if (isLikeAdapter)
						expandIndex = -1;
					else {
						if (expandIndex != -1) {
							expandIndex = -1;
						}
					}
					categorys.remove(category);
					notifyItemRemoved(count + j + 1);
					notifyItemRemoved(count + j);
					// return;
					// notifyItemRemoved(position - 1);
					break;
				} else {
					notifyItemChanged(getItemCount());
					categorys.remove(i);
					category.setmCategoryItem(songInfos);
					categorys.add(i, category);
					if (isLikeAdapter)
						expandIndex = -1;
					else {
						if (expandIndex != -1) {
							expandIndex = -1;
						}
					}
					notifyItemRemoved(count + j + 1);
					// notifyItemChanged(getItemCount());
					// notifyDataSetChanged();
					// return;
					break;
				}
			}
			count += category.getItemCount();
		}

		if (isLikeAdapter) {
			SongDB.getSongInfoDB(context).updatLikeSong(songInfo.getSid(),
					SongInfo.UNLIKE);

			SongMessage songMessage = new SongMessage();
			songInfo.setIslike(SongInfo.UNLIKE);
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.LOCALUNLIKEMUSIC);

			ObserverManage.getObserver().setMessage(songMessage);
		}

		if (songInfo.getSid().equals(Constants.playInfoID)) {
			// 正在删除当前播放的歌曲
			if (MediaManage.getMediaManage(context).getPlayListType() == MediaManage.PLAYLISTTYPE_LOCALLIKELIST) {
				// 如果为当前的播放列表，则自动跳转到下一首歌曲

				new Thread() {

					@Override
					public void run() {
						// try {
						// Thread.sleep(1000);

						MediaManage.getMediaManage(context).initPlayListData(
								MediaManage.PLAYLISTTYPE_LOCALLIKELIST);

						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.NEXTMUSIC);

						ObserverManage.getObserver().setMessage(songMessage);
						//
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}

				}.start();
			}
		} else {
			// 因为删除了一条数据，所以要更新当前的播放索引

			playIndexPosition = getPositionForSid(Constants.playInfoID);
		}
	}

	/**
	 * 弹出警告窗口
	 * 
	 * @param aid
	 */
	protected void showTwoAlert(final SongInfo songInfo, final int position) {

		// if (alartTwoButtonDialog == null) {
		alartTwoButtonDialog = new AlartTwoButtonDialogTitle(context,
				R.style.dialog, new TwoButtonDialogListener() {

					@Override
					public void twoButtonClick() {
						deleteSong(songInfo, position);
					}

					@Override
					public void oneButtonClick() {
					}
				});
		// }
		alartTwoButtonDialog.showDialog("是否删除该歌曲?", songInfo.getDisplayName(),
				"取消", "确定");
	}

	protected void deleteSong(SongInfo songInfo, int position) {

		if (null == categorys) {
			return;
		}
		// notifyItemRemoved(position);
		int count = 0;
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			boolean isRemove = false;
			int j = 0;
			for (; j < songInfos.size(); j++) {
				if (songInfos.get(j).getSid().equals(songInfo.getSid())) {
					// if (songInfos.get(j).getSid().equals(Constants.PLAY_SID))
					// {
					// playIndexPosition = -1;
					// }
					songInfos.remove(j);
					isRemove = true;
					break;
				}
			}
			if (isRemove) {
				if (songInfos.size() == 0
						&& !category.getmCategoryName().equals("#")) {
					notifyItemChanged(getItemCount());
					expandIndex = -1;
					categorys.remove(category);
					notifyItemRemoved(count + j + 1);
					notifyItemRemoved(count + j);
					// return;
					// notifyItemRemoved(position - 1);
					break;
				} else {
					notifyItemChanged(getItemCount());
					categorys.remove(i);
					category.setmCategoryItem(songInfos);
					categorys.add(i, category);
					expandIndex = -1;
					notifyItemRemoved(count + j + 1);
					// notifyItemChanged(getItemCount());
					// notifyDataSetChanged();
					// return;
					break;
				}
			}
			count += category.getItemCount();
		}

		// 从数据库里删除歌曲

		SongDB.getSongInfoDB(context).delete(songInfo.getSid());

		SongMessage songMessage = new SongMessage();
		songMessage.setSongInfo(songInfo);
		songMessage.setType(SongMessage.LIKEDELMUSIC);

		ObserverManage.getObserver().setMessage(songMessage);

		if (position != -1 && playIndexPosition == position) {
			// 正在删除当前播放的歌曲
			if (MediaManage.getMediaManage(context).getPlayListType() == MediaManage.PLAYLISTTYPE_LOCALLIKELIST) {
				// 如果为当前的播放列表，则自动跳转到下一首歌曲

				new Thread() {

					@Override
					public void run() {

						MediaManage.getMediaManage(context).initPlayListData(
								MediaManage.PLAYLISTTYPE_LOCALLIKELIST);

						// try {
						// Thread.sleep(1000);

						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.NEXTMUSIC);

						ObserverManage.getObserver().setMessage(songMessage);
						//
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}

				}.start();
			}
		} else {
			// 因为删除了一条数据，所以要更新当前的播放索引

			playIndexPosition = getPositionForSid(Constants.playInfoID);
		}

	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.DOWNLOADADDMUSIC
					|| songMessage.getType() == SongMessage.INITMUSIC) {
				Message msg = new Message();
				msg.obj = songMessage;
				mHandler.sendMessage(msg);
			}
		} else if (data instanceof DownloadTask) {
			DownloadTask task = (DownloadTask) data;
			if (task.getType() == DownloadTask.SONG_NET_DOWNLOAD) {
				Message msg = new Message();
				msg.obj = task;
				downloadHandler.sendMessage(msg);
			}
		}
	}

	/**
	 * 通过sid获取当前的播放索引
	 * 
	 * @param sid
	 * @return
	 */
	public int getPositionForSid(String sid) {
		int index = -1;
		// 异常情况处理
		if (null == categorys) {
			return -1;
		}

		int count = 0;
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			int j = 0;
			for (; j < songInfos.size(); j++) {
				if (songInfos.get(j).getSid().equals(sid)) {

					index = count + j + 1;

					break;
				}
			}
			count += category.getItemCount();
		}

		return index;
	}

	/**
	 * 通过sid获取歌曲信息
	 * 
	 * @param sid
	 * @return
	 */
	public SongInfo getSongInfoForSid(String sid) {
		if (null == categorys) {
			return null;
		}
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			int j = 0;
			for (; j < songInfos.size(); j++) {
				if (songInfos.get(j).getSid().equals(sid)) {

					return songInfos.get(j);
				}
			}
		}
		return null;
	}

	/**
	 * 添加下载歌曲
	 * 
	 * @param songInfo
	 */
	private void addDownloadSong(SongInfo songInfo) {
		if (categorys == null) {
			return;
		}
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			if (i == 0 && songInfo.getDownloadStatus() == SongInfo.DOWNLOADING) {
				for (int j = 0; j < songInfos.size(); j++) {
					SongInfo temp = songInfos.get(j);
					if (temp.getSid().equals(songInfo.getSid())) {
						return;
					}
				}
				songInfos.add(songInfo);
				categorys.remove(i);
				category.setmCategoryItem(songInfos);
				categorys.add(i, category);
				break;
			} else if (i == 1
					&& songInfo.getDownloadStatus() == SongInfo.DOWNLOADED) {
				for (int j = 0; j < songInfos.size(); j++) {
					SongInfo temp = songInfos.get(j);
					if (temp.getSid().equals(songInfo.getSid())) {
						return;
					}
				}
				songInfos.add(songInfo);
				categorys.remove(i);
				category.setmCategoryItem(songInfos);
				categorys.add(i, category);
				break;
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * 
	 * @param songInfo
	 */
	private void updateDownloadSong(SongInfo songInfo) {
		if (categorys == null) {
			return;
		}
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			if (i == 0 && songInfo.getDownloadProgress() == songInfo.getSize()) {
				for (int j = 0; j < songInfos.size(); j++) {
					SongInfo temp = songInfos.get(j);
					if (temp.getSid().equals(songInfo.getSid())) {
						songInfos.remove(j);
						break;
					}
				}
				categorys.remove(i);
				category.setmCategoryItem(songInfos);
				categorys.add(i, category);
			} else if (i == 1
					&& songInfo.getDownloadProgress() == songInfo.getSize()) {
				songInfo.setDownloadStatus(SongInfo.DOWNLOADED);
				songInfos.add(0, songInfo);
				categorys.remove(i);
				category.setmCategoryItem(songInfos);
				categorys.add(i, category);
			}
		}
		notifyDataSetChanged();
	}

	private void updateSong(SongInfo songInfo) {
		if (null == categorys) {
			return;
		}
		int count = 0;
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getCategoryItem();
			int j = 0;
			for (; j < songInfos.size(); j++) {
				if (songInfos.get(j).getSid().equals(songInfo.getSid())) {
					songInfos.remove(j);
					songInfos.add(j, songInfo);

					categorys.remove(i);
					category.setmCategoryItem(songInfos);
					categorys.add(i, category);

					reshViewHolderUI(count + j + 1);
					// print();

					return;
				}
			}
			count += category.getItemCount();
		}
	}

	/**
	 * 刷新界面
	 * 
	 * @param oldExpandIndex
	 */
	protected void reshViewHolderUI(int oldExpandIndex) {
		this.notifyItemChanged(oldExpandIndex);
	}

	/**
	 * 根据索引获取内容
	 * 
	 * @param position
	 * @return
	 */
	public Object getItem(int position) {
		// 异常情况处理
		if (null == categorys || position < 0 || position > getItemCount()) {
			return null;
		}

		// 同一分类内，第一个元素的索引值
		int categroyFirstIndex = 0;

		for (Category category : categorys) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categroyFirstIndex;
			// item在当前分类内
			if (categoryIndex < size) {
				return category.getItem(categoryIndex);
			}
			// 索引移动到当前分类结尾，即下一个分类第一个元素索引
			categroyFirstIndex += size;
		}

		return null;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

		if (viewType == ITEMDownloading) {

			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.downloadingmusiclist_item, viewGroup, false);
			return new ItemDownloadingViewHolder(v);

		} else if (viewType == ITEMDownloaded) {

			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.downloadedmusiclist_item, viewGroup, false);
			return new ItemDownloadedViewHolder(v);

		} else if (viewType == CATEGORYTITLE) {

			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.localmusiclist_category_title, viewGroup, false);
			return new CategoryViewHolder(v);

		}
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		// 异常情况处理
		if (null == categorys || position < 0 || position > getItemCount()) {
			return CATEGORYTITLE;
		}

		int categroyFirstIndex = 0;

		for (Category category : categorys) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex == 0) {
				return CATEGORYTITLE;
			}
			categroyFirstIndex += size;
		}
		SongInfo songInfo = (SongInfo) getItem(position);
		if (songInfo == null) {
			return CATEGORYTITLE;
		}
		if (songInfo.getDownloadStatus() == SongInfo.DOWNLOADED) {
			return ITEMDownloaded;
		}
		return ITEMDownloading;
	}

	/**
	 * 
	 * @author zhangliangming
	 * 
	 */
	class ItemDownloadingViewHolder extends ViewHolder {
		private View itemView;

		private MainTextView songname;
		private MainTextView tipText;
		private MainTextView progressText;
		private View lineView;
		private LinearLayout localPopdownLinearLayout;

		private RelativeLayout arrowDownImageView;

		private RelativeLayout arrowUpImageView;

		private ListItemRelativeLayout listitemBG;

		private PopdownItemRelativeLayout deletePopdownItemRelativeLayout;

		public ItemDownloadingViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public TextView getSongNameTextView() {
			if (songname == null) {
				songname = (MainTextView) itemView.findViewById(R.id.songname);
			}
			return songname;
		}

		public TextView getTipTextView() {
			if (tipText == null) {
				tipText = (MainTextView) itemView.findViewById(R.id.tipText);
			}
			return tipText;
		}

		public TextView getProgressTextView() {
			if (progressText == null) {
				progressText = (MainTextView) itemView
						.findViewById(R.id.progressText);
			}
			return progressText;
		}

		public View getlineView() {
			if (lineView == null) {
				lineView = itemView.findViewById(R.id.line);
			}
			return lineView;
		}

		public LinearLayout getLocalPopdownLinearLayout() {
			if (localPopdownLinearLayout == null) {
				localPopdownLinearLayout = (LinearLayout) itemView
						.findViewById(R.id.local_popdown);
			}
			return localPopdownLinearLayout;
		}

		public RelativeLayout getArrowDownImageView() {
			if (arrowDownImageView == null) {
				arrowDownImageView = (RelativeLayout) itemView
						.findViewById(R.id.img_right_menu_arrow_down_parent);
			}
			return arrowDownImageView;
		}

		public RelativeLayout getArrowUpImageView() {
			if (arrowUpImageView == null) {
				arrowUpImageView = (RelativeLayout) itemView
						.findViewById(R.id.img_right_menu_arrow_up_parent);
			}
			return arrowUpImageView;
		}

		public PopdownItemRelativeLayout getDeletePopdownItemRelativeLayout() {
			if (deletePopdownItemRelativeLayout == null) {
				deletePopdownItemRelativeLayout = (PopdownItemRelativeLayout) itemView
						.findViewById(R.id.delete);
			}
			return deletePopdownItemRelativeLayout;
		}

		public ListItemRelativeLayout getListitemBG() {
			if (listitemBG == null) {
				listitemBG = (ListItemRelativeLayout) itemView
						.findViewById(R.id.listitemBG);
			}
			return listitemBG;
		}
	}

	// 可复用的VH
	class ItemDownloadedViewHolder extends ViewHolder {
		private View itemView;

		private MainTextView songname;
		private View lineView;

		//
		private LinearLayout localPopdownLinearLayout;

		private RelativeLayout arrowDownImageView;

		private RelativeLayout arrowUpImageView;

		private PopdownItemRelativeLayout deletePopdownItemRelativeLayout;

		private View status;

		private ListItemRelativeLayout listitemBG;

		public ItemDownloadedViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public MainTextView getSongNameTextView() {
			if (songname == null) {
				songname = (MainTextView) itemView.findViewById(R.id.songname);
			}
			return songname;
		}

		public View getlineView() {
			if (lineView == null) {
				lineView = itemView.findViewById(R.id.line);
			}
			return lineView;
		}

		public LinearLayout getLocalPopdownLinearLayout() {
			if (localPopdownLinearLayout == null) {
				localPopdownLinearLayout = (LinearLayout) itemView
						.findViewById(R.id.local_popdown);
			}
			return localPopdownLinearLayout;
		}

		public RelativeLayout getArrowDownImageView() {
			if (arrowDownImageView == null) {
				arrowDownImageView = (RelativeLayout) itemView
						.findViewById(R.id.img_right_menu_arrow_down_parent);
			}
			return arrowDownImageView;
		}

		public RelativeLayout getArrowUpImageView() {
			if (arrowUpImageView == null) {
				arrowUpImageView = (RelativeLayout) itemView
						.findViewById(R.id.img_right_menu_arrow_up_parent);
			}
			return arrowUpImageView;
		}

		public PopdownItemRelativeLayout getDeletePopdownItemRelativeLayout() {
			if (deletePopdownItemRelativeLayout == null) {
				deletePopdownItemRelativeLayout = (PopdownItemRelativeLayout) itemView
						.findViewById(R.id.delete);
			}
			return deletePopdownItemRelativeLayout;
		}

		public View getStatus() {
			if (status == null) {
				status = itemView.findViewById(R.id.status);
			}
			return status;
		}

		public ListItemRelativeLayout getListitemBG() {
			if (listitemBG == null) {
				listitemBG = (ListItemRelativeLayout) itemView
						.findViewById(R.id.listitemBG);
			}
			return listitemBG;
		}
	}

	class CategoryViewHolder extends ViewHolder {
		private View itemView;
		private TextView categoryTextTextView;
		private View lineView;

		public CategoryViewHolder(View view) {
			super(view);
			this.itemView = view;
		}

		public TextView getCategoryTextTextView() {
			if (categoryTextTextView == null) {
				categoryTextTextView = (TextView) itemView
						.findViewById(R.id.category_text);
			}
			return categoryTextTextView;
		}

		public View getlineView() {
			if (lineView == null) {
				lineView = itemView.findViewById(R.id.line);
			}
			return lineView;
		}

	}
}
