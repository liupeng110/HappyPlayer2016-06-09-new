package com.happy.adapter;

import java.util.ArrayList;
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
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.DataUtil;
import com.happy.widget.AlartTwoButtonDialog.TwoButtonDialogListener;
import com.happy.widget.AlartTwoButtonDialogTitle;
import com.happy.widget.ListItemRelativeLayout;
import com.happy.widget.PopdownItemRelativeLayout;

public class LikeSongAdapter extends Adapter<ViewHolder> implements Observer {
	/**
	 * 标题
	 */
	public final static int CATEGORYTITLE = 0;
	/**
	 * item
	 */
	public final static int ITEM = 1;

	/**
	 * 询问item
	 */
	private static final int TYPE_FOOTER = 2;

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

			if (songMessage.getType() == SongMessage.LOCALADDLIKEMUSIC) {
				if (songMessage.getSongInfo() != null)
					addLikeMusic(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC) {
				if (songMessage.getSongInfo() != null)
					updateSong(songMessage.getSongInfo(), false);
			} else if (songMessage.getType() == SongMessage.INITMUSIC) {
				if (MediaManage.getMediaManage(context).getPlayListType() != MediaManage.PLAYLISTTYPE_LOCALLIKELIST) {
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
			} else if (songMessage.getType() == SongMessage.UPDATEMUSIC) {
				if (songMessage.getSongInfo() != null)
					updateSong(songMessage.getSongInfo());
			}

		}

	};

	public LikeSongAdapter(Context context, List<Category> categorys) {
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
		return count + 1;
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

			categoryViewHolder.getCategoryTextTextView().setText(mCategoryName);
			categoryViewHolder.getCategoryTextTextView().setTextColor(
					Constants.skinInfo.getIndicatorLineBackgroundColor());
			categoryViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());

		} else if (holder instanceof ItemViewHolder) {
			ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
			SongInfo songInfo = (SongInfo) getItem(position);
			itemViewHolder.getSongNameTextView().setText(
					songInfo.getDisplayName());
			itemViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());

			itemViewHolder.getStatus().setBackgroundColor(
					Constants.skinInfo.getIndicatorLineBackgroundColor());

			reshViewHolder(position, itemViewHolder, songInfo);

		} else if (holder instanceof FooterViewHolder) {
			FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
			int size = getmCategoryItemCount();
			footerViewHolder.getFooterTextView().setText("共有" + size + "首歌曲");
			footerViewHolder.getlineView().setBackgroundColor(
					Constants.skinInfo.getItemDividerBackgroundColor());
		}
	}

	/**
	 * 刷新item ui
	 * 
	 * @param position
	 * @param itemViewHolder
	 * @param songInfo
	 */
	private void reshViewHolder(final int position,
			final ItemViewHolder itemViewHolder, final SongInfo songInfo) {
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

		itemViewHolder.getLikePopdownItemRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// songInfo.setIslike(SongInfo.NOR);
						// ToastUtil.showCenterTextToast(context, "取消喜欢");
						// LocalSongDB.getSongInfoDB(context).updatLikeSong(
						// songInfo.getSid(), SongInfo.NOR);
						showTwoLikeAlert(songInfo);
					}
				});

		itemViewHolder.getDeletePopdownItemRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						showTwoAlert(songInfo, position);
					}
				});

		if (songInfo.getIslike() == SongInfo.LIKE) {
			itemViewHolder.getLikePopdownItemRelativeLayout().setVisibility(
					View.VISIBLE);
		} else {
			itemViewHolder.getLikePopdownItemRelativeLayout().setVisibility(
					View.GONE);
		}
		// 设置播放状态
		if (MediaManage.getMediaManage(context).getPlayListType() == MediaManage.PLAYLISTTYPE_LOCALLIKELIST
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
									.getPlayListType() != MediaManage.PLAYLISTTYPE_LOCALLIKELIST) {

								Constants.playListType = MediaManage.PLAYLISTTYPE_LOCALLIKELIST;
								MediaManage
										.getMediaManage(context)
										.initPlayListData(
												MediaManage.PLAYLISTTYPE_LOCALLIKELIST);
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

	private AlartTwoButtonDialogTitle alartTwoButtonLikeDialog;

	private AlartTwoButtonDialogTitle alartTwoButtonDialog;

	/**
	 * 弹出警告窗口
	 * 
	 * @param aid
	 */
	protected void showTwoLikeAlert(final SongInfo songInfo) {

		// if (alartTwoButtonDialog == null) {
		alartTwoButtonLikeDialog = new AlartTwoButtonDialogTitle(context,
				R.style.dialog, new TwoButtonDialogListener() {

					@Override
					public void twoButtonClick() {
						updateSong(songInfo, true);
					}

					@Override
					public void oneButtonClick() {
					}
				});
		// }
		alartTwoButtonLikeDialog.showDialog("是否取消喜欢歌曲?",
				songInfo.getDisplayName(), "取消", "确定");
	}

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
			Message msg = new Message();
			msg.obj = songMessage;
			mHandler.sendMessage(msg);
			// if (songMessage.getType() == SongMessage.LOCALADDLIKEMUSIC) {
			// if (songMessage.getSongInfo() != null)
			// addLikeMusic(songMessage.getSongInfo());
			// } else if (songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC)
			// {
			// if (songMessage.getSongInfo() != null)
			// updateSong(songMessage.getSongInfo(), false);
			// } else if (songMessage.getType() == SongMessage.INITMUSIC) {
			// if (MediaManage.getMediaManage(context).getPlayListType() !=
			// MediaManage.PLAYLISTTYPE_LOCALLIKELIST) {
			// if (playIndexPosition != -1) {
			// notifyItemChanged(playIndexPosition);
			// playIndexPosition = -1;
			// }
			// } else {
			// if (playIndexPosition != -1) {
			// notifyItemChanged(playIndexPosition);
			// playIndexPosition = -1;
			// }
			// if (songMessage.getSongInfo() != null)
			// updateSong(songMessage.getSongInfo());
			// }
			// } else if (songMessage.getType() == SongMessage.UPDATEMUSIC) {
			// if (songMessage.getSongInfo() != null)
			// updateSong(songMessage.getSongInfo());
			// }
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
	 * 更新歌曲
	 * 
	 * @param songInfo
	 */
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
	 * 添加喜欢歌曲
	 * 
	 * @param songInfo
	 */
	private void addLikeMusic(SongInfo songInfo) {

		if (categorys == null) {
			return;
		}
		// int count = 0;
		for (int j = 0; j < categorys.size(); j++) {
			Category category = categorys.get(j);
			char categoryChar = songInfo.getCategory().charAt(0);
			String childCategory = songInfo.getChildCategory();
			char tempCategory = category.getmCategoryName().charAt(0);
			if (tempCategory == '#') {
				tempCategory = '^';
			}
			if (categoryChar == '#') {
				categoryChar = '^';
			}

			if (categoryChar == tempCategory) {

				List<SongInfo> lists = category.getmCategoryItem();
				if (lists.size() == 0) {
					lists.add(songInfo);

					if (categoryChar == '^') {
						categoryChar = '#';
					}
					Category categoryTemp = new Category(categoryChar + "");
					categoryTemp.setmCategoryItem(lists);
					categorys.remove(j);
					categorys.add(j, categoryTemp);

					// localSongHandler.sendEmptyMessage(count);
					// localSongHandler.sendEmptyMessage(count + 1);
					//
					// notifyItemInserted(count);
					// notifyItemInserted(count + 1);

				} else {
					for (int i = 0; i < lists.size(); i++) {
						SongInfo tempSongInfo = lists.get(i);
						String tempChildCategory = tempSongInfo
								.getChildCategory();

						if (childCategory.compareTo(tempChildCategory) < 0) {
							lists.add(i, songInfo);

							if (categoryChar == '^') {
								categoryChar = '#';
							}
							Category categoryTemp = new Category(categoryChar
									+ "");
							categoryTemp.setmCategoryItem(lists);
							categorys.remove(j);
							categorys.add(j, categoryTemp);
							//
							// notifyItemInserted(count + i + 1);

							// localSongHandler.sendEmptyMessage(count + i);

							break;
						} else if (i == lists.size() - 1) {
							lists.add(songInfo);

							if (categoryChar == '^') {
								categoryChar = '#';
							}
							Category categoryTemp = new Category(categoryChar
									+ "");
							categoryTemp.setmCategoryItem(lists);
							categorys.remove(j);
							categorys.add(j, categoryTemp);
							//
							// notifyItemInserted(count + i + 1);

							// localSongHandler.sendEmptyMessage(count + i);

							break;
						}
					}
				}

				break;

			} else if (categoryChar < tempCategory || j == categorys.size() - 1) {

				if (categoryChar == '^') {
					categoryChar = '#';
				}
				Category categoryTemp = new Category(categoryChar + "");
				List<SongInfo> lists = new ArrayList<SongInfo>();
				lists.add(songInfo);
				categoryTemp.setmCategoryItem(lists);
				categorys.add(j, categoryTemp);

				// notifyItemInserted(count);
				// notifyItemInserted(count + 1);

				// localSongHandler.sendEmptyMessage(count);

				// localSongHandler.sendEmptyMessage(count + 1);

				break;

			}
			// count += category.getItemCount();
		}
		notifyDataSetChanged();

	}

	// protected void print() {
	// for (int i = 0; i < categorys.size(); i++) {
	// Category c = categorys.get(i);
	// List<SongInfo> list = c.getCategoryItem();
	// for (int j = 0; j < list.size(); j++) {
	// SongInfo song = list.get(j);
	// System.out.println("zhangliang" + song.getIslike());
	// }
	// }
	//
	// }

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

		if (viewType == ITEM) {

			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.likemusiclist_item, viewGroup, false);
			return new ItemViewHolder(v);

		} else if (viewType == CATEGORYTITLE) {

			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.localmusiclist_category_title, viewGroup, false);
			return new CategoryViewHolder(v);

		} else if (viewType == TYPE_FOOTER) {
			// 加载数据item的布局，生成VH返回
			View v = LayoutInflater.from(context).inflate(
					R.layout.localmusiclist_footer, viewGroup, false);
			return new FooterViewHolder(v);
		}
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		// 异常情况处理
		if (null == categorys || position < 0 || position > getItemCount()) {
			return CATEGORYTITLE;
		}

		if (position + 1 == getItemCount())
			return TYPE_FOOTER;

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
		return ITEM;
	}

	// 可复用的VH
	class ItemViewHolder extends ViewHolder {
		private View itemView;

		private TextView songname;
		private View lineView;

		//
		private LinearLayout localPopdownLinearLayout;

		private RelativeLayout arrowDownImageView;

		private RelativeLayout arrowUpImageView;

		private PopdownItemRelativeLayout likePopdownItemRelativeLayout;

		private PopdownItemRelativeLayout deletePopdownItemRelativeLayout;

		private View status;

		private ListItemRelativeLayout listitemBG;

		public ItemViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public TextView getSongNameTextView() {
			if (songname == null) {
				songname = (TextView) itemView.findViewById(R.id.songname);
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

		public PopdownItemRelativeLayout getLikePopdownItemRelativeLayout() {
			if (likePopdownItemRelativeLayout == null) {
				likePopdownItemRelativeLayout = (PopdownItemRelativeLayout) itemView
						.findViewById(R.id.like);
			}
			return likePopdownItemRelativeLayout;
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

	class FooterViewHolder extends ViewHolder {
		private View itemView;
		private TextView footerTextView;
		private View lineView;

		public FooterViewHolder(View view) {
			super(view);
			this.itemView = view;
		}

		public TextView getFooterTextView() {
			if (footerTextView == null) {
				footerTextView = (TextView) itemView
						.findViewById(R.id.list_size_text);
			}
			return footerTextView;
		}

		public View getlineView() {
			if (lineView == null) {
				lineView = itemView.findViewById(R.id.line);
			}
			return lineView;
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

	/**
	 * 通过所在的索引获取item所在的位置
	 * 
	 * @param charAt
	 * @return
	 */
	public int getPositionForSection(char charAt) {
		int count = 0;
		if (null != categorys) {
			for (Category category : categorys) {
				char temp = category.getmCategoryName().charAt(0);
				if (temp == charAt) {
					return count;
				}
				count += category.getItemCount();
			}
		}
		return -1;
	}

	/**
	 * 通过索引获取当前显示的所属分类组
	 * 
	 * @param position
	 * @return
	 */
	public char getPositionForIndex(int position) {
		Object obj = getItem(position);
		if (obj instanceof String) {
			String mCategoryName = (String) obj;
			return mCategoryName.charAt(0);
		} else if (obj instanceof SongInfo) {
			SongInfo songInfo = (SongInfo) obj;
			return songInfo.getCategory().charAt(0);
		}
		return (char) -1;
	}
}
