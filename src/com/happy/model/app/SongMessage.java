package com.happy.model.app;

import java.io.Serializable;

/**
 * 
 * 歌曲信息类
 * 
 */
public class SongMessage  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int i = 1000;

	public static final int DOWNLOADADDMUSIC = (i++); // 下载添加音乐

	public static final int ADDMUSIC = (i++); // 添加音乐
	public static final int LOCALDELMUSIC = (i++); // 本地歌曲列表删除歌曲
	public static final int LOCALADDLIKEMUSIC = (i++);// 本地添加喜欢歌曲
	public static final int LOCALUNLIKEMUSIC = (i++);// 本地取消喜欢歌曲
	public static final int LIKEDELMUSIC = (i++);// 喜欢列表删除歌曲
	public static final int SCANEDMUSIC = (i++); // 扫描音乐
	public static final int UPDATEMUSIC = (i++); // 更新音乐内容

	public static final int INITMUSIC = (i++); // 初始化歌曲操作
	public static final int PREMUSIC = (i++); // 上一首歌曲操作
	public static final int PLAYMUSIC = (i++); // 播放歌曲操作
	public static final int PLAYINFOMUSIC = (i++); // 播放歌曲
	public static final int PAUSEMUSIC = (i++); // 暂停歌曲操作
	public static final int NEXTMUSIC = (i++); // 下一首歌曲操作
	public static final int ERRORMUSIC = (i++); // 歌曲错误操作
	public static final int SEEKTOMUSIC = (i++); // 歌曲快进操作
	public static final int RANDOMMUSIC = (i++); // 随机选择歌曲操作

	public static final int SERVICEPLAYMUSIC = (i++); // 服务播放歌曲操作
	public static final int SERVICEPLAYINIT = (i++); // 服务播放歌曲操作
	public static final int SERVICEPLAYWAITING = (i++); // 服务播放歌曲等待
	public static final int SERVICEPLAYWAITINGEND = (i++); // 服务播放歌曲等待结束
	public static final int SERVICEDOWNLOADFINISHED = (i++); // 服务下载完成歌曲
	public static final int SERVICEPLAYINGMUSIC = (i++); // 服务正在播放歌曲操作
	public static final int SERVICEPAUSEMUSIC = (i++); // 服务暂停歌曲操作
	public static final int SERVICEPAUSEEDMUSIC = (i++); // 服务已经暂停歌曲操作
	public static final int SERVICESEEKTOMUSIC = (i++); // 服务快进歌曲
	public static final int SERVICEERRORMUSIC = (i++); // 服务播放歌曲错误操作

	public static final int ALUBMPHOTOLOADED = (i++); // 歌曲专辑图片加载完成

	public static final int SINGERPHOTOLOADED = (i++); // 歌手写真图片加载完成

	public static final int KSCTYPELRC = (i++); // lrcview ksc歌词
	public static final int LRCKSCDOWNLOADED = (i++); // lrcview ksc歌词下载完成
	public static final int LRCKSCLOADED = (i++); // lrcview ksc歌词下载完成

	public static final int KSCTYPEDES = (i++); // 桌面 ksc歌词
	public static final int DESKSCDOWNLOADED = (i++); // 桌面 ksc歌词下载完成
	public static final int DESKSCLOADED = (i++); // 桌面 ksc歌词下载完成

	public static final int KSCTYPELOCK = (i++); // 锁屏ksc歌词
	public static final int LOCKKSCDOWNLOADED = (i++); // 锁屏 ksc歌词下载完成
	public static final int LOCKKSCLOADED = (i++); // 锁屏 ksc歌词下载完成

	public static final int DESLRCSHOWORHIDE = (i++); // 桌面歌词显示或者隐藏
	public static final int DESLRCSHOWORHIDEED = (i++); // 桌面歌词显示或者隐藏完成
	public static final int DESLRCLOCKORUNLOCK = (i++); // 桌面歌词锁定或者解锁
	public static final int DESLRCLOCKORUNLOCKED = (i++); // 桌面歌词锁定或者解锁完成

	/**
	 * 歌曲为空报错信息
	 */
	public static final String ERRORMESSAGESONGNULL = "请选择播放歌曲";
	/**
	 * 播放歌曲为空报错信息
	 */

	public static final String ERRORMESSAGEPLAYSONGNULL = "播放歌曲为空报错";

	private int type;
	private SongInfo songInfo;// 歌曲数据
	private String errorMessage;// 错误信息

	private int progress = 0;// 进度
	private String kscFilePath; // ksc歌词路径
	private String sid;// ksc歌词所属的sid

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	public void setSongInfo(SongInfo songInfo) {
		this.songInfo = songInfo;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getKscFilePath() {
		return kscFilePath;
	}

	public void setKscFilePath(String kscFilePath) {
		this.kscFilePath = kscFilePath;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

}
