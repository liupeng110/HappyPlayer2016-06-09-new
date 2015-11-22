package com.happy.adapter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.adapter.SearchSongAdapter.ItemViewHolder;
import com.happy.common.Constants;
import com.happy.model.app.SongInfo;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;

public class SearchSongAdapter extends Adapter<ItemViewHolder> implements
		Observer {
	private List<SongInfo> datas;
	private static Context context;
	/**
	 * item展开索引
	 */
	private int expandIndex = -1;

	public SearchSongAdapter(Context context, List<SongInfo> datas) {
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
			final ItemViewHolder itemViewHolder, SongInfo songInfo) {
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

		private TextView songname;
		private View lineView;
		//
		private LinearLayout localPopdownLinearLayout;

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
	}

	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
	}
}
