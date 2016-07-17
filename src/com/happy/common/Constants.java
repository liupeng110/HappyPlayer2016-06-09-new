package com.happy.common;

import java.io.File;

import android.graphics.Color;
import android.os.Environment;

import com.happy.model.app.SkinInfo;

public class Constants {
	/**
	 * app是否关闭
	 */
	public final static boolean APPCLOSE = false;
	/**
	 * --------------------------应用配置--------------------------
	 **/
	/**
	 * app应用名
	 */
	public final static String APPNAME = "HappyPlayer";
	/**
	 * 配置文件的名称
	 */
	public static String PREFERENCE_NAME = "happy.sharepreference.name";

	/***
	 * ------------------------------------文件目录基本配置-----------------------------
	 **/
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "happy_player.db";

	/**
	 * 临时目录
	 */
	public final static String PATH_TEMP = Environment
			.getExternalStorageDirectory() + File.separator + "haplayer";

	/**
	 * Logcat日志目录
	 */
	public final static String PATH_LOGCAT = PATH_TEMP + File.separator
			+ "logcat";

	/**
	 * 全局异常日志目录
	 */
	public final static String PATH_CRASH = PATH_TEMP + File.separator
			+ "crash";

	/**
	 * 歌曲目录
	 */
	public final static String PATH_AUDIO = PATH_TEMP + File.separator + "audio";
	/**
	 * 歌曲temp目录
	 */
	public final static String PATH_MP3TEMP = PATH_AUDIO + File.separator
			+ "temp";

	/**
	 * 歌词目录
	 */
	public final static String PATH_KSC = PATH_TEMP + File.separator + "ksc";
	/**
	 * 歌手写真目录
	 */
	public final static String PATH_ARTIST = PATH_TEMP + File.separator
			+ "artist";
	/**
	 * 专辑图
	 */
	public final static String PATH_ALBUM = PATH_TEMP + File.separator
			+ "album";
	/**
	 * 缓存
	 */
	public final static String PATH_CACHE = PATH_TEMP + File.separator
			+ "cache";
	/**
	 * 图片缓存
	 */
	public final static String PATH_CACHE_IMAGE = PATH_TEMP + File.separator
			+ "cache" + File.separator + "image";

	/**
	 * 歌曲缓存
	 */
	public final static String PATH_CACHE_AUDIO = PATH_TEMP + File.separator
			+ "cache" + File.separator + "audio";

	/**
	 * 皮肤
	 */
	public final static String PATH_SKIN = PATH_TEMP + File.separator + "skin";

	/**
	 * 启动界面
	 */
	public final static String PATH_SPLASH = PATH_TEMP + File.separator
			+ "splash";
	/**
	 * 应用启动图片
	 */
	public final static String PATH_SPLASH_JPG = PATH_SPLASH + File.separator
			+ "splash.jpg";

	/**
	 * 应用启动文件
	 */
	public final static String PATH_SPLASH_TXT = PATH_SPLASH + File.separator
			+ "splash_readme.txt";

	/**
	 * 应用更新的apk包存放路径
	 */
	public final static String PATH_APK = PATH_TEMP + File.separator + "apk";
	/**
	 * EasyTouch
	 */
	public final static String PATH_EasyTouch = PATH_TEMP + File.separator
			+ "easytouch";

	/***
	 * ------------------------------------应用基本配置-----------------------------
	 **/
	/**
	 * 皮肤数据
	 */
	public static SkinInfo skinInfo;
	/**
	 * 皮肤默认的id
	 */
	public static String skinID = "hp19910420";
	/**
	 * 皮肤默认的idkeyt
	 */
	public static String skinID_KEY = "skinID_KEY";
	/**
	 * 应用是否是第一次启动
	 */
	public static boolean isFrist = true;
	/**
	 * 应用是否是第一次启动key
	 */
	public static String isFrist_KEY = "isFrist_KEY";
	/**
	 * 是否是第一次点击显示桌面歌词key
	 */
	public static String isFristSettingDesLrc_KEY = "isFristSettingDesLrc_KEY";
	/**
	 * 是否是第一次点击显示桌面歌词
	 */
	public static boolean isFristSettingDesLrc = true;

	/**
	 * 应用是否在wifi下联网
	 */
	public static boolean isWifi = true;
	/**
	 * 应用是否在wifi下联网key
	 */
	public static String isWifi_KEY = "isWifi_KEY";

	/**
	 * 歌曲播放模式
	 */
	public static int playModel = 0; // 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放
	/**
	 * 歌曲播放模式key
	 */
	public static String playModel_KEY = "playModel_KEY";
	/**
	 * 是否显示桌面歌词
	 */
	public static boolean showDesktopLyrics = false;
	/**
	 * 是否显示桌面歌词key
	 */
	public static String showDesktopLyrics_KEY = "showDesktopLyrics_KEY";

	/**
	 * 桌面歌词是否可以移动
	 */
	public static String desktopLyricsIsMove_KEY = "desktopLyricsIsMove_KEY";
	/**
	 * 桌面歌词是否可以移动
	 */
	public static boolean desktopLyricsIsMove = true;

	/**
	 * 歌词窗口x坐标
	 */
	public static String LRCX_KEY = "LRCX_KEY";
	public static int LRCX = 0;

	/**
	 * 歌词窗口y坐标
	 */
	public static String LRCY_KEY = "LRCY_KEY";
	public static int LRCY = 0;

	/**
	 * 是否显示锁屏歌词
	 */
	public static boolean showLockScreen = false;
	/**
	 * 是否显示锁屏歌词key
	 */

	public static String showLockScreen_KEY = "showLockScreen_KEY";

	/**
	 * 是否线控
	 */
	public static boolean isWire = true;

	/**
	 * 是否线控key
	 */
	public static String isWire_KEY = "isWire_KEY";
	/**
	 * 是否开启辅助操控
	 */
	public static boolean isEasyTouch = false;
	/**
	 * 是否开启辅助操控key
	 */
	public static String isEasyTouch_KEY = "isEasyTouch_KEY";
	/**
	 * 是否开启问候音
	 */
	public static boolean isSayHello = false;
	/**
	 * 是否开启问候音key
	 */
	public static String isSayHello_KEY = "isSayHello_KEY";
	/**
	 * 音质索引
	 */
	public static int soundIndex = 0;
	/**
	 * 音质索引key
	 */
	public static String soundIndex_KEY = "soundIndex_KEY";

	/**
	 * 标题颜色索引
	 */
	public static int colorIndex = 0;
	/**
	 * 标题颜色key
	 */
	public static String colorIndex_KEY = "colorIndex_KEY";

	/**
	 * 播放列表类型
	 */
	public static int playListType = 0;
	/**
	 * 播放列表类型key
	 */
	public static String playListType_KEY = "playListType_KEY";
	/**
	 * 播放歌曲id
	 */
	public static String playInfoID = "";
	/**
	 * 播放歌曲id key
	 */
	public static String playInfoID_KEY = "playInfoID_KEY";
	/**
	 * 标题颜色集合
	 */
	public static String[] colorBGColorStr = { "#c4a732", "#667e83", "#f76f60",
			"#f57bb8", "#e7923d", "#b38684" };
	/**
	 * 歌词颜色索引
	 */
	public static int lrcColorIndex = 0;
	/**
	 * 歌词颜色索引key
	 */
	public static String lrcColorIndex_KEY = "lrcColorIndex_KEY";
	/**
	 * 歌词颜色集合
	 */
	public static String[] lrcColorStr = { "#fcff15", "#6ee84d", "#fe6565",
			"#ffa144", "#3cdbe1", "#cc58f2" };

	/**
	 * 歌词最小大小
	 */
	public static int lrcFontMinSize = 100;
	/**
	 * 歌词最大大小
	 */
	public static int lrcFontMaxSize = 200;
	/**
	 * 歌词大小
	 */
	public static int lrcFontSize = lrcFontMinSize;

	/**
	 * 歌词字体大小key
	 */
	public static String lrcFontSize_KEY = "lrcFontSize_KEY";

	/**
	 * 桌面歌词最小大小
	 */
	public static int desktopLrcFontMinSize = 130;
	/**
	 * 桌面歌词最大大小
	 */
	public static int desktopLrcFontMaxSize = 200;
	/**
	 * 桌面歌词大小
	 */
	public static int desktopLrcFontSize = desktopLrcFontMinSize;

	/**
	 * 桌面歌词字体大小key
	 */
	public static String desktopLrcFontSize_KEY = "desktopLrcFontSize_KEY";

	/**
	 * 未读歌词颜色
	 */
	public static int DESLRCNOREADCOLOR[] = { Color.rgb(150, 209, 254),
			Color.rgb(214, 142, 237), Color.rgb(214, 215, 214),
			Color.rgb(233, 74, 69) };
	/**
	 * 已读歌词颜色
	 */
	public static int DESLRCREADEDCOLOR[] = { Color.rgb(229, 146, 230),
			Color.rgb(248, 246, 151), Color.rgb(133, 208, 255),
			Color.rgb(255, 193, 120) };
	/***
	 * 桌面歌词颜色
	 */
	public static String desktopLrcIndex_KEY = "DEF_DES_COLOR_INDEX_KEY";
	public static int desktopLrcIndex = 0;

	/**
	 * 桌面歌词颜色
	 */
	public static int DESLRCCOLORS[] = { Color.rgb(86, 168, 240),
			Color.rgb(170, 29, 216), Color.rgb(207, 208, 207),
			Color.rgb(213, 4, 0) };

	/***
	 * ------------------------------------应用基本配置-----------------------------
	 **/

	/***
	 * ------------------------------------Notification基本配置--------------------
	 * ---------
	 **/
	/**
	 * 通知栏app下载完成通知
	 */
	public static String NOTIFIATION_APP_DOWNLOADFINISH = "com.notification.app.downloadfinish";

	/**
	 * 通知栏app下载通知
	 */
	public static String NOTIFIATION_APP_DOWNLOAD = "com.notification.app.download";
	/**
	 * 通知栏app播放歌曲
	 */
	public static String NOTIFIATION_APP_PLAYMUSIC = "com.notification.app.playmusic";
	/**
	 * 通知栏app暂停歌曲
	 */
	public static String NOTIFIATION_APP_PAUSEMUSIC = "com.notification.app.pausemusic";
	/**
	 * 通知栏app上一首歌曲
	 */
	public static String NOTIFIATION_APP_PREMUSIC = "com.notification.app.premusic";
	/**
	 * 通知栏app下一首歌曲
	 */
	public static String NOTIFIATION_APP_NEXTMUSIC = "com.notification.app.nextmusic";
	/**
	 * 通知栏app关闭
	 */
	public static String NOTIFIATION_APP_CLOSE = "com.notification.app.close";
	/**
	 * 通知栏显示桌面歌词
	 */
	public static String NOTIFIATION_DESLRC_SHOW = "com.notification.des.lrc.show";
	/**
	 * 通知栏隐藏桌面歌词
	 */
	public static String NOTIFIATION_DESLRC_HIDE = "com.notification.des.lrc.hide";
	/**
	 * 通知栏桌面歌词解锁
	 */
	public static String NOTIFIATION_DESLRC_UNLOCK = "com.notification.des.lrc.unlock";
	/***
	 * ------------------------------------Notification基本配置--------------------
	 * ---------
	 **/
}
