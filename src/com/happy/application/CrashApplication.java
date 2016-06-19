package com.happy.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import com.happy.common.Constants;
import com.happy.logger.LoggerManage;
import com.happy.manage.ActivityManage;
import com.happy.model.app.Alert;
import com.happy.observable.ObserverManage;
import com.happy.util.ToastUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class CrashApplication extends Application implements Observer {

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ToastUtil.showTextToast(getApplicationContext(), (String) msg.obj);
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		 CrashHandler catchHandler = new CrashHandler();
		 catchHandler.init(getApplicationContext());
		initImageLoad();
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 初始化图片下载配置
	 */
	private void initImageLoad() {
		File file = new File(Constants.PATH_CACHE_IMAGE);
		WindowManager wm = (WindowManager) getBaseContext().getSystemService(
				Context.WINDOW_SERVICE);
		int wwidth = wm.getDefaultDisplay().getWidth();// 手机屏幕的宽度
		int hheight = wm.getDefaultDisplay().getHeight();// 手机屏幕的高度
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(wwidth, hheight)
				// max width, max height，即保存的每个缓存文件的最大长宽
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null) // Can slow ImageLoader, use it carefully (Better don't
				// use it)/设置缓存的详细信息，最好不要设置这个
				/**
				 * ①减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
				 */
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// ⑤使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new WeakMemoryCache())
				// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 *
				// 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				// .memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// 缓存的文件数量
				.discCache(new UnlimitedDiskCache(file))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(getApplicationContext(),
								5 * 1000, 30 * 1000)) // connectTimeout
				// (5
				// s),
				// readTimeout
				// (30
				// s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();// 开始构建
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof Alert) {
			Alert alert = (Alert) data;
			Message msg = new Message();
			msg.obj = alert.getAlertText();

			handler.sendMessage(msg);
		}
	}

	/**
	 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误报告.
	 * 
	 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
	 */
	public class CrashHandler implements UncaughtExceptionHandler {

		private LoggerManage logger;
		private SimpleDateFormat crashfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
		private int SDCARD_LOG_FILE_SAVE_DAYS = 3;// sd卡中日志文件的最多保存天数
		// 系统默认的UncaughtException处理类
		private Thread.UncaughtExceptionHandler mDefaultHandler;
		// 程序的Context对象
		private Context mContext;
		// 用来存储设备信息和异常信息
		private Map<String, String> infos = new HashMap<String, String>();

		/** 保证只有一个CrashHandler实例 */
		private CrashHandler() {
			cleanOldLogFile();
		}

		/**
		 * 初始化
		 */
		public void init(Context context) {
			mContext = context;
			logger = LoggerManage.getZhangLogger(mContext);
			// 获取系统默认的UncaughtException处理器
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			// 设置该CrashHandler为程序的默认处理器
			Thread.setDefaultUncaughtExceptionHandler(this);
		}

		/**
		 * 当UncaughtException发生时会转入该函数来处理
		 */
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (!handleException(ex) && mDefaultHandler != null) {
				// 如果用户没有处理则让系统默认的异常处理器来处理
				mDefaultHandler.uncaughtException(thread, ex);
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.e(e.toString());
				}
				// 退出程序
				ActivityManage.getInstance().exit();
			}
		}

		/**
		 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
		 * 
		 * @param ex
		 * @return true:如果处理了该异常信息;否则返回false.
		 */
		private boolean handleException(Throwable ex) {
			if (ex == null) {
				return false;
			}
			// 收集设备参数信息
			collectDeviceInfo(mContext);
			// 使用Toast来显示异常信息
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(mContext, "程序出现异常,即将退出", Toast.LENGTH_SHORT)
							.show();
					Looper.loop();
				}
			}.start();
			// 保存日志文件
			saveCatchInfoFile(ex);
			return true;
		}

		/**
		 * 收集设备参数信息
		 * 
		 * @param mContext
		 */
		private void collectDeviceInfo(Context ctx) {
			// 获取当前程序的版本号. 版本的id
			try {
				PackageManager pm = ctx.getPackageManager();
				PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
						PackageManager.GET_ACTIVITIES);
				if (pi != null) {
					String versionName = pi.versionName == null ? "null"
							: pi.versionName;
					String versionCode = pi.versionCode + "";
					infos.put("versionName", versionName);
					infos.put("versionCode", versionCode);
				}
			} catch (NameNotFoundException e) {
				logger.e(e.toString());
			}
			// 获取手机的硬件信息
			// 通过反射获取系统的硬件信息
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					infos.put(field.getName(), field.get(null).toString());
				} catch (Exception e) {
					logger.e(e.toString());
				}
			}
		}

		/**
		 * 保存日志文件
		 * 
		 * @param ex
		 */
		private void saveCatchInfoFile(Throwable ex) {

			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : infos.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key + "=" + value + "\n");
			}

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			sb.append(result);
			try {
				// 用于格式化日期,作为日志文件名的一部分
				String time = crashfile.format(new Date());
				String fileName = time + ".log";
				String path = Constants.PATH_CRASH + File.separator;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			} catch (Exception e) {
				logger.e(e.toString());
			}
		}

		/**
		 * 清除过期的日志文件
		 */
		private void cleanOldLogFile() {
			File logFileParent = new File(Constants.PATH_CRASH);
			if (logFileParent.exists()) {
				String needDelTime = crashfile.format(getDateBefore());
				File[] files = logFileParent.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						String fileName = files[i].getName();
						fileName = fileName.substring(0,
								fileName.lastIndexOf("."));
						if (needDelTime.compareTo(fileName) > 0) {
							files[i].delete();
						}
					}
				}
			}
		}

		/**
		 * 
		 * @return
		 */
		private Date getDateBefore() {
			Date nowtime = new Date();
			Calendar now = Calendar.getInstance();
			now.setTime(nowtime);
			now.set(Calendar.DATE, now.get(Calendar.DATE)
					- SDCARD_LOG_FILE_SAVE_DAYS);
			return now.getTime();
		}
	}
}
