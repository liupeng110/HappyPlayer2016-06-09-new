package com.happy.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.eva.views.RoundedImageView;
import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.model.app.HttpResult;
import com.happy.model.app.SongMessage;
import com.happy.model.pc.SingerAvatar;
import com.happy.observable.ObserverManage;

/**
 * 加载歌曲专辑图片
 * 
 * @author zhangliangming
 * 
 */
public class AlbumUtil {

	// 缓存
	public static LruCache<String, Bitmap> sImageCache = getImageCache();

	/**
	 * 初始化图片内存
	 */
	private static LruCache<String, Bitmap> getImageCache() {
		// 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		// 给LruCache分配1/8 4M
		LruCache<String, Bitmap> sImageCache = new LruCache<String, Bitmap>(
				mCacheSize) {

			// 必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		return sImageCache;
	}

	/**
	 * 歌手专辑图片
	 */
	public static String mSinger = "";

	/**
	 * 加载歌手图片
	 * 
	 * @param context
	 * @param albumRoundedImageView
	 * @param defResourceID
	 *            默认图片id
	 * @param sid
	 *            歌曲sid
	 * @param albumID
	 *            歌曲歌手写真
	 * @param singer
	 *            歌手名称
	 */
	public static void loadAlbumImage(final Context context,
			final RoundedImageView albumRoundedImageView,
			final int defResourceID, final String sid, final String albumID,
			final String singer) {
		albumRoundedImageView.setImageResource(defResourceID);
		// // 如果albumID== null 则先通过 singer 来获取专辑图片id
		// if (albumID == null || albumID.equals("")) {
		// new AsyncTask<String, Integer, HttpResult<SingerAvatar>>() {
		//
		// @Override
		// protected HttpResult<SingerAvatar> doInBackground(
		// String... arg0) {
		// return HttpUtil.getSingerAvatarBySinger(context, singer);
		// }
		//
		// @Override
		// protected void onPostExecute(HttpResult<SingerAvatar> httpResult) {
		// int status = httpResult.getStatus();
		// if (status == HttpUtil.SUCCESS) {
		// List<SingerAvatar> lists = httpResult.getModels();
		// SingerAvatar singerAvatar = lists.get(0);
		// String albumIDTemp = singerAvatar.getSid();
		// String imageUrl = HttpUtil
		// .getSingerAvatarImageByID(albumIDTemp);
		//
		// loadImage(context, albumRoundedImageView, sid,
		// albumIDTemp, imageUrl, singer, true);
		// }
		// }
		//
		// }.execute("");
		//
		// } else {
		//
		loadImage(context, albumRoundedImageView, sid, albumID, singer);
		// }
	}

	/**
	 * 加载专辑图片
	 * 
	 * @param context
	 * @param defRoundedImageView
	 *            默认的专辑图片
	 * @param artistLoadingImageView
	 * @param rotateAnimation
	 * @param albumRoundedImageView
	 *            专辑图片
	 * @param sid
	 *            歌曲id
	 * @param albumID
	 *            专辑图片id
	 * @param singer
	 *            歌手
	 */
	public static void loadAlbumImage(final Context context,
			final RoundedImageView defRoundedImageView,
			final ImageView artistLoadingImageView,
			final Animation rotateAnimation,
			final RoundedImageView albumRoundedImageView, final String sid,
			final String albumID, final String singer) {
		// 如果歌手没有改变，就不需要修改专辑图片
		if (mSinger.equals(singer))
			return;
		mSinger = singer;

		defRoundedImageView.setVisibility(View.VISIBLE);
		artistLoadingImageView.setVisibility(View.INVISIBLE);
		albumRoundedImageView.setVisibility(View.INVISIBLE);

		// 先从内存获取图片数据，如果没有，则从本地获取，如果本地也没有，则从服务器上获取

		loadImageFormLocal(context, defRoundedImageView,
				artistLoadingImageView, rotateAnimation, albumRoundedImageView,
				sid, albumID, singer);

		// // 如果albumID== null 则先通过 singer 来获取专辑图片id
		// if (albumID == null || albumID.equals("")) {
		// new AsyncTask<String, Integer, HttpResult<SingerAvatar>>() {
		//
		// @Override
		// protected HttpResult<SingerAvatar> doInBackground(
		// String... arg0) {
		// return HttpUtil.getSingerAvatarBySinger(context, singer);
		// }
		//
		// @Override
		// protected void onPostExecute(HttpResult<SingerAvatar> httpResult) {
		// int status = httpResult.getStatus();
		// if (status == HttpUtil.SUCCESS) {
		// List<SingerAvatar> lists = httpResult.getModels();
		// SingerAvatar singerAvatar = lists.get(0);
		// String albumIDTemp = singerAvatar.getSid();
		// String imageUrl = HttpUtil
		// .getSingerAvatarImageByID(albumIDTemp);
		//
		// artistLoadingImageView.setVisibility(View.VISIBLE);
		// artistLoadingImageView.startAnimation(rotateAnimation);
		//
		// loadImage(context, defRoundedImageView,
		// artistLoadingImageView, rotateAnimation,
		// albumRoundedImageView, sid, albumIDTemp,
		// imageUrl, singer, true);
		// }
		// }
		//
		// }.execute("");
		//
		// } else {
		// artistLoadingImageView.setVisibility(View.VISIBLE);
		// artistLoadingImageView.startAnimation(rotateAnimation);
		// String imageUrl = HttpUtil.getSingerAvatarImageByID(albumID);
		// loadImage(context, defRoundedImageView, artistLoadingImageView,
		// rotateAnimation, albumRoundedImageView, sid, albumID,
		// imageUrl, singer, false);
		// }

	}

	/**
	 * 从本地加载专辑图片
	 * 
	 * @param context
	 * @param defRoundedImageView
	 *            默认的专辑图片
	 * @param artistLoadingImageView
	 * @param rotateAnimation
	 * @param albumRoundedImageView
	 *            专辑图片
	 * @param sid
	 *            歌曲id
	 * @param albumID
	 *            专辑图片id
	 * @param singer
	 *            歌手
	 */
	private static void loadImageFormLocal(final Context context,
			final RoundedImageView defRoundedImageView,
			final ImageView artistLoadingImageView, Animation rotateAnimation,
			final RoundedImageView albumRoundedImageView, final String sid,
			final String albumID, final String singer) {

		if(artistLoadingImageView.getVisibility() != View.VISIBLE){
			artistLoadingImageView.setVisibility(View.VISIBLE);
			artistLoadingImageView.startAnimation(rotateAnimation);
		}

		// 先从内存里面获取图片数据
		// 如果内存里面没有图片，则从文件里面获取图片数据
		// 如果文件里面没有，则从网络上获取图片
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... arg0) {

				try {
					Thread.sleep(1 * 300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Bitmap bitmap = null;
				if (sImageCache.get(singer) != null) {
					bitmap = sImageCache.get(singer);
				}

				String filePath = Constants.PATH_ALBUM + File.separator
						+ singer + ".jpg";
				if (bitmap == null) {
					bitmap = getImageFormFile(filePath, context);
				}
				if (bitmap == null) {
					bitmap = getImageFormNetService(context, filePath, sid,
							singer, albumID, false);
				}
				return bitmap;
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					if (sImageCache.get(singer) == null) {
						sImageCache.put(singer, result);
					}
					albumRoundedImageView.setImageDrawable(new BitmapDrawable(
							result));
					albumRoundedImageView.setVisibility(View.VISIBLE);
					
					if(artistLoadingImageView.getVisibility() != View.VISIBLE){
						artistLoadingImageView.clearAnimation();
						artistLoadingImageView.setVisibility(View.INVISIBLE);
					}
					defRoundedImageView.setVisibility(View.INVISIBLE);

				} else {
					artistLoadingImageView.clearAnimation();
					artistLoadingImageView.setVisibility(View.INVISIBLE);
					defRoundedImageView.setVisibility(View.VISIBLE);
				}
			}

		}.execute("");
	}

	/**
	 * 从服务器获取图片数据
	 * 
	 * @param context
	 * @param filePath
	 * @param sid
	 * @param singer
	 * @param albumID
	 * @param isNotifiIcon
	 *            判断是否是通知栏图标
	 * @return
	 */
	protected static Bitmap getImageFormNetService(Context context,
			String filePath, String sid, String singer, String albumID,
			boolean isNotifiIcon) {
		if (albumID == null || albumID.equals("")) {
			HttpResult<SingerAvatar> httpResult = HttpUtil
					.getSingerAvatarBySinger(context, singer);
			int status = httpResult.getStatus();
			if (status == HttpUtil.SUCCESS) {
				List<SingerAvatar> lists = httpResult.getModels();
				SingerAvatar singerAvatar = lists.get(0);
				String albumIDTemp = singerAvatar.getSid();
				String imageUrl = HttpUtil
						.getSingerAvatarImageByID(albumIDTemp);

				updateDB(context, sid, albumIDTemp, true);
				return getImageFormUrl(sid, filePath, imageUrl, context,
						isNotifiIcon);
			}
		} else {
			String imageUrl = HttpUtil.getSingerAvatarImageByID(albumID);
			return getImageFormUrl(sid, filePath, imageUrl, context,
					isNotifiIcon);
		}

		return null;
	}

	/**
	 * 加载图片
	 * 
	 * @param context
	 * @param defRoundedImageView
	 *            默认的专辑图片
	 * @param artistLoadingImageView
	 * @param rotateAnimation
	 * @param albumRoundedImageView
	 *            专辑图片
	 * @param sid
	 *            歌曲id
	 * @param albumID
	 *            专辑图片id
	 * @param imageUrl
	 *            专辑图片路径
	 * @param singer
	 */
	// protected static void loadImage(final Context context,
	// final RoundedImageView defRoundedImageView,
	// final ImageView artistLoadingImageView,
	// final Animation rotateAnimation,
	// final RoundedImageView albumRoundedImageView, final String sid,
	// final String albumID, final String imageUrl, final String singer,
	// final boolean updateAlbumID) {
	// // 先从内存里面获取图片数据
	// // 如果内存里面没有图片，则从文件里面获取图片数据
	// // 如果文件里面没有，则从网络上获取图片
	// new AsyncTask<String, Integer, Bitmap>() {
	//
	// @Override
	// protected Bitmap doInBackground(String... arg0) {
	//
	// try {
	// Thread.sleep(1 * 300);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// Bitmap bitmap = null;
	// if (sImageCache.get(singer) != null) {
	// bitmap = sImageCache.get(singer);
	// }
	//
	// String filePath = Constants.PATH_ALBUM + File.separator
	// + singer + ".jpg";
	// if (bitmap == null) {
	// bitmap = getImageFormFile(filePath, context);
	// }
	// if (bitmap == null) {
	// bitmap = getImageFormUrl(filePath, imageUrl, context);
	// }
	// return bitmap;
	// }
	//
	// @SuppressLint("NewApi")
	// @Override
	// protected void onPostExecute(Bitmap result) {
	// if (result != null) {
	// if (sImageCache.get(singer) != null) {
	// sImageCache.put(singer, result);
	// }
	//
	// new Thread() {
	//
	// @Override
	// public void run() {
	// if (albumID != null && !albumID.equals("")) {
	// updateDB(context, sid, albumID, updateAlbumID);
	// }
	// }
	// }.start();
	//
	// albumRoundedImageView.setImageDrawable(new BitmapDrawable(
	// result));
	//
	// artistLoadingImageView.clearAnimation();
	// artistLoadingImageView.setVisibility(View.INVISIBLE);
	// defRoundedImageView.setVisibility(View.INVISIBLE);
	// albumRoundedImageView.setVisibility(View.VISIBLE);
	//
	// }
	// }
	//
	// }.execute("");
	// }

	/**
	 * 加载歌手图片
	 * 
	 * @param context
	 * @param albumRoundedImageView
	 * @param sid
	 *            歌曲id
	 * @param albumID
	 *            歌手写真
	 * @param imageUrl
	 *            歌手图片路径
	 * @param singer
	 *            歌手名称
	 * @param updateAlbumID
	 *            是否要更新数据库
	 */
	private static void loadImage(final Context context,
			final RoundedImageView albumRoundedImageView, final String sid,
			final String albumID, final String singer) {
		// 先从内存里面获取图片数据
		// 如果内存里面没有图片，则从文件里面获取图片数据
		// 如果文件里面没有，则从网络上获取图片
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... arg0) {

				try {
					Thread.sleep(1 * 500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Bitmap bitmap = null;
				if (sImageCache.get(singer) != null) {
					bitmap = sImageCache.get(singer);
				}

				String filePath = Constants.PATH_ALBUM + File.separator
						+ singer + ".jpg";
				if (bitmap == null) {
					bitmap = getImageFormFile(filePath, context);
				}
				if (bitmap == null) {
					bitmap = getImageFormNetService(context, filePath, sid,
							singer, albumID, false);
				}
				return bitmap;
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					if (sImageCache.get(singer) == null) {
						sImageCache.put(singer, result);
					}

					albumRoundedImageView.setImageDrawable(new BitmapDrawable(
							result));
				}
			}

		}.execute("");
	}

	/**
	 * 加载通知栏图标图片
	 * 
	 * @param context
	 * @param sid
	 * @param albumID
	 * @param singer
	 * @return
	 */
	public static Bitmap getNotifiIcon(final Context context, final String sid,
			final String albumID, final String singer) {
		Bitmap bitmap = null;
		if (sImageCache.get(singer) != null) {
			bitmap = sImageCache.get(singer);
		}

		final String filePath = Constants.PATH_ALBUM + File.separator + singer
				+ ".jpg";
		if (bitmap == null) {
			bitmap = getImageFormFile(filePath, context);
		}
		if (bitmap == null) {
			new Thread() {
				@Override
				public void run() {
					getImageFormNetService(context, filePath, sid, singer,
							albumID, true);
				}
			}.start();
		}
		return bitmap;
	}

	/**
	 * 更新数据库数据
	 * 
	 * @param context
	 * @param sid
	 * @param albumID
	 */
	private static void updateDB(Context context, String sid, String albumID,
			boolean updateAlbumID) {
		if (updateAlbumID)
			SongDB.getSongInfoDB(context).updateSongAlbumUrl(sid, albumID);
	}

	/**
	 * 
	 * @param filePath
	 *            文件路径
	 * @param imageUrl
	 *            图片路径
	 * @param context
	 * @param isNotifiIcon
	 * @return
	 */
	protected static Bitmap getImageFormUrl(final String sid,
			final String filePath, String imageUrl, final Context context,
			final boolean isNotifiIcon) {
		Bitmap bm = getBitmap(imageUrl, context);
		if (bm != null) {
			final Bitmap bitmap = bm;
			new Thread() {

				@Override
				public void run() {

					if (isNotifiIcon) {
						//
						SongMessage songMessage = new SongMessage();
						songMessage.setSid(sid);
						songMessage.setType(SongMessage.ALUBMPHOTOLOADED);
						// 通知
						ObserverManage.getObserver().setMessage(songMessage);

					}
					saveImage(bitmap, filePath);
				}
			}.start();
		}

		return bm;
	}

	/**
	 * 保存图片到本地
	 * 
	 * @param bm
	 * @param isbig
	 * @param fileName
	 */
	public static void saveImage(Bitmap bm, String filePath) {
		if (bm == null) {
			return;
		}
		try {
			// 你要存放的文件
			File file = new File(filePath);
			// file文件的上一层文件夹
			File parentFile = new File(file.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream outStream = new FileOutputStream(file);
			// //10 是压缩率，表示压缩90%; 如果不压缩是100，表示压缩率为0
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	private static Bitmap getBitmap(String imageUri, Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		int displaypixels = screenWidth * screenHeight;

		// 显示网络上的图片
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			byte[] bytes = getBytes(is);
			// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
			// end
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory
					.decodeByteArray(bytes, 0, bytes.length, opts);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 数据流转成btyle[]数组
	 * */
	private static byte[] getBytes(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[2048];
		int len = 0;
		try {
			while ((len = is.read(b, 0, 2048)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	/**
	 * 从文件中获取图片
	 */
	private static Bitmap getImageFormFile(String filePath, Context context) {
		File imageFile = new File(filePath);
		if (!imageFile.exists()) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);

		/** 这里是获取手机屏幕的分辨率用来处理 图片 溢出问题的。begin */
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int displaypixels = dm.widthPixels * dm.heightPixels;

		opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		opts.inJustDecodeBounds = false;
		try {
			return BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
		return null;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
