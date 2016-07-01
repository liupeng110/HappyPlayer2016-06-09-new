package com.happy.model.app;

import java.io.Serializable;

import com.happy.model.widget.BackgroundColor;
import com.happy.model.widget.ButtonIcon;
import com.happy.model.widget.Icon;
import com.happy.model.widget.SeekBar;
import com.happy.model.widget.Text;

/**
 * 皮肤数据
 * 
 */
public class SkinInfo  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 通用的一级菜单
	 */
	private Text CommonTitleText;
	/**
	 * 通用的二级菜单
	 */
	private Text CommonSubTitleText;
	/**
	 * 按钮点击后的背景颜色
	 */
	private int ButtonPressBgColor;
	/**
	 * 标题字体颜色
	 */
	private int TitleColor;
	/**
	 * 标题背景颜色
	 */
	private int TitleBackgroundColor;
	/**
	 * 导航背景颜色
	 */
	private int IndicatorBackgroundColor;
	/**
	 * 导航文字颜色
	 */
	private Text IndicatorTitleText;
	/**
	 * 导航底部指示器背景颜色
	 */
	private int IndicatorLineBackgroundColor;
	/**
	 * listview分隔线
	 */
	private int ItemDividerBackgroundColor;

	/**
	 * 主页面背景
	 */
	private String BackgroundPath;
	/**
	 * 页面背景颜色
	 */
	private int BackgroundColor;

	/**
	 * item颜色
	 */
	private BackgroundColor ItemBackgroundColor;
	/**
	 * listview item
	 */
	private BackgroundColor ListItemBackgroundColor;
	/**
	 * item弹出菜单
	 */
	private BackgroundColor PopdownItemBackgroundColor;
	/**
	 * 标题
	 */
	private Icon TitleIcon;
	/**
	 * 标题搜索按钮
	 */
	private Icon TitleSearchIcon;
	/**
	 * 标题返回按钮
	 */
	private Icon TitleBackIcon;
	/**
	 * 底部播放分隔线
	 */
	private int PlayBarDividerBackgroundColor;

	/**
	 * 随机播放按钮
	 */
	private Icon RandomPlay;
	/**
	 * 本地音乐
	 */
	private Icon MusicIcon;
	/**
	 * 我的最爱
	 */
	private Icon FavoriteIcon;
	/**
	 * 我的下载
	 */
	private Icon DownloadIcon;

	/**
	 * 底部播放bar背景颜色
	 */
	private int PlayBarBackgroundColor;

	/**
	 * 默认歌手图片
	 */
	private Icon PlayBarDefArtistIcon;

	/**
	 * 菜单图标
	 */
	private ButtonIcon PlayBarSidebarIcon;
	/**
	 * 底部播放bar播放按钮图标
	 */
	private ButtonIcon PlayBarPlayButtonIcon;
	/**
	 * 底部播放bar暂停按钮图标
	 */
	private ButtonIcon PlayBarPauseButtonIcon;
	/**
	 * 底部播放bar下一首按钮图标
	 */
	private ButtonIcon PlayBarNextButtonIcon;
	/**
	 * 底部播放bar进度条
	 */
	private SeekBar PlayBarSlide;

	/**
	 * menu菜单背景颜色
	 */
	private int MenuBackgroundColor;

	/**
	 * menu菜单item点击后的背景颜色
	 */
	private int MenuItemPressBgColor;
	/**
	 * 菜单标题颜色
	 */
	private int MenuTitleColor;

	public Text getCommonTitleText() {
		return CommonTitleText;
	}

	public void setCommonTitleText(Text commonTitleText) {
		CommonTitleText = commonTitleText;
	}

	public Text getCommonSubTitleText() {
		return CommonSubTitleText;
	}

	public void setCommonSubTitleText(Text commonSubTitleText) {
		CommonSubTitleText = commonSubTitleText;
	}

	public int getButtonPressBgColor() {
		return ButtonPressBgColor;
	}

	public void setButtonPressBgColor(int buttonPressBgColor) {
		ButtonPressBgColor = buttonPressBgColor;
	}

	public int getTitleColor() {
		return TitleColor;
	}

	public void setTitleColor(int titleColor) {
		TitleColor = titleColor;
	}

	public int getTitleBackgroundColor() {
		return TitleBackgroundColor;
	}

	public void setTitleBackgroundColor(int titleBackgroundColor) {
		TitleBackgroundColor = titleBackgroundColor;
	}

	public int getIndicatorBackgroundColor() {
		return IndicatorBackgroundColor;
	}

	public void setIndicatorBackgroundColor(int indicatorBackgroundColor) {
		IndicatorBackgroundColor = indicatorBackgroundColor;
	}

	public Text getIndicatorTitleText() {
		return IndicatorTitleText;
	}

	public void setIndicatorTitleText(Text indicatorTitleText) {
		IndicatorTitleText = indicatorTitleText;
	}

	public int getIndicatorLineBackgroundColor() {
		return IndicatorLineBackgroundColor;
	}

	public void setIndicatorLineBackgroundColor(int indicatorLineBackgroundColor) {
		IndicatorLineBackgroundColor = indicatorLineBackgroundColor;
	}

	public int getItemDividerBackgroundColor() {
		return ItemDividerBackgroundColor;
	}

	public void setItemDividerBackgroundColor(int itemDividerBackgroundColor) {
		ItemDividerBackgroundColor = itemDividerBackgroundColor;
	}

	public String getBackgroundPath() {
		return BackgroundPath;
	}

	public void setBackgroundPath(String backgroundPath) {
		BackgroundPath = backgroundPath;
	}

	public int getBackgroundColor() {
		return BackgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		BackgroundColor = backgroundColor;
	}

	public BackgroundColor getItemBackgroundColor() {
		return ItemBackgroundColor;
	}

	public BackgroundColor getListItemBackgroundColor() {
		return ListItemBackgroundColor;
	}

	public void setListItemBackgroundColor(
			BackgroundColor listItemBackgroundColor) {
		ListItemBackgroundColor = listItemBackgroundColor;
	}

	public BackgroundColor getPopdownItemBackgroundColor() {
		return PopdownItemBackgroundColor;
	}

	public void setPopdownItemBackgroundColor(
			BackgroundColor popdownItemBackgroundColor) {
		PopdownItemBackgroundColor = popdownItemBackgroundColor;
	}

	public void setItemBackgroundColor(BackgroundColor itemBackgroundColor) {
		ItemBackgroundColor = itemBackgroundColor;
	}

	public Icon getTitleIcon() {
		return TitleIcon;
	}

	public void setTitleIcon(Icon titleIcon) {
		TitleIcon = titleIcon;
	}

	public Icon getTitleSearchIcon() {
		return TitleSearchIcon;
	}

	public void setTitleSearchIcon(Icon titleSearchIcon) {
		TitleSearchIcon = titleSearchIcon;
	}

	public Icon getTitleBackIcon() {
		return TitleBackIcon;
	}

	public void setTitleBackIcon(Icon titleBackIcon) {
		TitleBackIcon = titleBackIcon;
	}

	public int getPlayBarDividerBackgroundColor() {
		return PlayBarDividerBackgroundColor;
	}

	public void setPlayBarDividerBackgroundColor(
			int playBarDividerBackgroundColor) {
		PlayBarDividerBackgroundColor = playBarDividerBackgroundColor;
	}

	public Icon getRandomPlay() {
		return RandomPlay;
	}

	public void setRandomPlay(Icon randomPlay) {
		RandomPlay = randomPlay;
	}

	public Icon getMusicIcon() {
		return MusicIcon;
	}

	public void setMusicIcon(Icon musicIcon) {
		MusicIcon = musicIcon;
	}

	public Icon getFavoriteIcon() {
		return FavoriteIcon;
	}

	public void setFavoriteIcon(Icon favoriteIcon) {
		FavoriteIcon = favoriteIcon;
	}

	public Icon getDownloadIcon() {
		return DownloadIcon;
	}

	public void setDownloadIcon(Icon downloadIcon) {
		DownloadIcon = downloadIcon;
	}

	public int getPlayBarBackgroundColor() {
		return PlayBarBackgroundColor;
	}

	public void setPlayBarBackgroundColor(int playBarBackgroundColor) {
		PlayBarBackgroundColor = playBarBackgroundColor;
	}

	public Icon getPlayBarDefArtistIcon() {
		return PlayBarDefArtistIcon;
	}

	public void setPlayBarDefArtistIcon(Icon playBarDefArtistIcon) {
		PlayBarDefArtistIcon = playBarDefArtistIcon;
	}

	public ButtonIcon getPlayBarSidebarIcon() {
		return PlayBarSidebarIcon;
	}

	public void setPlayBarSidebarIcon(ButtonIcon playBarSidebarIcon) {
		PlayBarSidebarIcon = playBarSidebarIcon;
	}

	public ButtonIcon getPlayBarPlayButtonIcon() {
		return PlayBarPlayButtonIcon;
	}

	public void setPlayBarPlayButtonIcon(ButtonIcon playBarPlayButtonIcon) {
		PlayBarPlayButtonIcon = playBarPlayButtonIcon;
	}

	public ButtonIcon getPlayBarPauseButtonIcon() {
		return PlayBarPauseButtonIcon;
	}

	public void setPlayBarPauseButtonIcon(ButtonIcon playBarPauseButtonIcon) {
		PlayBarPauseButtonIcon = playBarPauseButtonIcon;
	}

	public ButtonIcon getPlayBarNextButtonIcon() {
		return PlayBarNextButtonIcon;
	}

	public void setPlayBarNextButtonIcon(ButtonIcon playBarNextButtonIcon) {
		PlayBarNextButtonIcon = playBarNextButtonIcon;
	}

	public SeekBar getPlayBarSlide() {
		return PlayBarSlide;
	}

	public void setPlayBarSlide(SeekBar playBarSlide) {
		PlayBarSlide = playBarSlide;
	}

	public int getMenuBackgroundColor() {
		return MenuBackgroundColor;
	}

	public void setMenuBackgroundColor(int menuBackgroundColor) {
		MenuBackgroundColor = menuBackgroundColor;
	}

	public int getMenuItemPressBgColor() {
		return MenuItemPressBgColor;
	}

	public void setMenuItemPressBgColor(int menuItemPressBgColor) {
		MenuItemPressBgColor = menuItemPressBgColor;
	}

	public int getMenuTitleColor() {
		return MenuTitleColor;
	}

	public void setMenuTitleColor(int menuTitleColor) {
		MenuTitleColor = menuTitleColor;
	}

}
