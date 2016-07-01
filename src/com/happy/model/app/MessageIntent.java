package com.happy.model.app;

import java.io.Serializable;

public class MessageIntent  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 皮肤加载失败
	 */
	public static final String SKINTHEMEERROR = "com.hp.skin.error";

	/**
	 * 切换播放模式
	 */
	public static final String CHANGEMODE = "com.hp.play.changemode";

	/**
	 * 标题颜色
	 */
	public static final String TITLECOLOR = "com.hp.title.color";

	/**
	 * 多行歌词字体大小
	 */
	public static final String KSCMANYLINEFONTSIZE = "com.hp.ksc.fontsize";
	/**
	 * 多行歌词歌词颜色
	 */
	public static final String KSCMANYLINELRCCOLOR = "com.hp.ksc.lrc.color";

	/**
	 * 多行桌面歌词字体大小
	 */
	public static final String DESKSCMANYLINEFONTSIZE = "com.hp.ksc.des.fontsize";
	/**
	 * 多行桌面歌词歌词颜色
	 */
	public static final String DESKSCMANYLINELRCCOLOR = "com.hp.ksc.lrc.des.color";
	/**
	 * 是否启动线控
	 */
	public static final String OPENORCLOSEWIRE = "com.hp.player.wire";
	/**
	 * 系统时间广播
	 */
	public static final String SYSTEMTIME = "com.hp.system.time";

	private String action;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
