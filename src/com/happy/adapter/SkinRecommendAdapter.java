package com.happy.adapter;

import java.io.File;
import java.text.DecimalFormat;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happy.adapter.SkinRecommendAdapter.ItemViewHolder;
import com.happy.common.Constants;
import com.happy.db.DownloadTaskDB;
import com.happy.db.DownloadThreadDB;
import com.happy.db.SkinThemeDB;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.DataUtil;
import com.happy.util.DateUtil;
import com.happy.util.DownloadManage;
import com.happy.util.DownloadThreadManage;
import com.happy.util.DownloadThreadPool;
import com.happy.util.DownloadThreadPool.IDownloadTaskEventCallBack;
import com.happy.util.HttpUtil;
import com.happy.util.ImageLoadUtil;
import com.happy.util.ToastUtil;
import com.happy.util.UnzipUtil;
import com.happy.widget.CardViewRelativeLayout;
import com.happy.widget.MainTextView;
import com.happy.widget.SkinProgressBar;

public class SkinRecommendAdapter extends Adapter<ItemViewHolder> implements
		Observer {
	private List<DownloadTask> datas;
	private static Context context;
	private int selectedIndex = -1;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DownloadTask task = (DownloadTask) msg.obj;
			if (task != null) {
				reshUI(task);
			}
		}

	};

	private IDownloadTaskEventCallBack eventCallBack = new IDownloadTaskEventCallBack() {

		@Override
		public void waiting(DownloadTask task) {
			task.setStatus(DownloadTask.WAITING);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
			ObserverManage.getObserver().setMessage(task);
			if (DownloadTaskDB.getDownloadTaskDB(context).taskIsExists(
					task.getTid(), DownloadTask.SKIN)) {
				DownloadTaskDB.getDownloadTaskDB(context).update(task);
			}
		}

		@Override
		public void threadDownloading(DownloadTask task, int downloadSize,
				int threadIndex, int threadNum, int startIndex, int endIndex) {

		}

		@Override
		public void pauseed(DownloadTask task, int downloadedSize) {
			task.setStatus(DownloadTask.DOWNLOAD_PAUSE);
			task.setDownloadedSize(downloadedSize);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void finished(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_FINISH);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
			ObserverManage.getObserver().setMessage(task);
			if (DownloadTaskDB.getDownloadTaskDB(context).taskIsExists(
					task.getTid(), DownloadTask.SKIN)) {
				DownloadTaskDB.getDownloadTaskDB(context).update(task);
			}
		}

		@Override
		public void error(DownloadTask task) {
			ObserverManage.getObserver().setMessage(task);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
		}

		@Override
		public void downloading(DownloadTask task, int downloadedSize) {
			task.setStatus(DownloadTask.DOWNLOING);
			task.setDownloadedSize(downloadedSize);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void canceled(DownloadTask task) {
			task.setStatus(DownloadTask.DOWNLOAD_CANCEL);
			// Message msg = new Message();
			// msg.obj = task;
			// mHandler.sendMessage(msg);
			ObserverManage.getObserver().setMessage(task);
		}

		@Override
		public void cancelWaiting(DownloadTask task) {
		}
	};

	public SkinRecommendAdapter(Context context, List<DownloadTask> datas) {
		this.context = context;
		this.datas = datas;
		// selectedIndex = findOldSelectedIndex();
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
				DownloadTask downloadTask = datas.get(i);
				if (downloadTask.getTid().equals(Constants.skinID)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(final ItemViewHolder itemViewHolder,
			final int position) {
		final DownloadTask downloadTask = datas.get(position);
		String sid = downloadTask.getTid();
		String imageUrl = HttpUtil.getSkinThemePreviewImageByID(sid);
		ImageLoadUtil.loadImageFormUrl(imageUrl, itemViewHolder.getImavPic(),
				R.drawable.picture_manager_default, true);
		itemViewHolder.getMainTextView().setText(downloadTask.gettName() + "");
		itemViewHolder.getSkinSizeTextView().setText(
				getFileSize(downloadTask.getFileSize()) + "");

		itemViewHolder.getSkinProgressBar().setMax(
				(int) downloadTask.getFileSize());
		itemViewHolder.getSkinProgressBar().setProgress(
				(int) downloadTask.getDownloadedSize());

		reshViewHolder(position, itemViewHolder, downloadTask);

	}

	/**
	 * 加载皮肤
	 * 
	 * @param position
	 * 
	 * @param downloadTask
	 * @param itemViewHolder
	 */
	protected void loadSkin(int position, DownloadTask downloadTask,
			ItemViewHolder itemViewHolder) {
		selectedIndex = findOldSelectedIndex();
		if (selectedIndex != -1) {
			reshPICStatusUI(selectedIndex);
		}
		// 设置当前皮肤主题的id和保存，并通知各个页面去加载新的皮肤
		Constants.skinID = downloadTask.getTid();
		DataUtil.saveValue(context, Constants.skinID_KEY, Constants.skinID);

		SkinThemeApp skinTheme = SkinThemeDB.getSkinThemeDB(context)
				.getSkinThemeInfo(Constants.skinID);
		if (skinTheme != null) {
			DataUtil.loadSkin(context);
			ObserverManage.getObserver().setMessage(skinTheme);
		}
		selectedIndex = position;
		reshPICStatusUI(selectedIndex);
	}

	/**
	 * 刷新界面
	 * 
	 * @param oldSelectedIndex
	 */
	protected void reshPICStatusUI(int oldSelectedIndex) {
		this.notifyItemChanged(oldSelectedIndex);
	}

	/**
	 * 下载皮肤
	 * 
	 * @param position
	 * 
	 * @param downloadTask
	 */
	protected void downloadSkin(int position, DownloadTask downloadTask) {

		DownloadThreadManage dtm = new DownloadThreadManage(downloadTask, 10,
				100);
		downloadTask.setDownloadThreadManage(dtm);
		DownloadThreadPool dp = DownloadManage.getSkinTM(context);
		dp.setEvent(eventCallBack);
		dp.addDownloadTask(downloadTask);

	}

	/**
	 * 刷新ui
	 * 
	 * @param position
	 * 
	 * @param itemViewHolder
	 * @param downloadTask
	 */
	private void reshViewHolder(final int position,
			final ItemViewHolder itemViewHolder, final DownloadTask downloadTask) {
		if (SkinThemeDB.getSkinThemeDB(context).skinThemeIsExists(
				downloadTask.getTid())) {
			// itemViewHolder.getDownloadstatusRelativeLayout().setVisibility(
			// View.INVISIBLE);
			// itemViewHolder.getSkinProgressBar().setVisibility(View.INVISIBLE);
			if (!downloadTask.getTid().equals(Constants.skinID)) {
				itemViewHolder.getselectImageView().setVisibility(
						View.INVISIBLE);

				itemViewHolder.getItembg().setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								// 加载皮肤数据
								loadSkin(position, downloadTask, itemViewHolder);

							}
						});

			} else {
				itemViewHolder.getItembg().setOnClickListener(null);
				// selectedIndex = position;
				itemViewHolder.getselectImageView().setVisibility(View.VISIBLE);
			}
			itemViewHolder.getSkinProgressBar().setVisibility(View.INVISIBLE);
			itemViewHolder.getDownloadstatusRelativeLayout().setVisibility(
					View.INVISIBLE);
		} else {
			itemViewHolder.getselectImageView().setVisibility(View.INVISIBLE);
			if (downloadTask.getStatus() == DownloadTask.DOWNLOAD_FINISH) {
				itemViewHolder.getSkinProgressBar().setVisibility(
						View.INVISIBLE);
				itemViewHolder.getDownloadstatusRelativeLayout().setVisibility(
						View.INVISIBLE);

				itemViewHolder.getItembg().setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// 加载皮肤数据
								loadSkin(position, downloadTask, itemViewHolder);
							}
						});

			} else if (downloadTask.getStatus() == DownloadTask.DOWNLOING
					|| downloadTask.getStatus() == DownloadTask.WAITING) {
				itemViewHolder.getSkinProgressBar().setVisibility(View.VISIBLE);
				itemViewHolder.getDownloadstatusRelativeLayout().setVisibility(
						View.INVISIBLE);
				itemViewHolder.getItembg().setOnClickListener(null);
			} else {
				itemViewHolder.getSkinProgressBar().setVisibility(
						View.INVISIBLE);
				itemViewHolder.getDownloadstatusRelativeLayout().setVisibility(
						View.VISIBLE);

				itemViewHolder.getItembg().setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// 下载皮肤
								downloadSkin(position, downloadTask);
							}
						});
			}
		}
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof DownloadTask) {
			DownloadTask task = (DownloadTask) data;
			if (task.getType() == DownloadTask.SKIN) {
				Message msg = new Message();
				msg.obj = task;
				mHandler.sendMessage(msg);
				// reshUI(task);
			}
		} else if (data instanceof SkinThemeApp) {
			SkinThemeApp skinTheme = (SkinThemeApp) data;
			if (selectedIndex != -1) {
				reshPICStatusUI(selectedIndex);
			}
			for (int i = 0; i < datas.size(); i++) {
				if (skinTheme.getID().equals(datas.get(i).getTid())) {
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

	/**
	 * 刷新ui
	 * 
	 * @param task
	 */
	private void reshUI(final DownloadTask task) {
		if (task.getStatus() == DownloadTask.DOWNLOAD_FINISH) {
			new Thread() {

				@Override
				public void run() {
					boolean isExists = SkinThemeDB.getSkinThemeDB(context)
							.skinThemeIsExists(task.getTid());
					if (!isExists) {

						// 往皮肤数据库添加数据
						SkinThemeApp skinTheme = new SkinThemeApp();
						skinTheme.setID(task.getTid());
						skinTheme.setThemeName(task.gettName());
						skinTheme.setAssetsType(SkinThemeApp.NET);
						skinTheme.setAddTime(DateUtil.dateToString(new Date()));
						skinTheme.setDownloadPath(task.getFilePath());

						// 解压皮肤文件
						String path = context.getFilesDir().getParent()
								+ File.separator + "files";
						String outputDirectory = path + File.separator + "skin"
								+ File.separator + task.getTid();

						String zipPath = task.getFilePath();
						String zipName = task.getTid();

						skinTheme.setUnZipPath(outputDirectory);

						SkinThemeDB.getSkinThemeDB(context).add(skinTheme);

						UnzipUtil.unZip(context, zipName, zipPath,
								outputDirectory);

						Message msg = new Message();
						msg.obj = skinTheme;
						ObserverManage.getObserver().setMessage(msg);
					}
				}

			}.start();

			ToastUtil.showText("皮肤: " + task.gettName() + " 下载完成");
		}

		for (int i = 0; i < datas.size(); i++) {
			DownloadTask temp = datas.get(i);
			if (task.getTid().equals(temp.getTid())) {
				datas.remove(i);
				datas.add(i, task);
				reshPICStatusUI(i);
				break;
			}
		}
	}

	/**
	 * 计算文件的大小，返回相关的m字符串
	 * 
	 * @param fileS
	 * @return
	 */
	private String getFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// 加载数据item的布局，生成VH返回
		View v = LayoutInflater.from(context).inflate(
				R.layout.listview_item_skin_image, viewGroup, false);
		return new ItemViewHolder(v);
	}

	// 可复用的VH
	class ItemViewHolder extends ViewHolder {
		// 大图
		private ImageView imavPic;

		private CardViewRelativeLayout itembg;

		private ImageView selectImageView;

		private MainTextView themeName;

		private RelativeLayout downloadstatusRelativeLayout;

		private TextView skinSizeTextView;

		private SkinProgressBar skinProgressBar;

		private View itemView;

		public ItemViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
		}

		public ImageView getImavPic() {
			if (imavPic == null) {
				imavPic = (ImageView) itemView.findViewById(R.id.theme_pic);
			}
			return imavPic;
		}

		public CardViewRelativeLayout getItembg() {
			if (itembg == null) {
				itembg = (CardViewRelativeLayout) itemView
						.findViewById(R.id.itembg);
			}

			return itembg;
		}

		public ImageView getselectImageView() {
			if (selectImageView == null) {
				selectImageView = (ImageView) itemView
						.findViewById(R.id.secect_stats);
			}
			return selectImageView;
		}

		public MainTextView getMainTextView() {
			if (themeName == null) {
				themeName = (MainTextView) itemView
						.findViewById(R.id.theme_text);
			}
			return themeName;
		}

		public RelativeLayout getDownloadstatusRelativeLayout() {
			if (downloadstatusRelativeLayout == null) {
				downloadstatusRelativeLayout = (RelativeLayout) itemView
						.findViewById(R.id.downloadstatus);
			}
			return downloadstatusRelativeLayout;
		}

		public TextView getSkinSizeTextView() {
			if (skinSizeTextView == null) {
				skinSizeTextView = (TextView) itemView
						.findViewById(R.id.skinSize);
			}
			return skinSizeTextView;
		}

		public SkinProgressBar getSkinProgressBar() {
			if (skinProgressBar == null) {
				skinProgressBar = (SkinProgressBar) itemView
						.findViewById(R.id.skinProgressBar);
			}
			return skinProgressBar;
		}

	}

	public void finish() {
		ObserverManage.getObserver().deleteObserver(this);
	}
}
