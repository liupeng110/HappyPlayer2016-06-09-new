package com.happy.adapter;

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

import com.happy.adapter.RecommendSongAdapter.ItemViewHolder;
import com.happy.common.Constants;
import com.happy.manage.MediaManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.DataUtil;
import com.happy.widget.ListItemRelativeLayout;
import com.happy.widget.PopdownItemRelativeLayout;

public class RecommendSongAdapter extends Adapter<ItemViewHolder> implements
		Observer {
	private List<SongInfo> datas;
	private static Context context;
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
			if (songMessage.getType() == SongMessage.LIKEDELMUSIC) {
				if (songMessage.getSongInfo() != null)
					deleteSong(songMessage.getSongInfo(), -1);
			} else if (songMessage.getType() == SongMessage.LOCALUNLIKEMUSIC) {
				if (songMessage.getSongInfo() != null)
					updateSong(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.INITMUSIC) {
				if (MediaManage.getMediaManage(context).getPlayListType() != MediaManage.PLAYLISTTYPE_NETLIST) {
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

	public RecommendSongAdapter(Context context, List<SongInfo> datas) {
		this.context = context;
		this.datas = datas;
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder itemViewHolder,
			final int position) {

		SongInfo songInfo = datas.get(position);
		itemViewHolder.getSongNameTextView().setText(songInfo.getDisplayName());
		itemViewHolder.getlineView().setBackgroundColor(
				Constants.skinInfo.getItemDividerBackgroundColor());
		itemViewHolder.getStatus().setBackgroundColor(
				Constants.skinInfo.getIndicatorLineBackgroundColor());
		reshViewHolder(position, itemViewHolder, songInfo);
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
						}
						expandIndex = position;
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
		// 设置播放状态
		if (Constants.playInfoID.equals(songInfo.getSid())) {

			playIndexPosition = position;

			itemViewHolder.getListitemBG().setSelect(true);
			itemViewHolder.getStatus().setVisibility(View.VISIBLE);
		} else {
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

							if (MediaManage.getMediaManage(context)
									.getPlayListType() != MediaManage.PLAYLISTTYPE_NETLIST
									|| Constants.playInfoID.equals("")) {

								Constants.playListType = MediaManage.PLAYLISTTYPE_NETLIST;
								MediaManage
										.getMediaManage(context)
										.initPlayListData(
												MediaManage.PLAYLISTTYPE_NETLIST);
							}
							Constants.playInfoID = songInfo.getSid();

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
		itemViewHolder.getDownloadItemRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SongMessage msg = new SongMessage();
						msg.setSongInfo(songInfo);
						msg.setType(SongMessage.DOWNLOADADDMUSIC);
						ObserverManage.getObserver().setMessage(msg);
					}
				});
	}

	/**
	 * 更新歌曲信息
	 * 
	 * @param songInfo
	 */
	private void updateSong(SongInfo songInfo) {
		for (int j = 0; j < datas.size(); j++) {
			if (datas.get(j).getSid().equals(songInfo.getSid())) {
				datas.remove(j);
				datas.add(j, songInfo);
				reshViewHolderUI(j);
				return;
			}
		}
	}

	protected void deleteSong(SongInfo songInfo, int position) {
	}

	/**
	 * 刷新界面
	 * 
	 * @param oldExpandIndex
	 */
	protected void reshViewHolderUI(int oldExpandIndex) {
		this.notifyItemChanged(oldExpandIndex);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			Message msg = new Message();
			msg.obj = songMessage;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// 加载数据item的布局，生成VH返回
		View v = LayoutInflater.from(context).inflate(
				R.layout.netmusiclist_item, viewGroup, false);
		return new ItemViewHolder(v);
	}

	// 可复用的VH
	class ItemViewHolder extends ViewHolder {
		private View itemView;

		private ListItemRelativeLayout listitemBG;

		private TextView songname;
		private View lineView;
		private View status;
		//
		private LinearLayout localPopdownLinearLayout;

		private PopdownItemRelativeLayout downloadItemRelativeLayout;

		private RelativeLayout arrowDownImageView;

		private RelativeLayout arrowUpImageView;

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

		public ListItemRelativeLayout getListitemBG() {
			if (listitemBG == null) {
				listitemBG = (ListItemRelativeLayout) itemView
						.findViewById(R.id.listitemBG);
			}
			return listitemBG;
		}

		public View getStatus() {
			if (status == null) {
				status = itemView.findViewById(R.id.status);
			}
			return status;
		}

		public PopdownItemRelativeLayout getDownloadItemRelativeLayout() {
			if (downloadItemRelativeLayout == null) {
				downloadItemRelativeLayout = (PopdownItemRelativeLayout) itemView
						.findViewById(R.id.download);
			}
			return downloadItemRelativeLayout;
		}
	}

	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
	}
}
