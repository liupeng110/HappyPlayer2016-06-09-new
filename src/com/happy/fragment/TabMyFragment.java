package com.happy.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.happy.adapter.DownloadAdapter;
import com.happy.adapter.LikeSongAdapter;
import com.happy.adapter.LocalSongAdapter;
import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.iface.PageAction;
import com.happy.logger.LoggerManage;
import com.happy.model.app.Category;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.ImageUtil;
import com.happy.util.ToastUtil;
import com.happy.widget.ItemRelativeLayout;
import com.happy.widget.LinearLayoutRecyclerView;
import com.happy.widget.LinearLayoutRecyclerView.OnLinearLayoutRecyclerViewScrollListener;
import com.happy.widget.LoadRelativeLayout;
import com.happy.widget.MainTextView;
import com.happy.widget.SlideBar;
import com.happy.widget.SlideBar.OnTouchingLetterChangedListener;

@SuppressLint("ValidFragment")
public class TabMyFragment extends Fragment implements Observer {
	private View mMainView;
	private SkinInfo skinInfo;

	/**
	 * 随机播放按钮
	 */
	private ImageView RandomPlayIcon;

	/**
	 * 本地音乐
	 */
	private ImageView MusicIcon;

	/**
	 * 我的最爱
	 */
	private ImageView FavoriteIcon;
	/**
	 * 我的下载
	 */
	private ImageView DownloadIcon;

	/**
	 * 本地音乐
	 */
	private ItemRelativeLayout musicListItemRelativeLayout;
	/**
	 * 我的最爱
	 */
	private ItemRelativeLayout favoriteListItemRelativeLayout;

	/**
	 * 我的下载
	 */
	private ItemRelativeLayout downloadListItemRelativeLayout;

	/**
	 * 
	 */
	private MainTextView musicCountText;

	private int songCount = 0;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			musicCountText.setText(songCount + "首歌曲");
		}

	};

	private LoggerManage logger;
	private PageAction action;

	public TabMyFragment() {

	}

	public TabMyFragment(PageAction action) {
		this.action = action;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initComponent();
		// 初始化皮肤
		initSkin();
		// 初始化动画
		initRandomAni();

		loadSongCount();

		ObserverManage.getObserver().addObserver(this);
	}

	private void initComponent() {
		logger = LoggerManage.getZhangLogger(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater.inflate(R.layout.fragment_tab_my, null, false);

		//
		musicListItemRelativeLayout = (ItemRelativeLayout) mMainView
				.findViewById(R.id.music);
		musicListItemRelativeLayout.setOnClickListener(new ItemOnClick());

		favoriteListItemRelativeLayout = (ItemRelativeLayout) mMainView
				.findViewById(R.id.favorite);
		favoriteListItemRelativeLayout.setOnClickListener(new ItemOnClick());

		downloadListItemRelativeLayout = (ItemRelativeLayout) mMainView
				.findViewById(R.id.download);
		downloadListItemRelativeLayout.setOnClickListener(new ItemOnClick());

		//
		RandomPlayIcon = (ImageView) mMainView.findViewById(R.id.randomPlay);
		RandomPlayIcon.setOnClickListener(new ItemOnClick());

		MusicIcon = (ImageView) mMainView.findViewById(R.id.music_icon);
		FavoriteIcon = (ImageView) mMainView.findViewById(R.id.favorite_icon);
		DownloadIcon = (ImageView) mMainView.findViewById(R.id.download_icon);

		musicCountText = (MainTextView) mMainView
				.findViewById(R.id.musicCountText);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) mMainView.getParent();
		if (viewGroup != null) {
			viewGroup.removeAllViewsInLayout();
		}
		return mMainView;
	}

	private void initSkin() {
		skinInfo = Constants.skinInfo;

		ImageUtil.loadImageFormFile(RandomPlayIcon, skinInfo.getRandomPlay()
				.getNormal(), getActivity(), null);

		ImageUtil.loadImageFormFile(MusicIcon, skinInfo.getMusicIcon()
				.getNormal(), getActivity(), null);

		ImageUtil.loadImageFormFile(FavoriteIcon, skinInfo.getFavoriteIcon()
				.getNormal(), getActivity(), null);

		ImageUtil.loadImageFormFile(DownloadIcon, skinInfo.getDownloadIcon()
				.getNormal(), getActivity(), null);

		if (localSongAdapter != null) {
			localSongAdapter.notifyDataSetChanged();
		}

		if (likeSongAdapter != null) {
			likeSongAdapter.notifyDataSetChanged();
		}
		if (downloadAdapter != null) {
			downloadAdapter.notifyDataSetChanged();
		}

	}

	private void loadSongCount() {

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				songCount = SongDB.getSongInfoDB(getActivity()).getCount();
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				mHandler.sendEmptyMessage(0);
			}

		}.execute("");
	}

	class ItemOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.randomPlay:
				randomPlay();
				break;
			case R.id.music:
				music();
				break;
			case R.id.favorite:
				favorite();
				break;
			case R.id.download:
				download();
				break;
			}
		}
	}

	/**
	 * 随机播放
	 */
	public void randomPlay() {
		RandomPlayIcon.startAnimation(leftAim);

		SongMessage songMessage = new SongMessage();
		songMessage.setType(SongMessage.RANDOMMUSIC);
		ObserverManage.getObserver().setMessage(songMessage);

		ToastUtil.showTextToast(getActivity(), "乐乐随机为您点播了一首歌曲!!");
	}

	private RotateAnimation leftAim;
	private RotateAnimation rightAim;
	private RotateAnimation leftBackAim;

	public void initRandomAni() {
		leftAim = new RotateAnimation(0f, -20f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		leftAim.setFillAfter(true);
		leftAim.setDuration(300);
		leftAim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				RandomPlayIcon.clearAnimation();
				RandomPlayIcon.startAnimation(rightAim);
			}
		});

		rightAim = new RotateAnimation(-20f, 40f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rightAim.setFillAfter(true);
		rightAim.setDuration(300);
		rightAim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				RandomPlayIcon.clearAnimation();
				RandomPlayIcon.startAnimation(leftBackAim);
			}
		});

		leftBackAim = new RotateAnimation(40f, 0f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		leftBackAim.setFillAfter(true);
		leftBackAim.setDuration(300);
	}

	/**
	 * 本地音乐视图
	 */
	private View localView;
	/**
	 * 本地歌曲列表视图
	 */
	private LinearLayoutRecyclerView localPlayListview;
	/**
	 * 本地的歌曲列表
	 */
	List<Category> localPlayListSongCategorys;
	/**
	 * 本地歌曲适配器
	 */
	private LocalSongAdapter localSongAdapter;

	private LoadRelativeLayout localSongLoadRelativeLayout;
	private Handler localSongHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				localSongLoadRelativeLayout.showLoadingView();
				break;
			case -2:
				localSongLoadRelativeLayout.showSuccessView();
				break;
			default:
				if (localSongAdapter != null) {
					localSongAdapter.notifyDataSetChanged();
				}
				break;
			}
		}

	};

	private SlideBar localSlideBar;
	/**
	 * 显示字母的TextView
	 */
	private TextView localDialog;

	private LinearLayoutManager localLayoutManager;

	public void music() {
		// logger.i("本地音乐");
		if (localView == null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			localView = inflater.inflate(R.layout.fragment_local, null, false);

			localPlayListview = (LinearLayoutRecyclerView) localView
					.findViewById(R.id.localPlayListview);

			// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
			localPlayListview.setHasFixedSize(true);

			localLayoutManager = new LinearLayoutManager(getActivity());
			localPlayListview.setLinearLayoutManager(localLayoutManager);

			localSongLoadRelativeLayout = (LoadRelativeLayout) localView
					.findViewById(R.id.localsongloadRelativeLayout);
			localSongLoadRelativeLayout.init(getActivity());

			localSlideBar = (SlideBar) localView
					.findViewById(R.id.localSlideBar);

			localDialog = (TextView) localView.findViewById(R.id.localDialog);

			localSlideBar.setTextView(localDialog);

			// 设置右侧触摸监听
			localSlideBar
					.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

						@Override
						public void onTouchingLetterChanged(String s) {
							if (localSongAdapter != null) {
								// 该字母首次出现的位置
								int position = localSongAdapter
										.getPositionForSection(s.charAt(0));
								if (position != -1) {
									localPlayListview
											.move(position,
													LinearLayoutRecyclerView.smoothScroll);
								}
							}

						}
					});

			localPlayListview
					.OnLinearLayoutRecyclerViewScrollListener(new OnLinearLayoutRecyclerViewScrollListener() {

						@Override
						public void onScrollEnd(int firstIndex) {
							if (localSongAdapter != null) {
								char choose = localSongAdapter
										.getPositionForIndex(firstIndex);
								if (choose != -1) {
									localSlideBar.setChoose(choose);
								}
							}
						}
					});

			localSongHandler.sendEmptyMessage(-1);
		}
		if (localSongAdapter == null) {
			new AsyncTask<String, Integer, Void>() {

				@Override
				protected Void doInBackground(String... arg0) {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					localPlayListSongCategorys = new ArrayList<Category>();
					List<String> categoryList = SongDB.getSongInfoDB(
							getActivity()).getAllLocalSongCategory();
					for (int i = 0; i < categoryList.size(); i++) {
						String categoryName = categoryList.get(i);
						List<SongInfo> songInfos = SongDB.getSongInfoDB(
								getActivity()).getAllLocalCategorySong(
								categoryName);
						if (categoryName.equals("^")) {
							categoryName = "#";
						}
						Category category = new Category(categoryName);
						category.setmCategoryItem(songInfos);
						localPlayListSongCategorys.add(category);
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (localPlayListSongCategorys != null
							&& localPlayListSongCategorys.size() != 0) {
						localSongAdapter = new LocalSongAdapter(getActivity(),
								localPlayListSongCategorys);
						localPlayListview.setAdapter(localSongAdapter);

						if (localSongAdapter != null) {
							char choose = localSongAdapter
									.getPositionForIndex(0);
							if (choose != -1) {
								localSlideBar.setChoose(choose);
							}
						}
					}
					localSongHandler.sendEmptyMessage(-2);
				}

			}.execute("");
		}
		action.addPage(localView, "本地音乐");
	}

	/**
	 * 本地音乐视图
	 */
	private View likeView;

	private LinearLayoutRecyclerView likePlayListview;
	List<Category> likePlayListSongCategorys;
	private LikeSongAdapter likeSongAdapter;

	private LoadRelativeLayout likeSongLoadRelativeLayout;
	private Handler likeSongHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				likeSongLoadRelativeLayout.showLoadingView();
				break;
			case 1:
				likeSongLoadRelativeLayout.showSuccessView();
				break;
			case 2:
				likeSongLoadRelativeLayout.showNoResultView();
				break;
			}
		}

	};

	private SlideBar likeSlideBar;
	/**
	 * 显示字母的TextView
	 */
	private TextView likeDialog;

	/**
	 * 我的最爱
	 */
	public void favorite() {
		if (likeView == null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			likeView = inflater.inflate(R.layout.fragment_mylove, null, false);

			likePlayListview = (LinearLayoutRecyclerView) likeView
					.findViewById(R.id.likePlayListview);
			// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
			likePlayListview.setHasFixedSize(true);

			likePlayListview.setLinearLayoutManager(new LinearLayoutManager(
					getActivity()));

			likeSongLoadRelativeLayout = (LoadRelativeLayout) likeView
					.findViewById(R.id.likesongloadRelativeLayout);
			likeSongLoadRelativeLayout.init(getActivity());

			likeSlideBar = (SlideBar) likeView.findViewById(R.id.likeSlideBar);

			likeDialog = (TextView) likeView.findViewById(R.id.likeDialog);

			likeSlideBar.setTextView(likeDialog);

			// 设置右侧触摸监听
			likeSlideBar
					.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

						@Override
						public void onTouchingLetterChanged(String s) {
							if (likeSongAdapter != null) {
								// 该字母首次出现的位置
								int position = likeSongAdapter
										.getPositionForSection(s.charAt(0));
								if (position != -1) {
									likePlayListview
											.move(position,
													LinearLayoutRecyclerView.smoothScroll);
								}
							}

						}
					});

			likePlayListview
					.OnLinearLayoutRecyclerViewScrollListener(new OnLinearLayoutRecyclerViewScrollListener() {

						@Override
						public void onScrollEnd(int firstIndex) {
							if (likeSongAdapter != null) {
								char choose = likeSongAdapter
										.getPositionForIndex(firstIndex);
								if (choose != -1) {
									likeSlideBar.setChoose(choose);
								}
							}
						}
					});

			likeSongHandler.sendEmptyMessage(0);
		}

		if (likeSongAdapter == null) {
			new AsyncTask<String, Integer, Void>() {

				@Override
				protected Void doInBackground(String... arg0) {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					likePlayListSongCategorys = new ArrayList<Category>();
					List<String> categoryList = SongDB.getSongInfoDB(
							getActivity()).getAllLikeCategory();
					for (int i = 0; i < categoryList.size(); i++) {
						String categoryName = categoryList.get(i);
						List<SongInfo> songInfos = SongDB.getSongInfoDB(
								getActivity()).getAllLikeCategorySong(
								categoryName);
						if (categoryName.equals("^")) {
							categoryName = "#";
						}
						Category category = new Category(categoryName);
						category.setmCategoryItem(songInfos);
						likePlayListSongCategorys.add(category);
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (likePlayListSongCategorys != null
							&& likePlayListSongCategorys.size() != 0) {
						likeSongAdapter = new LikeSongAdapter(getActivity(),
								likePlayListSongCategorys);
						likePlayListview.setAdapter(likeSongAdapter);

						if (likeSongAdapter != null) {
							char choose = likeSongAdapter
									.getPositionForIndex(0);
							if (choose != -1) {
								likeSlideBar.setChoose(choose);
							}
						}

						likeSongHandler.sendEmptyMessage(1);
					} else {
						likeSongHandler.sendEmptyMessage(2);
					}
				}

			}.execute("");
		}
		action.addPage(likeView, "我的最爱");
	}

	View downloadView = null;
	private LinearLayoutRecyclerView downloadListview;
	List<Category> downloadListSongCategorys;
	private DownloadAdapter downloadAdapter;
	private LoadRelativeLayout downloadSongLoadRelativeLayout;
	private Handler downloadSongHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				downloadSongLoadRelativeLayout.showLoadingView();
				break;
			case 1:
				downloadSongLoadRelativeLayout.showSuccessView();
				break;
			case 2:
				downloadSongLoadRelativeLayout.showNoResultView();
				break;
			}
		}

	};

	/**
	 * 我的下载
	 */
	public void download() {
		if (downloadView == null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			downloadView = inflater.inflate(R.layout.fragment_mydownload, null,
					false);

			downloadListview = (LinearLayoutRecyclerView) downloadView
					.findViewById(R.id.downloadListview);
			// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
			downloadListview.setHasFixedSize(true);

			downloadListview.setLinearLayoutManager(new LinearLayoutManager(
					getActivity()));

			downloadSongLoadRelativeLayout = (LoadRelativeLayout) downloadView
					.findViewById(R.id.downloadsongloadRelativeLayout);
			downloadSongLoadRelativeLayout.init(getActivity());
			downloadSongHandler.sendEmptyMessage(0);

			if (downloadAdapter == null) {
				new AsyncTask<String, Integer, Void>() {

					@Override
					protected Void doInBackground(String... arg0) {

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						downloadListSongCategorys = new ArrayList<Category>();
						String[] categoryList = { "正在下载", "已下载" };
						int[] status = { SongInfo.DOWNLOADING,
								SongInfo.DOWNLOADED };
						for (int i = 0; i < categoryList.length; i++) {
							String categoryName = categoryList[i];
							List<SongInfo> songInfos = SongDB.getSongInfoDB(
									getActivity()).getDownloadSong(status[i]);
							Category category = new Category(categoryName);
							category.setmCategoryItem(songInfos);
							downloadListSongCategorys.add(category);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if (downloadListSongCategorys != null
								&& downloadListSongCategorys.size() != 0) {
							downloadAdapter = new DownloadAdapter(
									getActivity(), downloadListSongCategorys);
							downloadListview.setAdapter(downloadAdapter);
							downloadSongHandler.sendEmptyMessage(1);
						} else {
							downloadSongHandler.sendEmptyMessage(2);
						}
					}

				}.execute("");
			}
		}
		action.addPage(downloadView, "我的下载");
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof SkinThemeApp) {
			initSkin();
		} else if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.ADDMUSIC) {
				songCount++;
				mHandler.sendEmptyMessage(0);
				if (songMessage.getSongInfo() != null) {
					addMUSIC(songMessage.getSongInfo());
				}
			} else if (songMessage.getType() == SongMessage.LOCALDELMUSIC) {
				songCount--;
				if (songCount < 0) {
					songCount = 0;
				}
				mHandler.sendEmptyMessage(0);
			} else if (songMessage.getType() == SongMessage.SCANEDMUSIC) {
				localSongHandler.sendEmptyMessage(0);
			}
		}
	}

	/**
	 * 添加歌曲
	 * 
	 * @param songInfo
	 */
	private void addMUSIC(SongInfo songInfo) {
		if (localPlayListSongCategorys == null) {
			return;
		}
		// int count = 0;
		for (int j = 0; j < localPlayListSongCategorys.size(); j++) {
			Category category = localPlayListSongCategorys.get(j);
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
					localPlayListSongCategorys.remove(j);
					localPlayListSongCategorys.add(j, categoryTemp);

					// localSongHandler.sendEmptyMessage(count);
					// localSongHandler.sendEmptyMessage(count + 1);
					//
					// localSongAdapter.notifyItemInserted(count);
					// localSongAdapter.notifyItemInserted(count + 1);

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
							localPlayListSongCategorys.remove(j);
							localPlayListSongCategorys.add(j, categoryTemp);
							//
							// localSongAdapter.notifyItemInserted(count);

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
							localPlayListSongCategorys.remove(j);
							localPlayListSongCategorys.add(j, categoryTemp);
							//
							// localSongAdapter.notifyItemInserted(count);

							// localSongHandler.sendEmptyMessage(count + i);

							break;
						}
					}
				}

				break;

			} else if (categoryChar < tempCategory
					|| j == localPlayListSongCategorys.size() - 1) {

				if (categoryChar == '^') {
					categoryChar = '#';
				}
				Category categoryTemp = new Category(categoryChar + "");
				List<SongInfo> lists = new ArrayList<SongInfo>();
				lists.add(songInfo);
				categoryTemp.setmCategoryItem(lists);
				localPlayListSongCategorys.add(j, categoryTemp);

				// localSongAdapter.notifyItemInserted(count);
				// localSongAdapter.notifyItemInserted(count + 1);

				// localSongHandler.sendEmptyMessage(count);

				// localSongHandler.sendEmptyMessage(count + 1);

				break;

			}
			// count += category.getItemCount();
		}
		// localSongAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		ObserverManage.getObserver().deleteObserver(this);
		// if (adapter != null)
		// adapter.finish();
		super.onDestroy();
	}

}
