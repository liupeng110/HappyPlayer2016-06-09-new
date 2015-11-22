package com.happy.adapter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eva.views.RoundedImageView;
import com.happy.common.Constants;
import com.happy.manage.MediaManage;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.AlbumUtil;
import com.happy.util.DataUtil;
import com.happy.widget.PopPlayListItemRelativeLayout;

public class PopupLrcPlayListAdapter extends BaseAdapter implements Observer {
	private List<SongInfo> playlist;
	private Context context;
	private int playIndexPosition = -1;
	private ListView popPlayListView;
	private PopupWindow mPopupWindow;

	public PopupLrcPlayListAdapter(Context context, List<SongInfo> playlist,
			ListView popPlayListView, PopupWindow mPopupWindow) {
		this.playlist = playlist;
		this.context = context;
		this.popPlayListView = popPlayListView;
		this.mPopupWindow = mPopupWindow;
		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	public int getCount() {
		return playlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return playlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.popup_list_lrc_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final SongInfo songInfo = playlist.get(position);
		final RoundedImageView singerImageView = holder.getSingerImageView();
		final TextView songNameTextView = holder.getSongNameTextView();
		final TextView singerNameTextView = holder.getSingerNameTextView();
		songNameTextView.setText(songInfo.getTitle());
		singerNameTextView.setText(songInfo.getSinger());
		final PopPlayListItemRelativeLayout listitemBG = holder.getListitemBG();
		final TextView songNoTextView = holder.getSongNoTextView();
		songNoTextView.setText((position + 1) + "");

		if (songInfo.getSid().equals(Constants.playInfoID)) {
			playIndexPosition = position;
		}
		if (playIndexPosition == position) {
			listitemBG.setSelect(true);
			songNameTextView.setTextColor(Color.WHITE);
			songNoTextView.setVisibility(View.INVISIBLE);
			singerImageView.setVisibility(View.VISIBLE);

			// 加载歌手图片
			AlbumUtil.loadAlbumImage(context, singerImageView,
					R.drawable.fx_icon_user_default, songInfo.getSid(),
					songInfo.getAlbumUrl(), songInfo.getSinger());

		} else {
			listitemBG.setSelect(false);
			holder.getSongNameTextView().setTextColor(Color.rgb(193, 193, 193));
			songNoTextView.setVisibility(View.VISIBLE);
			singerImageView.setVisibility(View.INVISIBLE);
		}

		listitemBG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (playIndexPosition == position) {

					if (MediaManage.getMediaManage(context).getPlayStatus() == MediaManage.PLAYING) {
						// 当前正在播放，发送暂停
						SongMessage msg = new SongMessage();
						msg.setSongInfo(songInfo);
						msg.setType(SongMessage.PAUSEMUSIC);
						ObserverManage.getObserver().setMessage(msg);
					} else {

						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.PLAYMUSIC);
						// 通知
						ObserverManage.getObserver().setMessage(songMessage);
					}
					return;
				}
				listitemBG.setSelect(true);
				songNameTextView.setTextColor(Color.WHITE);

				songNoTextView.setVisibility(View.INVISIBLE);
				singerImageView.setVisibility(View.VISIBLE);

				AlbumUtil.loadAlbumImage(context, singerImageView,
						R.drawable.fx_icon_user_default, songInfo.getSid(),
						songInfo.getAlbumUrl(), songInfo.getSinger());

				if (playIndexPosition != -1) {
					reshPlayStatusUI(playIndexPosition, false, null);
				}
				playIndexPosition = position;

				Constants.playInfoID = songInfo.getSid();

				// 发送播放
				SongMessage msg = new SongMessage();
				msg.setSongInfo(songInfo);
				msg.setType(SongMessage.PLAYINFOMUSIC);
				ObserverManage.getObserver().setMessage(msg);

				DataUtil.saveValue(context, Constants.playInfoID_KEY,
						Constants.playInfoID);

				if (mPopupWindow != null && mPopupWindow.isShowing())
					mPopupWindow.dismiss();
			}
		});

		return convertView;
	}

	/**
	 * 重新刷新上一次的item页面
	 * 
	 * @param wantedPosition
	 */
	private void reshPlayStatusUI(int wantedPosition, boolean status,
			SongInfo songInfo) {
		int firstPosition = popPlayListView.getFirstVisiblePosition()
				- popPlayListView.getHeaderViewsCount();
		int wantedChild = wantedPosition - firstPosition;
		if (wantedChild < 0 || wantedChild >= popPlayListView.getChildCount()) {
			return;
		}
		View view = popPlayListView.getChildAt(wantedChild);

		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null)
			return;
		if (status) {
			holder.getListitemBG().setSelect(true);
			holder.getSongNameTextView().setTextColor(Color.rgb(193, 193, 193));
			holder.getSongNoTextView().setVisibility(View.INVISIBLE);
			holder.getSingerImageView().setVisibility(View.VISIBLE);

			AlbumUtil.loadAlbumImage(context, holder.getSingerImageView(),
					R.drawable.fx_icon_user_default, songInfo.getSid(),
					songInfo.getAlbumUrl(), songInfo.getSinger());

		} else {
			holder.getListitemBG().setSelect(false);
			holder.getSongNameTextView().setTextColor(Color.WHITE);
			holder.getSongNoTextView().setVisibility(View.VISIBLE);
			holder.getSingerImageView().setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.INITMUSIC) {
				if (songMessage.getSongInfo() != null)
					reshNextPlayStatusUI(songMessage.getSongInfo());
				else
					notifyDataSetChanged();
			}
		}
	}

	/**
	 * 刷新下一首的界面
	 * 
	 * @param songInfo
	 */
	private void reshNextPlayStatusUI(SongInfo songInfo) {
		int oldPlayIndexPosition = playIndexPosition;
		playIndexPosition = MediaManage.getMediaManage(context).getPlayIndex();
		if (playIndexPosition == oldPlayIndexPosition)
			return;
		if (playIndexPosition != -1) {
			reshPlayStatusUI(playIndexPosition, true, songInfo);
		}
		reshPlayStatusUI(oldPlayIndexPosition, false, null);
		if (playIndexPosition != -1) {
			popPlayListView.setSelection(playIndexPosition);
		}
	}

	private class ViewHolder {
		private View view;
		private TextView songNameTextView;
		private TextView singerNameTextView;
		private TextView songNoTextView;
		private RoundedImageView singerImageView;
		private ImageView deleImageView;
		private PopPlayListItemRelativeLayout listitemBG;

		ViewHolder(View v) {
			view = v;
		}

		TextView getSongNameTextView() {
			if (songNameTextView == null) {
				songNameTextView = (TextView) view.findViewById(R.id.song_name);
			}
			return songNameTextView;
		}

		TextView getSingerNameTextView() {
			if (singerNameTextView == null) {
				singerNameTextView = (TextView) view
						.findViewById(R.id.singer_name);
			}
			return singerNameTextView;
		}

		TextView getSongNoTextView() {
			if (songNoTextView == null) {
				songNoTextView = (TextView) view.findViewById(R.id.songno);
			}
			return songNoTextView;
		}

		RoundedImageView getSingerImageView() {
			if (singerImageView == null) {
				singerImageView = (RoundedImageView) view
						.findViewById(R.id.pic);
			}
			return singerImageView;
		}

		PopPlayListItemRelativeLayout getListitemBG() {
			if (listitemBG == null) {
				listitemBG = (PopPlayListItemRelativeLayout) view
						.findViewById(R.id.listitemBG);
			}
			return listitemBG;
		}
	}

}
