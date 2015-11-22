package com.happy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Color;
import android.util.Xml;

import com.happy.logger.LoggerManage;
import com.happy.model.app.SkinInfo;
import com.happy.model.widget.BackgroundColor;
import com.happy.model.widget.ButtonIcon;
import com.happy.model.widget.Icon;
import com.happy.model.widget.SeekBar;
import com.happy.model.widget.Text;

public class SkinUtil {

	/**
	 * 初始化皮肤数据结构
	 * 
	 * @param context
	 * 
	 * @param outputDirectory
	 *            输出路径(包含文件名)
	 */
	public static SkinInfo loadSkin(Context context, String outputDirectory) {
		LoggerManage logger = LoggerManage.getZhangLogger(context);
		SkinInfo skinInfo = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			String basePath = outputDirectory + File.separator;
			String path = basePath + "config.xml";
			File xmlFile = new File(path);
			FileInputStream is = new FileInputStream(xmlFile);
			parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					skinInfo = new SkinInfo();
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("View")) {
						String backgroundPath = basePath + "images"
								+ File.separator
								+ parser.getAttributeValue("", "Background");
						skinInfo.setBackgroundPath(backgroundPath);
						// ImageUtil.preloadingImageFormFile(backgroundPath,
						// context);
						ImageLoadUtil.loadImageFormFile(backgroundPath, null,
								0, false);

						skinInfo.setBackgroundColor(parserColor(parser
								.getAttributeValue("", "BackgroundColor")));
					} else if (parser.getName().equals("Color")) {
						initSkinColorData(skinInfo, parser, basePath);
					} else if (parser.getName().equals("Icon")) {
						initSkinIconData(skinInfo, parser, basePath);
					} else if (parser.getName().equals("Button")) {
						initSkinButtonIconData(skinInfo, parser, basePath);
					} else if (parser.getName().equals("SeekBar")) {
						initSkinSeekBarData(skinInfo, parser, basePath);
					}
					break;
				case XmlPullParser.END_TAG:

					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			skinInfo = null;
			e.printStackTrace();
			logger.e(e.toString());
		}
		return skinInfo;
	}

	/**
	 * 设置皮肤数据
	 * 
	 * @param skinInfo
	 *            皮肤实体类
	 * @param parser
	 *            解析器
	 * @param basePath
	 *            解压后的基本路径
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static void initSkinSeekBarData(SkinInfo skinInfo,
			XmlPullParser parser, String basePath) {
		SeekBar seekBar = new SeekBar();
		String Background = parser.getAttributeValue("", "Background").trim();
		String Progress = parser.getAttributeValue("", "Progress").trim();
		String SecondProgress = parser.getAttributeValue("", "SecondProgress")
				.trim();

		seekBar.setBackgroundColor(parserColor(Background));
		seekBar.setProgressColor(parserColor(Progress));
		seekBar.setSecondProgressColor(parserColor(SecondProgress));

		String ID = parser.getAttributeValue("", "ID").trim();
		if (ID.equals("PlayBarProgress")) {
			skinInfo.setPlayBarSlide(seekBar);
		} else if (ID.equals("")) {

		}
	}

	/**
	 * 设置皮肤数据
	 * 
	 * @param skinInfo
	 *            皮肤实体类
	 * @param parser
	 *            解析器
	 * @param basePath
	 *            解压后的基本路径
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static void initSkinButtonIconData(SkinInfo skinInfo,
			XmlPullParser parser, String basePath)
			throws XmlPullParserException, IOException {
		ButtonIcon buttonIcon = new ButtonIcon();
		String normalIcon = parser.getAttributeValue("", "NormalIcon").trim();
		String normalIconImagePath = basePath + "images" + File.separator
				+ normalIcon;
		buttonIcon.setNormalIcon(normalIconImagePath);

		String pressedIcon = parser.getAttributeValue("", "PressedIcon").trim();
		String pressedIconImagePath = basePath + "images" + File.separator
				+ pressedIcon;
		buttonIcon.setPressedIcon(pressedIconImagePath);
		String ID = parser.getAttributeValue("", "ID").trim();
		if (ID.equals("PlayButton")) {
			skinInfo.setPlayBarPlayButtonIcon(buttonIcon);
		} else if (ID.equals("PauseButton")) {
			skinInfo.setPlayBarPauseButtonIcon(buttonIcon);
		} else if (ID.equals("NextButton")) {
			skinInfo.setPlayBarNextButtonIcon(buttonIcon);
		} else if (ID.equals("PlayBarSidebar")) {
			skinInfo.setPlayBarSidebarIcon(buttonIcon);
		} else if (ID.equals("")) {
		}
	}

	/**
	 * 设置皮肤数据
	 * 
	 * @param skinInfo
	 *            皮肤实体类
	 * @param parser
	 *            解析器
	 * @param basePath
	 *            解压后的基本路径
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static void initSkinIconData(SkinInfo skinInfo,
			XmlPullParser parser, String basePath)
			throws XmlPullParserException, IOException {
		Icon icon = new Icon();
		String IconName = parser.getAttributeValue("", "Icon").trim();
		String imagePath = basePath + "images" + File.separator + IconName;
		icon.setNormal(imagePath);
		String ID = parser.getAttributeValue("", "ID").trim();
		if (ID.equals("Music")) {
			skinInfo.setMusicIcon(icon);
		} else if (ID.equals("Favorite")) {
			skinInfo.setFavoriteIcon(icon);
		} else if (ID.equals("Download")) {
			skinInfo.setDownloadIcon(icon);
		} else if (ID.equals("TitleIcon")) {
			skinInfo.setTitleIcon(icon);
		} else if (ID.equals("TitleSearchIcon")) {
			skinInfo.setTitleSearchIcon(icon);
		} else if (ID.equals("RandomPlay")) {
			skinInfo.setRandomPlay(icon);
		} else if (ID.equals("PlayBarDefArtist")) {
			skinInfo.setPlayBarDefArtistIcon(icon);
		} else if (ID.equals("TitleBackIcon")) {
			skinInfo.setTitleBackIcon(icon);
		} else if (ID.equals("")) {

		}
	}

	/**
	 * 设置皮肤数据
	 * 
	 * @param skinInfo
	 *            皮肤实体类
	 * @param parser
	 *            解析器
	 * @param basePath
	 *            解压后的基本路径
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static void initSkinColorData(SkinInfo skinInfo,
			XmlPullParser parser, String basePath)
			throws XmlPullParserException, IOException {
		String ParetnID = parser.getAttributeValue("", "ParentID").trim();
		String ID = parser.getAttributeValue("", "ID").trim();
		String value = parser.getAttributeValue("", "Normal").trim();
		if (ParetnID.equals("Common")) {
			if (ID.equals("TitleText")) {
				Text commonTitleText = new Text();
				commonTitleText.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				commonTitleText.setSelected(parserColor(value2));
				skinInfo.setCommonTitleText(commonTitleText);
			} else if (ID.equals("SubTitleText")) {
				Text commonSubTitleText = new Text();
				commonSubTitleText.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				commonSubTitleText.setSelected(parserColor(value2));
				skinInfo.setCommonSubTitleText(commonSubTitleText);
			} else if (ID.equals("ButtonPressBgColor")) {
				skinInfo.setButtonPressBgColor(parserColor(value));
			}
		} else if (ParetnID.equals("Title")) {
			if (ID.equals("TitleText")) {
				skinInfo.setTitleColor(parserColor(value));
			} else if (ID.equals("Background")) {
				skinInfo.setTitleBackgroundColor(parserColor(value));
			}
		} else if (ParetnID.equals("Indicator")) {
			if (ID.equals("Background")) {
				skinInfo.setIndicatorBackgroundColor(parserColor(value));
			} else if (ID.equals("TitleText")) {
				Text indicatorTitleText = new Text();
				indicatorTitleText.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				indicatorTitleText.setSelected(parserColor(value2));
				skinInfo.setIndicatorTitleText(indicatorTitleText);
			} else if (ID.equals("IndicatorLine")) {
				skinInfo.setIndicatorLineBackgroundColor(parserColor(value));
			}
		} else if (ParetnID.equals("Card")) {
			if (ID.equals("Item")) {
				BackgroundColor itemBackgroundColor = new BackgroundColor();
				itemBackgroundColor.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				itemBackgroundColor.setSelected(parserColor(value2));
				skinInfo.setItemBackgroundColor(itemBackgroundColor);
			} else if (ID.equals("ItemDivider")) {
				skinInfo.setItemDividerBackgroundColor(parserColor(value));
			} else if (ID.equals("PopdownItem")) {
				BackgroundColor itemBackgroundColor = new BackgroundColor();
				itemBackgroundColor.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				itemBackgroundColor.setSelected(parserColor(value2));
				skinInfo.setPopdownItemBackgroundColor(itemBackgroundColor);
			} else if (ID.equals("ListItem")) {
				BackgroundColor itemBackgroundColor = new BackgroundColor();
				itemBackgroundColor.setNormal(parserColor(value));
				String value2 = parser.getAttributeValue("", "Selected").trim();
				itemBackgroundColor.setSelected(parserColor(value2));
				skinInfo.setListItemBackgroundColor(itemBackgroundColor);
			}
		} else if (ParetnID.equals("PlayBar")) {
			if (ID.equals("PlayBarDivider")) {
				skinInfo.setPlayBarDividerBackgroundColor(parserColor(value));
			} else if (ID.equals("Background")) {
				skinInfo.setPlayBarBackgroundColor(parserColor(value));
			}
		} else if (ParetnID.equals("Menu")) {
			if (ID.equals("Background")) {
				skinInfo.setMenuBackgroundColor(parserColor(value));
			} else if (ID.equals("Title")) {
				skinInfo.setMenuTitleColor(parserColor(value));
			} else if (ID.equals("MenuItemPG")) {
				skinInfo.setMenuItemPressBgColor(parserColor(value));
			}
		} else if (ParetnID.equals("")) {

		}

	}

	/**
	 * 解析颜色字符串
	 * 
	 * @param value
	 *            颜色字符串 #edf8fc,255
	 * @return
	 */
	private static int parserColor(String value) {
		String regularExpression = ",";
		if (value.contains(regularExpression)) {
			String[] temp = value.split(regularExpression);

			int color = Color.parseColor(temp[0]);
			int alpha = Integer.valueOf(temp[1]);
			int red = (color & 0xff0000) >> 16;
			int green = (color & 0x00ff00) >> 8;
			int blue = (color & 0x0000ff);

			return Color.argb(alpha, red, green, blue);
		}
		return Color.parseColor(value);
	}
}
