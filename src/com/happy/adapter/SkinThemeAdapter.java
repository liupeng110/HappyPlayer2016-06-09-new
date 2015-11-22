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
import android.widget.ImageView;

import com.happy.adapter.SkinThemeAdapter.ItemViewHolder;
import com.happy.common.Constants;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.DataUtil;
import com.happy.util.HttpUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.CardViewRelativeLayout;
import com.happy.widget.MainTextView;

public class SkinThemeAdapter extends Adapter<ItemViewHolder> implements
		Observer {

	private List<SkinThemeApp> datas;
	private Context context;
	private int selectedIndex = 0;

	public SkinThemeAdapter(Context context, List<SkinThemeApp> datas) {
		this.context = context;
		this.datas = datas;
		selectedIndex = findOldSelectedIndex();
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 查找已选择的主题皮肤的索引
	 * 
	 * @return
	 */
	private int findOldSelectedIndex() {
		if (datas != null && datas.size() != 0) {
			for (int i = 0; i < datas.size(); i++) {
				SkinThemeApp temp = datas.get(i);
				if (temp.getID().equals(Constants.skinID)) {
					return i;
				}
			}
		}
		return 0;
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(ItemViewHolder itemViewHolder,
			final int position) {
		final SkinThemeApp skinTheme = datas.get(position);
		ImageView themeImageView = itemViewHolder.getThemeImageView();
		if (skinTheme.getAssetsType() == SkinThemeApp.LOCAL) {

			ImageLoadUtil
					.loadImageFormFile(skinTheme.getPreviewPath(),
							themeImageView,
							R.drawable.img_skin_default_thumbnail, true);
		} else {
			String imageUrl = HttpUtil.getSkinThemePreviewImageByID(skinTheme
					.getID());
			ImageLoadUtil.loadImageFormUrl(imageUrl, themeImageView,
					R.drawable.picture_manager_default, true);
		}
		final ImageView selectImageView = itemViewHolder.getselectImageView();

		if (!skinTheme.getID().equals(Constants.skinID)) {
			selectImageView.setVisibility(View.INVISIBLE);
		} else {
			selectImageView.setVisibility(View.VISIBLE);
		}
		itemViewHolder.getMainTextView().setText(skinTheme.getThemeName() + "");
		itemViewHolder.getCardViewRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!skinTheme.getID().equals(Constants.skinID)) {
							selectImageView.setVisibility(View.VISIBLE);
							reshPICStatusUI(selectedIndex);
							selectedIndex = position;
							// 设置当前皮肤主题的id和保存，并通知各个页面去加载新的皮肤
							Constants.skinID = skinTheme.getID();
							DataUtil.saveValue(context, Constants.skinID_KEY,
									Constants.skinID);
							loadSkinData(skinTheme);
						}
					}
				});
	}

	/**
	 * 刷新界面
	 * 
	 * @param oldSelectedIndex
	 */
	protected void reshPICStatusUI(int oldSelectedIndex) {
		this.notifyItemChanged(oldSelectedIndex);
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// 加载数据item的布局，生成VH返回
		View v = LayoutInflater.from(context).inflate(
				R.layout.list_item_theme_pic, viewGroup, false);
		return new ItemViewHolder(v);
	}

	/**
	 * 加载皮肤数据
	 * 
	 * @param skinTheme
	 */
	protected void loadSkinData(SkinThemeApp skinTheme) {
		DataUtil.loadSkin(context);
		ObserverManage.getObserver().setMessage(skinTheme);
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			SkinThemeApp skinTheme = (SkinThemeApp) data;
			reshPICStatusUI(selectedIndex);
			for (int i = 0; i < datas.size(); i++) {
				if (skinTheme.getID().equals(datas.get(i).getID())) {
					selectedIndex = i;
					reshPICStatusUI(selectedIndex);
					break;
				}
			}
		} else if (data instanceof MessageIntent) {
			MessageIntent mi = (MessageIntent) data;
			if (mi.getAction().equals(MessageIntent.SKINTHEMEERROR)) {
				ToastUtil.showText("皮肤加载失败!");
				notifyDataSetChanged();
			}

		}
	}

	// 可复用的VH
	class ItemViewHolder extends ViewHolder {
		private ImageView themeImageView;

		private ImageView selectImageView;

		private MainTextView themeName;

		private CardViewRelativeLayout cardViewRelativeLayout;

		private View itemView;

		public ItemViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		ImageView getThemeImageView() {
			if (themeImageView == null) {
				themeImageView = (ImageView) itemView
						.findViewById(R.id.theme_pic);
			}
			return themeImageView;
		}

		ImageView getselectImageView() {
			if (selectImageView == null) {
				selectImageView = (ImageView) itemView
						.findViewById(R.id.secect_stats);
			}
			return selectImageView;
		}

		MainTextView getMainTextView() {
			if (themeName == null) {
				themeName = (MainTextView) itemView
						.findViewById(R.id.theme_text);
			}
			return themeName;
		}

		CardViewRelativeLayout getCardViewRelativeLayout() {
			if (cardViewRelativeLayout == null) {
				cardViewRelativeLayout = (CardViewRelativeLayout) itemView
						.findViewById(R.id.itembg);
			}
			return cardViewRelativeLayout;
		}
	}

	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
	}

}
