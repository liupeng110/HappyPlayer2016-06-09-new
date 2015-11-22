package com.happy.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.happy.ui.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoadUtil {
	/**
	 * 4.如果经常出现OOM（别人那边看到的，觉得很有提的必要） ①减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
	 * ②使用.bitmapConfig(Bitmap.config.RGB_565)代替ARGB_8888;
	 * ③使用.imageScaleType(ImageScaleType.IN_SAMPLE_INT)或者
	 * try.imageScaleType(ImageScaleType.EXACTLY)；
	 * ④避免使用RoundedBitmapDisplayer.他会创建新的ARGB_8888格式的Bitmap对象；
	 * ⑤使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
	 */
	private static DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.picture_manager_default) // 设置图片在下载期间显示的图片
	// img_skin_default_thumbnail
			.showImageForEmptyUri(R.drawable.picture_manager_faile)//
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.picture_manager_faile) //
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
			// .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
			.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
			.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
			// .decodingOptions(android.graphics.BitmapFactory.Options
			// decodingOptions)//设置图片的解码配置
			// .delayBeforeLoading(int delayInMillis)//int
			// delayInMillis为你设置的下载前的延迟时间
			// 设置图片加入缓存前，对bitmap进行设置
			// .preProcessor(BitmapProcessor preProcessor)
			.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
			// .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
			// .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
			.build();// 构建完成

	/**
	 * 加载网络图片
	 * 
	 * @param imageUrl
	 * @param mImageView
	 * @param resid
	 * @param showImage
	 */
	public static void loadImageFormUrl(String imageUrl, View mImageView,
			int resid, boolean showImage) {
		loadImage(imageUrl, mImageView, resid, showImage);
	}

	/**
	 * 加载本地图片
	 * 
	 * @param imagePath
	 * @param mImageView
	 * @param resid
	 * @param showImage
	 */
	public static void loadImageFormFile(String imagePath, View mImageView,
			int resid, boolean showImage) {
		String imageUrl = Scheme.FILE.wrap(imagePath);
		loadImage(imageUrl, mImageView, resid, showImage);
	}

	/**
	 * 加载图片并显示
	 * 
	 * @param imageUrl
	 * @param mImageView
	 * @param resid
	 * @param showImage
	 * @param imcallBack
	 */
	private static void loadImage(String imageUrl, final View mImageView,
			final int resid, final boolean showImage) {
		ImageLoader.getInstance().loadImage(imageUrl, options,

		new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				if (resid != 0 && showImage)
					mImageView.setBackgroundResource(resid);
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}

			@SuppressLint("NewApi")
			@Override
			public void onLoadingComplete(String arg0, View arg1,
					Bitmap loadedImage) {
				if (showImage)
					mImageView.setBackground(new BitmapDrawable(loadedImage));
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		}

		);
	}
}
