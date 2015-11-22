package com.happy.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.widget.ImageView;

import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.model.app.HttpResult;
import com.happy.model.app.SongMessage;
import com.happy.model.pc.SingerPhoto;
import com.happy.observable.ObserverManage;

/**
 * 歌手写真图片处理
 * 
 * @author zhangliangming
 * 
 */
public class SingerPhotoUtil {
	/**
	 * 图片索引
	 */
	public static int index = 0;
	/**
	 * 歌手写真图片
	 */
	public static Drawable[] singerPhotoDrawable = null;
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
	 * 加载歌手写真图片
	 * 
	 * @param context
	 * @param artistImageView
	 *            歌手写真图片视图
	 * @param sid
	 *            歌曲id
	 * @param singerPIC
	 *            歌手写真路径id
	 * @param singer
	 *            歌手名称
	 */
	@SuppressLint("NewApi")
	public static void loadSingerPhotoImage(Context context,
			ImageView artistImageView, String sid, String singerPIC,
			String singer) {
		// 2判断singer/data.xml文件是否存在
		// 3如果存在，则解析该文件，以获取歌手的3张写真图片路径
		// 1singerPIC歌手写真下载路径不为空，则直接5
		// 4获取歌手写真图片路径后
		// 5先从内存获取图片
		// 6内存不存在，则从本地文件里获取图片
		// 7本地文件不存在，则从网络服务器获取
		// 8保存图片到本地文件
		singerPhotoDrawable = new Drawable[3];
		artistImageView.setBackground(new BitmapDrawable());
		String xmlFilePath = Constants.PATH_ARTIST + File.separator + singer
				+ File.separator + "data.xml";
		File xmlFile = new File(xmlFilePath);
		// 本地不存在文件
		if (!xmlFile.exists()) {
			if (singerPIC == null || singerPIC.equals("")) {
				// 从服务器获取图片数据信息
				loadImageFormNet(context, artistImageView, sid, singer);
			} else {
				// 加载图片数据
				loadImage(context, artistImageView, sid, singerPIC, singer);
			}
		} else {
			// 解析xml文件数据，获取歌手的3张写真图片路径
			loadImageFormXmlFile(context, xmlFile, artistImageView, sid, singer);
		}
	}

	/**
	 * 从xml文件里面获取写真图片数据
	 * 
	 * @param context
	 * @param xmlFile
	 * @param artistImageView
	 * @param sid
	 * @param singerPIC
	 * @param singer
	 */
	private static void loadImageFormXmlFile(Context context, File xmlFile,
			ImageView artistImageView, String sid, String singer) {
		String singerPIC = "";
		try {
			XmlPullParser parser = Xml.newPullParser();
			InputStream inputStream = new FileInputStream(xmlFile);
			parser.setInput(inputStream, "UTF-8"); // 设置输入流 并指明编码方式
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("data")) {

						singerPIC = parser.getAttributeValue("", "sid");

						eventType = parser.next();
					}
					break;
				case XmlPullParser.END_TAG:

					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadImage(context, artistImageView, sid, singerPIC, singer);
	}

	/**
	 * 从网络获取图片
	 * 
	 * @param context
	 * @param artistImageView
	 * @param sid
	 * @param singerPIC
	 * @param singer
	 */
	private static void loadImageFormNet(final Context context,
			final ImageView artistImageView, final String sid,
			final String singer) {
		new AsyncTask<String, Integer, HttpResult<SingerPhoto>>() {

			@Override
			protected HttpResult<SingerPhoto> doInBackground(String... arg0) {
				return HttpUtil.getSingerPhotoBySinger(context, singer);
			}

			@Override
			protected void onPostExecute(HttpResult<SingerPhoto> httpResult) {
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					List<SingerPhoto> lists = httpResult.getModels();
					SingerPhoto singerPhoto = lists.get(0);
					final String singerPIC = singerPhoto.getSid();

					new Thread() {

						@Override
						public void run() {
							saveXmlFile(context, singer, singerPIC);
						}

					}.start();

					new Thread() {

						@Override
						public void run() {
							SongDB.getSongInfoDB(context).updateSongSingerPIC(
									sid, singerPIC);
						}

					}.start();

					loadImage(context, artistImageView, sid, singerPIC, singer);
				}
			}

		}.execute("");
	}

	/**
	 * 保存到xml文件
	 * 
	 * @param context
	 * @param singer
	 * @param singerPIC
	 */
	private static void saveXmlFile(Context context, String singer,
			String singerPIC) {
		String xmlFilePath = Constants.PATH_ARTIST + File.separator + singer
				+ File.separator + "data.xml";

		FileOutputStream fileos = null;

		File newXmlFile = new File(xmlFilePath);
		if (!newXmlFile.getParentFile().exists()) {
			newXmlFile.getParentFile().mkdirs();
		}
		try {

			fileos = new FileOutputStream(newXmlFile);

			// we create a XmlSerializer in order to write xml data
			XmlSerializer serializer = Xml.newSerializer();

			// we set the FileOutputStream as output for the serializer,
			// using UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");

			// <?xml version=”1.0″ encoding=”UTF-8″>
			// Write <?xml declaration with encoding (if encoding not
			// null) and standalone flag (if stan dalone not null)
			// This method can only be called just after setOutput.
			serializer.startDocument("UTF-8", null);

			// start a tag called "root"
			serializer.startTag(null, "data");

			serializer.attribute(null, "sid", singerPIC);

			serializer.endTag(null, "data");
			serializer.endDocument();

			// write xml data into the FileOutputStream
			serializer.flush();
			// finally we close the file stream
			fileos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载图片
	 * 
	 * @param context
	 * @param artistImageView
	 * @param sid
	 * @param singerPIC
	 * @param singer
	 */
	@SuppressLint("NewApi")
	private static void loadImage(final Context context,
			final ImageView artistImageView, final String sid,
			final String singerPIC, final String singer) {

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {

				Bitmap bitmap1 = getLayoutBitmap(context, sid, singer,
						singerPIC, "1");

				Bitmap bitmap2 = getLayoutBitmap(context, sid, singer,
						singerPIC, "2");
				Bitmap bitmap3 = getLayoutBitmap(context, sid, singer,
						singerPIC, "3");
				int i = 0;
				if (bitmap1 != null) {
					singerPhotoDrawable[i++] = new BitmapDrawable(bitmap1);
				}
				if (bitmap2 != null) {
					singerPhotoDrawable[i++] = new BitmapDrawable(bitmap2);
				}
				if (bitmap3 != null) {
					singerPhotoDrawable[i++] = new BitmapDrawable(bitmap3);
				}

				if (singerPhotoDrawable != null
						&& singerPhotoDrawable.length != 0) {
					SongMessage songMessage = new SongMessage();
					songMessage.setType(SongMessage.SINGERPHOTOLOADED);
					// 通知
					ObserverManage.getObserver().setMessage(songMessage);
				}

				return null;
			}

			// @Override
			protected void onPostExecute(String result) {

				// SongMessage songMessage = new SongMessage();
				// songMessage.setType(SongMessage.SINGERPHOTOLOADED);
				// // 通知
				// ObserverManage.getObserver().setMessage(songMessage);

				// if (result != null && result.length != 0) {
				//
				// TransitionDrawable mTransitionDrawable = new
				// TransitionDrawable(
				// result);
				// mTransitionDrawable.setCrossFadeEnabled(true);
				// mTransitionDrawable.startTransition(1000 * 2);
				// artistImageView.setBackgroundDrawable(mTransitionDrawable);
				// }
			}

		}.execute("");

	}

	/**
	 * 获取图层图片
	 * 
	 * @param context
	 * @param singer
	 * @param singerPIC
	 * @return
	 */
	private static Bitmap getLayoutBitmap(Context context, String sid,
			String singer, String singerPIC, String imageid) {

		Bitmap bitmap = null;

		String filePath = Constants.PATH_ARTIST + File.separator + singer
				+ File.separator + singerPIC.hashCode() + imageid.hashCode()
				+ ".jpg";

		if (sImageCache.get(filePath) != null) {
			bitmap = sImageCache.get(filePath);
		}
		if (bitmap == null) {
			bitmap = getImageFormFile(filePath, context);
		}
		if (bitmap == null) {
			String imageUrl = HttpUtil.getSingerPhotoImageByID(singerPIC,
					imageid);
			bitmap = getImageFormUrl(filePath, imageUrl, context);
		}
		if (bitmap != null) {
			if (sImageCache.get(filePath) == null) {
				sImageCache.put(filePath, bitmap);
			}
		}
		return bitmap;
	}

	/**
	 * 
	 * @param filePath
	 *            文件路径
	 * @param imageUrl
	 *            图片路径
	 * @param context
	 * @return
	 */
	protected static Bitmap getImageFormUrl(final String filePath,
			String imageUrl, Context context) {
		Bitmap bm = getBitmap(imageUrl, context);
		if (bm != null) {
			final Bitmap bitmap = bm;
			new Thread() {

				@Override
				public void run() {
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

	public static Drawable[] getSingerPhotoDrawable() {
		return singerPhotoDrawable;
	}

	public static void setSingerPhotoDrawable(Drawable[] singerPhotoDrawable) {
		SingerPhotoUtil.singerPhotoDrawable = singerPhotoDrawable;
	}

}
