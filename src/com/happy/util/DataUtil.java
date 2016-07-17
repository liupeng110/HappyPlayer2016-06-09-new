package com.happy.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Xml;

import com.happy.common.Constants;
import com.happy.db.SkinThemeDB;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SkinInfo;
import com.happy.model.app.SkinThemeApp;
import com.happy.observable.ObserverManage;

/**
 * SharedPreferences配置文件处理和皮肤数据初始化处理
 * 
 * @author zhangliangming
 * 
 */
public class DataUtil {
	private static SharedPreferences preferences;

	/**
	 * 初始化，将所有配置文件里的数据赋值给Constants
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(
					Constants.PREFERENCE_NAME, 0);
		}
		initSharedPreferences();
		initFile(context);
		// 应用第一次使用，先将assets里面的皮肤包解压
		if (Constants.isFrist) {
			// 清空file目录
			String path = context.getFilesDir().getParent() + File.separator
					+ "files";
			cleanFile(path);
			// 解压
			unAsssetsSkinFile(context);
			Constants.isFrist = false;
			saveValue(context, Constants.isFrist_KEY, Constants.isFrist);
		}
		loadSkin(context);
	}

	/**
	 * 
	 * @Title: initSharedPreferences
	 * @Description: (初始化基本的数据配置)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private static void initSharedPreferences() {
		// 皮肤主题id
		Constants.skinID = preferences.getString(Constants.skinID_KEY,
				Constants.skinID);
		// 是否是第一次启动
		Constants.isFrist = preferences.getBoolean(Constants.isFrist_KEY,
				Constants.isFrist);
		// 是否是wifi网络设置
		Constants.isWifi = preferences.getBoolean(Constants.isWifi_KEY,
				Constants.isWifi);
		// 播放模式
		Constants.playModel = preferences.getInt(Constants.playModel_KEY,
				Constants.playModel);
		// 桌面歌词
		Constants.showDesktopLyrics = preferences.getBoolean(
				Constants.showDesktopLyrics_KEY, Constants.showDesktopLyrics);
		// 桌面歌词的位置x轴
		Constants.LRCX = preferences.getInt(Constants.LRCX_KEY, Constants.LRCX);

		// 桌面歌词的位置Y轴
		Constants.LRCY = preferences.getInt(Constants.LRCY_KEY, Constants.LRCY);

		// 桌面歌词是否可以移动
		Constants.desktopLyricsIsMove = preferences.getBoolean(
				Constants.desktopLyricsIsMove_KEY,
				Constants.desktopLyricsIsMove);

		// 锁屏歌词
		Constants.showLockScreen = preferences.getBoolean(
				Constants.showLockScreen_KEY, Constants.showLockScreen);
		// 是否线控
		Constants.isWire = preferences.getBoolean(Constants.isWire_KEY,
				Constants.isWire);
		// 是否开启辅助操控
		Constants.isEasyTouch = preferences.getBoolean(
				Constants.isEasyTouch_KEY, Constants.isEasyTouch);
		// 是否开启问候音
		Constants.isSayHello = preferences.getBoolean(Constants.isSayHello_KEY,
				Constants.isSayHello);
		// 音质索引
		Constants.soundIndex = preferences.getInt(Constants.soundIndex_KEY,
				Constants.soundIndex);
		// 播放列表类型
		Constants.playListType = preferences.getInt(Constants.playListType_KEY,
				Constants.playListType);
		// 歌曲id
		Constants.playInfoID = preferences.getString(Constants.playInfoID_KEY,
				Constants.playInfoID);

		// 标题颜色索引
		Constants.colorIndex = preferences.getInt(Constants.colorIndex_KEY,
				Constants.colorIndex);
		// 歌词颜色索引
		Constants.lrcColorIndex = preferences.getInt(
				Constants.lrcColorIndex_KEY, Constants.lrcColorIndex);
		// 歌词字体大小
		Constants.lrcFontSize = preferences.getInt(Constants.lrcFontSize_KEY,
				Constants.lrcFontSize);

		// 桌面歌词字体大小
		Constants.desktopLrcFontSize = preferences.getInt(
				Constants.desktopLrcFontSize_KEY, Constants.desktopLrcFontSize);

		// 桌面歌词颜色索引
		Constants.desktopLrcIndex = preferences.getInt(
				Constants.desktopLrcIndex_KEY, Constants.desktopLrcIndex);

		// 是否是第一次点击显示桌面歌词
		Constants.isFristSettingDesLrc = preferences.getBoolean(
				Constants.isFristSettingDesLrc_KEY,
				Constants.isFristSettingDesLrc);

	}

	/**
	 * 
	 * @Title: initFile
	 * @Description: (初始化文件夹)
	 * @param:
	 * @return: void
	 * @throws
	 */
	public static void initFile(Context context) {
		// 创建相关的文件夹
		File file = new File(Constants.PATH_AUDIO);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.PATH_KSC);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.PATH_ARTIST);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.PATH_ALBUM);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constants.PATH_LOGCAT);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_CRASH);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_CACHE);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_CACHE_IMAGE);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_CACHE_AUDIO);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_SKIN);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_APK);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_SPLASH);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_EasyTouch);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_MP3TEMP);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(Constants.PATH_SPLASH_TXT);
		if (!file.exists()) {
			// 解压splash说明文件到splash文件夹
			copyFile(context, "splash_readme.txt", Constants.PATH_SPLASH_TXT);
		}
	}

	private static void copyFile(Context context, String from, String to) {
		// 例：from:890.salid;
		// to:/mnt/sdcard/to/890.salid
		try {
			int bytesum = 0;
			int byteread = 0;
			InputStream inStream = context.getResources().getAssets()
					.open(from);// 将assets中的内容以流的形式展示出来
			OutputStream fs = new BufferedOutputStream(new FileOutputStream(to));// to为要写入sdcard中的文件名称
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();

		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @Title: unAsssetsSkinFile
	 * @Description: (解压assets文件里的皮肤文件)
	 * @param:
	 * @return: void
	 * @throws
	 */
	private static void unAsssetsSkinFile(Context context) {
		String[] files = null;
		try {
			// 遍历assest文件夹下skin文件里面的所有文件
			files = context.getAssets().list("skin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String path = context.getFilesDir().getParent() + File.separator
				+ "files";
		String outputDirectory = path + File.separator + "skin"
				+ File.separator;
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			if (fileName.contains(".xml")) {
				String assetPath = "skin" + File.separator + fileName;
				try {
					XmlPullParser parser = Xml.newPullParser();
					InputStream inputStream = context.getAssets().open(
							assetPath);
					parser.setInput(inputStream, "UTF-8"); // 设置输入流 并指明编码方式
					int eventType = parser.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if (parser.getName().equals("item")) {
								eventType = parser.next();
								String zipPath = "skin" + File.separator
										+ parser.getText();
								String zipName = parser.getText().substring(0,
										parser.getText().lastIndexOf("."));
								boolean result = UnzipUtil.unAssetsZip(context,
										zipName, zipPath, outputDirectory);
								if (result) {
									// 读取皮肤里面的相关数据，并将相关的数据保存到数据库
									String unZipFilePath = outputDirectory
											+ zipName;
									SkinThemeApp skinTheme = new SkinThemeApp();
									skinTheme.setAssetsType(SkinThemeApp.LOCAL);
									readSkinTheme(context, unZipFilePath,
											skinTheme);
									boolean isExists = SkinThemeDB
											.getSkinThemeDB(context)
											.skinThemeIsExists(
													skinTheme.getID());
									if (!isExists) {
										SkinThemeDB.getSkinThemeDB(context)
												.add(skinTheme);
									}
								}
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

				break;
			}
		}
	}

	/**
	 * 
	 * @Title: readSkinTheme
	 * @Description: (读取解压后的文件夹)
	 * @param: @param context
	 * @param: @param unZipFilePath 解压后的文件路径
	 * @return: void
	 * @throws
	 */
	private static void readSkinTheme(Context context, String unZipFilePath,
			SkinThemeApp skinTheme) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			String basePath = unZipFilePath + File.separator;
			String path = basePath + "config.xml";
			File xmlFile = new File(path);
			FileInputStream is = new FileInputStream(xmlFile);
			parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("Theme")) {
						String ID = parser.getAttributeValue("", "ID").trim();
						String Name = parser.getAttributeValue("", "Name")
								.trim();
						String previewPath = basePath
								+ "images"
								+ File.separator
								+ parser.getAttributeValue("", "preview")
										.trim();

						skinTheme.setID(ID);
						skinTheme.setThemeName(Name);
						skinTheme.setPreviewPath(previewPath);
						skinTheme.setUnZipPath(unZipFilePath);
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
	}

	/**
	 * 
	 * @Title: loadSkin
	 * @Description: (加载皮肤)
	 * @param: @param context
	 * @return: void
	 * @throws
	 */
	public static void loadSkin(Context context) {
		// 通过皮肤的id从数据库里获取要显示的皮肤，如果获取失败，则加载默认的皮肤
		SkinThemeApp skinTheme = SkinThemeDB.getSkinThemeDB(context)
				.getSkinThemeInfo(Constants.skinID);
		if (skinTheme == null) {
			initdefSkin(context);
			return;
		}
		// 初始化皮肤数据
		SkinInfo skinInfo = SkinUtil
				.loadSkin(context, skinTheme.getUnZipPath());
		if (skinInfo != null) {
			Constants.skinInfo = skinInfo;
			// 皮肤设置数据成功
		} else {
			// 皮肤设置数据失败
			initdefSkin(context);
		}
	}

	/**
	 * 
	 * @Title: unZipAndLoadSkin
	 * @Description: (解压zip并设置皮肤)
	 * @param: @param context
	 * @param: @param fileName
	 * @param: @param filePath
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public static boolean unZipAndLoadSkin(Context context, String fileName,
			String filePath) {
		String path = context.getFilesDir().getParent() + File.separator
				+ "files";
		String outputDirectory = path + File.separator + "skin"
				+ File.separator;
		// 解压皮肤文件
		boolean result = UnzipUtil.unAssetsZip(context, fileName, filePath,
				outputDirectory);
		if (result) {
			// 解压成功
			// 初始化皮肤数据
			SkinInfo skinInfo = SkinUtil.loadSkin(context, outputDirectory
					+ fileName);
			if (skinInfo != null) {
				Constants.skinInfo = skinInfo;
				return true;
				// 皮肤设置数据成功
			} else {
				// 皮肤设置数据失败
			}
		} else {
			// 解压失败
		}
		return false;
	}

	/**
	 * 初始化默认皮肤数据,在一般情况下，解压皮肤都不会出错。
	 * 
	 * @param context
	 */
	private static void initdefSkin(Context context) {
		String assetFileName = "hp19910420";
		String assetPath = "skin" + File.separator + assetFileName + ".zip";
		String path = context.getFilesDir().getParent() + File.separator
				+ "files";
		String outputDirectory = path + File.separator + "skin"
				+ File.separator;
		// 解压皮肤文件
		boolean result = UnzipUtil.unAssetsZip(context, assetFileName,
				assetPath, outputDirectory);
		if (result) {
			// 解压成功
			// 初始化皮肤数据
			SkinInfo skinInfo = SkinUtil.loadSkin(context, outputDirectory
					+ assetFileName);
			if (skinInfo != null) {
				Constants.skinID = assetFileName;
				Constants.skinInfo = skinInfo;
				// 皮肤设置数据成功
			}
		}

		saveValue(context, Constants.skinID_KEY, assetFileName);

		// 如果进入加载默认皮肤，则证明加载皮肤数据失败
		MessageIntent mi = new MessageIntent();
		mi.setAction(MessageIntent.SKINTHEMEERROR);
		ObserverManage.getObserver().setMessage(mi);
	}

	/**
	 * 清空file目录
	 * 
	 * @param path
	 *            目录路径
	 */
	public static void cleanFile(String path) {
		// 清空file目录；
		File file_file = new File(path);
		if (file_file.exists()) {
			File[] files = file_file.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * 保存数据到SharedPreferences配置文件
	 * 
	 * @param context
	 * @param key
	 *            关键字
	 * @param data
	 *            要保存的数据
	 */
	public static void saveValue(Context context, String key, Object data) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(
					Constants.PREFERENCE_NAME, 0);
		}
		Editor editor = preferences.edit();
		if (data instanceof Boolean) {
			editor.putBoolean(key, (Boolean) data);
		} else if (data instanceof Integer) {
			editor.putInt(key, (Integer) data);
		} else if (data instanceof String) {
			editor.putString(key, (String) data);
		} else if (data instanceof Float) {
			editor.putFloat(key, (Float) data);
		} else if (data instanceof Long) {
			editor.putFloat(key, (Long) data);
		}

		// 提交修改
		editor.commit();
	}

	/**
	 * 从SharedPreferences配置文件中获取数据
	 * 
	 * @param context
	 * @param key
	 *            关键字
	 * @param defData
	 *            默认获取的数据
	 * @return
	 */
	public static Object getValue(Context context, String key, Object defData) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(
					Constants.PREFERENCE_NAME, 0);
		}

		if (defData instanceof Boolean) {
			return preferences.getBoolean(key, (Boolean) defData);
		} else if (defData instanceof Integer) {
			return preferences.getInt(key, (Integer) defData);
		} else if (defData instanceof String) {
			return preferences.getString(key, (String) defData);
		} else if (defData instanceof Float) {
			return preferences.getFloat(key, (Float) defData);
		} else if (defData instanceof Long) {
			return preferences.getLong(key, (Long) defData);
		}

		return null;

	}
}
