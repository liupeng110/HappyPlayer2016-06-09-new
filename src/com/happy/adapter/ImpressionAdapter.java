package com.happy.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happy.adapter.ImpressionAdapter.ItemViewHolder;
import com.happy.model.pc.Splash;
import com.happy.ui.R;
import com.happy.util.HttpUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.widget.BaseCardViewRelativeLayout;

public class ImpressionAdapter extends Adapter<ItemViewHolder> {

	private List<Splash> datas;
	private Context context;

	public ImpressionAdapter(Context context, List<Splash> datas) {
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(ItemViewHolder itemViewHolder,
			final int position) {
		final Splash splash = datas.get(position);
		String sid = splash.getSid();
		String imageUrl = HttpUtil.getSplashImageByID(sid);
		ImageLoadUtil.loadImageFormUrl(imageUrl, itemViewHolder.getImavPic(),
				R.drawable.picture_manager_default, true);

		itemViewHolder.getTitle().setText(splash.getTitle() + "");

		itemViewHolder.getButtonPressRelativeLayout().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// Intent intent = new Intent(context,
						// PreviewActivity.class);
						// intent.putExtra("position", position);
						// intent.putExtra("splashs", (Serializable) datas);
						// context.startActivity(intent);
						// ((Activity) context).overridePendingTransition(0, 0);
					}
				});
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// 加载数据item的布局，生成VH返回
		View v = LayoutInflater.from(context).inflate(
				R.layout.listview_item_image, viewGroup, false);
		return new ItemViewHolder(v);
	}

	// 可复用的VH
	class ItemViewHolder extends ViewHolder {
		// 大图
		private ImageView imavPic;

		private TextView title;

		private BaseCardViewRelativeLayout buttonPressRelativeLayout;

		private View itemView;

		public ItemViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public ImageView getImavPic() {
			if (imavPic == null) {
				imavPic = (ImageView) itemView.findViewById(R.id.imavPic);
			}
			return imavPic;
		}

		public TextView getTitle() {
			if (title == null) {
				title = (TextView) itemView.findViewById(R.id.title);
			}
			return title;
		}

		public BaseCardViewRelativeLayout getButtonPressRelativeLayout() {
			if (buttonPressRelativeLayout == null) {
				buttonPressRelativeLayout = (BaseCardViewRelativeLayout) itemView
						.findViewById(R.id.itemBg);
			}
			return buttonPressRelativeLayout;
		}
	}

}
