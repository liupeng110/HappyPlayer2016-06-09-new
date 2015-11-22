package com.happy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.View;

public class ImageUtil {

	private static ExecutorService SINGLE_TASK_EXECUTOR;
	static {
		SINGLE_TASK_EXECUTOR = (ExecutorService) Executors
				.newSingleThreadExecutor();
	};

	public interface ImageLoadCallBack {
		public void callback();
	}

	// 缓存
	public static LruCache<String, Bitmap> sImageCache = getImageCache();

	/**
	 * 清空缓存
	 */
	public static void cleanSoftReference() {
		sImageCache = getImageCache();
	}

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
	 * 加载文件图片
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 * @param imcallBack
	 *            下载完成图片后，要执行的方法
	 * @return
	 */
	@SuppressLint("NewApi")
	public static void loadImageFormFile(final View view,
			final String filePath, final Context context,
			final ImageLoadCallBack imcallBack) {
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap bitmap = null;
				if (sImageCache.get(filePath) != null) {
					bitmap = sImageCache.get(filePath);
				}
				if (bitmap == null) {
					bitmap = getImageFormFile(filePath, context);
				}
				return bitmap;
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					view.setBackground(new BitmapDrawable(result));
					if (sImageCache.get(filePath) != null) {
						sImageCache.put(filePath, result);
					}
				}
				if (imcallBack != null)
					imcallBack.callback();
			}

		}.executeOnExecutor(SINGLE_TASK_EXECUTOR, "");

	}

	/**
	 * 加载图片
	 * 
	 * @param view
	 * @param filePath
	 * @param context
	 * @param resid
	 *            默认的资源图片id
	 */
	@SuppressLint("NewApi")
	public static void loadImageFormFile(final View view,
			final String filePath, final Context context, final int resid) {
		view.setBackgroundResource(resid);
		new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap bitmap = null;
				if (sImageCache.get(filePath) != null) {
					bitmap = sImageCache.get(filePath);
				}
				if (bitmap == null) {
					bitmap = getImageFormFile(filePath, context);
				}
				return bitmap;
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					view.setBackground(new BitmapDrawable(result));
					if (sImageCache.get(filePath) != null) {
						sImageCache.put(filePath, result);
					}
				} else {
					view.setBackgroundResource(resid);
				}
			}

		}.executeOnExecutor(SINGLE_TASK_EXECUTOR, "");

	}

	/**
	 * 加载图片
	 * 
	 * @param view
	 * @param filePath
	 * @param context
	 * @param resid
	 *            默认的资源图片id
	 * @param url
	 *            图片下载路径
	 */
	// @SuppressLint("NewApi")
	// public static void loadImageFormUrl(final View view, final String
	// filePath,
	// final Context context, final int resid, final String url) {
	// view.setBackgroundResource(resid);
	// new AsyncTask<String, Integer, Bitmap>() {
	//
	// @Override
	// protected Bitmap doInBackground(String... params) {
	// Bitmap bitmap = null;
	// if (sImageCache.get(filePath) != null) {
	// bitmap = sImageCache.get(filePath);
	// }
	// if (bitmap == null) {
	// bitmap = getImageFormFile(filePath, context);
	// }
	// if (bitmap == null) {
	// bitmap = getImageFormUrl(filePath, url, context);
	// }
	// return bitmap;
	// }
	//
	// @SuppressLint("NewApi")
	// @Override
	// protected void onPostExecute(Bitmap result) {
	// if (result != null) {
	// view.setBackground(new BitmapDrawable(result));
	// if (sImageCache.get(filePath) != null) {
	// sImageCache.put(filePath, result);
	// }
	// } else {
	// view.setBackgroundResource(resid);
	// }
	// }
	//
	// }.executeOnExecutor(SINGLE_TASK_EXECUTOR, "");
	//
	// }

	/**
	 * 
	 * @param filePath
	 * @param imageUrl
	 * @return
	 */
	// protected static Bitmap getImageFormUrl(final String filePath,
	// String imageUrl, Context context) {
	// Bitmap bm = getBitmap(imageUrl, context);
	// if (bm != null) {
	// final Bitmap bitmap = bm;
	// new Thread() {
	//
	// @Override
	// public void run() {
	// saveImage(bitmap, filePath);
	// }
	// }.start();
	// }
	// return bm;
	// }

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	// private static Bitmap getBitmap(String imageUri, Context context) {
	// DisplayMetrics dm = new DisplayMetrics();
	// dm = context.getResources().getDisplayMetrics();
	// int screenWidth = dm.widthPixels;
	// int screenHeight = dm.heightPixels;
	// int displaypixels = screenWidth * screenHeight;
	//
	// // 显示网络上的图片
	// Bitmap bitmap = null;
	// BitmapFactory.Options opts = new BitmapFactory.Options();
	// try {
	// URL myFileUrl = new URL(imageUri);
	// HttpURLConnection conn = (HttpURLConnection) myFileUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream is = conn.getInputStream();
	// byte[] bytes = getBytes(is);
	// // 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
	// opts.inJustDecodeBounds = true;
	// BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
	// opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
	// // end
	// opts.inJustDecodeBounds = false;
	// bitmap = BitmapFactory
	// .decodeByteArray(bytes, 0, bytes.length, opts);
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// return null;
	// }
	// return bitmap;
	// }

	/**
	 * 数据流转成btyle[]数组
	 * */
	// private static byte[] getBytes(InputStream is) {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// byte[] b = new byte[2048];
	// int len = 0;
	// try {
	// while ((len = is.read(b, 0, 2048)) != -1) {
	// baos.write(b, 0, len);
	// baos.flush();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// byte[] bytes = baos.toByteArray();
	// return bytes;
	// }

	/**
	 * 预加载图片
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 */
	// public static void preloadingImageFormFile(final String filePath,
	// final Context context) {
	//
	// // CrashApplication.bitmap = null;
	// Bitmap bitmap = null;
	// if (sImageCache.get(filePath) != null) {
	// bitmap = sImageCache.get(filePath);
	// }
	// if (bitmap == null) {
	// bitmap = getImageFormFile(filePath, context);
	// }
	// if (bitmap != null) {
	// if (sImageCache.get(filePath) != null) {
	// sImageCache.put(filePath, bitmap);
	// // CrashApplication.bitmap = bitmap;
	// }
	// }
	// ImageLoadUtil.loadImageFormFile(filePath, null, 0, false);
	// }

	/**
	 * 加载图片
	 * 
	 * @param filePath
	 * @param context
	 * @return
	 */
	public static Bitmap loadImageFormFile(String filePath, Context context) {
		Bitmap bitmap = null;
		if (sImageCache.get(filePath) != null) {
			bitmap = sImageCache.get(filePath);
		}
		if (bitmap == null) {
			bitmap = getImageFormFile(filePath, context);
		}
		if (bitmap != null) {
			sImageCache.put(filePath, bitmap);
		}
		return bitmap;
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
