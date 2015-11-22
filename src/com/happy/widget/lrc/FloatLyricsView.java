package com.happy.widget.lrc;

import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.happy.common.Constants;
import com.happy.model.app.KscLyricsLineInfo;
import com.happy.model.app.MessageIntent;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.KscLyricsParserUtil;

public class FloatLyricsView extends View implements Observer {
	/**
	 * 是否有ksc歌词
	 */
	private boolean hasKsc = false;

	private Context context;
	/**
	 * 默认高亮未读画笔
	 */
	private Paint paintHLDEF;
	/**
	 * 高亮已读画笔
	 */
	private Paint paintHLED;

	/**
	 * 轮廓画笔
	 */
	private Paint paintBackgruond;

	/**
	 * 显示放大缩小的歌词文字的大小值
	 */
	private float SCALEIZEWORDDEF = 0;

	/**
	 * 歌词每行的间隔
	 */
	private float INTERVAL = 0;

	/**
	 * 歌词解析
	 */
	private KscLyricsParserUtil kscLyricsParser;

	/**
	 * 歌词列表
	 */
	private TreeMap<Integer, KscLyricsLineInfo> lyricsLineTreeMap;

	/**
	 * 当前歌词的所在行数
	 */
	private int lyricsLineNum = -1;

	/**
	 * 当前歌词的第几个字
	 */
	private int lyricsWordIndex = -1;

	/**
	 * 当前歌词第几个字 已经播放的时间
	 */
	private int lyricsWordHLEDTime = 0;

	/**
	 * 当前歌词第几个字 已经播放的长度
	 */
	private float lineLyricsHLWidth = 0;

	/** 高亮歌词当前的其实x轴绘制坐标 **/
	private float highLightLrcMoveX;

	/***
	 * 播放进度
	 */
	private int progress = 0;

	/**
	 * 字体大小缩放比例
	 */
	private int fontSizeScale = 0;
	/**
	 * 字体大小缩放比例
	 */
	private int oldFontSizeScale = 0;

	public FloatLyricsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FloatLyricsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FloatLyricsView(Context context) {
		super(context);
		init(context);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}

	};

	private void init(Context context) {
		this.context = context;

		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/weiruanyahei14M.ttf");

		paintHLDEF = new Paint();
		paintHLDEF.setDither(true);
		paintHLDEF.setAntiAlias(true);
		paintHLDEF.setTypeface(typeFace);

		paintHLED = new Paint();
		paintHLED.setDither(true);
		paintHLED.setAntiAlias(true);
		paintHLED.setTypeface(typeFace);

		paintBackgruond = new Paint();
		paintBackgruond.setColor(Color.BLACK);
		paintBackgruond.setTypeface(typeFace);
		paintBackgruond.setDither(true);
		paintBackgruond.setAntiAlias(true);

		initColor();
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 初始化颜色
	 */
	private void initColor() {
		paintHLDEF
				.setColor(Constants.DESLRCNOREADCOLOR[Constants.desktopLrcIndex]);
		paintHLED
				.setColor(Constants.DESLRCREADEDCOLOR[Constants.desktopLrcIndex]);
	}

	@Override
	public void draw(Canvas canvas) {
		if (SCALEIZEWORDDEF == 0) {
			initSizeWord();
		}
		if (!hasKsc) {
			// 根据提示语的长度，和歌词视图的宽度来设置字体的大小
			String tip = context.getString(R.string.lrc_tip);
			drawDefText(canvas, tip);
		} else {
			drawLrcText(canvas);
		}
		super.draw(canvas);
	}

	/**
	 * 初始化字体大小
	 */
	private void initSizeWord() {
		fontSizeScale = Constants.desktopLrcFontSize;
		int height = getHeight();
		float maxSizeWord = (float) (height - 8 * 3) / 2;
		SCALEIZEWORDDEF = fontSizeScale * maxSizeWord
				/ Constants.desktopLrcFontMaxSize;

		INTERVAL = (float) (height - SCALEIZEWORDDEF * 2) / 3;

		paintHLDEF.setTextSize(SCALEIZEWORDDEF);
		paintHLED.setTextSize(SCALEIZEWORDDEF);
		paintBackgruond.setTextSize(SCALEIZEWORDDEF);
	}

	/**
	 * 画歌词
	 * 
	 * @param canvas
	 */
	private void drawLrcText(Canvas canvas) {

		// 画之前的歌词
		if (lyricsLineNum == -1) {
			String lyricsLeft = lyricsLineTreeMap.get(0).getLineLyrics();

			drawBackground(canvas, lyricsLeft, 10, SCALEIZEWORDDEF + INTERVAL);
			canvas.drawText(lyricsLeft, 10, SCALEIZEWORDDEF + INTERVAL,
					paintHLDEF);
			if (lyricsLineNum + 2 < lyricsLineTreeMap.size()) {
				String lyricsRight = lyricsLineTreeMap.get(lyricsLineNum + 2)
						.getLineLyrics();

				float lyricsRightWidth = paintHLDEF.measureText(lyricsRight);
				float textRightX = getWidth() - lyricsRightWidth - 10;
				// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
				textRightX = Math.max(textRightX, 10);
				drawBackground(canvas, lyricsRight, textRightX,
						(SCALEIZEWORDDEF + INTERVAL) * 2);

				canvas.drawText(lyricsRight, textRightX,
						(SCALEIZEWORDDEF + INTERVAL) * 2, paintHLDEF);
			}
		} else {

			// 先设置当前歌词，之后再根据索引判断是否放在左边还是右边

			KscLyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap
					.get(lyricsLineNum);
			// 当行歌词
			String currentLyrics = kscLyricsLineInfo.getLineLyrics();
			float currentTextWidth = paintHLED.measureText(currentLyrics);// 用画笔测量歌词的宽度
			FontMetrics fm = paintHLED.getFontMetrics();
			float currentTextHeight = (int) Math.ceil(fm.descent - fm.top) + 2;

			if (lyricsWordIndex != -1) {

				String lyricsWords[] = kscLyricsLineInfo.getLyricsWords();
				int wordsDisInterval[] = kscLyricsLineInfo
						.getWordsDisInterval();
				// 当前歌词之前的歌词
				String lyricsBeforeWord = "";
				for (int i = 0; i < lyricsWordIndex; i++) {
					lyricsBeforeWord += lyricsWords[i];
				}
				// 当前歌词
				String lyricsNowWord = lyricsWords[lyricsWordIndex].trim();// 去掉空格
				// 当前歌词之前的歌词长度
				float lyricsBeforeWordWidth = paintHLED
						.measureText(lyricsBeforeWord);

				// 当前歌词长度
				float lyricsNowWordWidth = paintHLED.measureText(lyricsNowWord);

				float len = lyricsNowWordWidth
						/ wordsDisInterval[lyricsWordIndex]
						* lyricsWordHLEDTime;
				lineLyricsHLWidth = lyricsBeforeWordWidth + len;
			} else {
				// 整行歌词
				lineLyricsHLWidth = currentTextWidth;
			}
			// 当前歌词行的x坐标
			float textX = 0;

			// 当前歌词行的y坐标
			float textY = 0;
			if (lyricsLineNum % 2 == 0) {

				if (currentTextWidth > getWidth()) {
					if (lineLyricsHLWidth >= getWidth() / 2) {
						if ((currentTextWidth - lineLyricsHLWidth) >= getWidth() / 2) {
							highLightLrcMoveX = (getWidth() / 2 - lineLyricsHLWidth);
						} else {
							highLightLrcMoveX = getWidth() - currentTextWidth
									- 10;
						}
					} else {
						highLightLrcMoveX = 10;
					}
					// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
					textX = highLightLrcMoveX;
				} else {
					// 如果歌词宽度小于view的宽
					textX = 10;
				}

				// 画下一句的歌词
				if (lyricsLineNum + 1 < lyricsLineTreeMap.size()) {
					String lyricsRight = lyricsLineTreeMap.get(
							lyricsLineNum + 1).getLineLyrics();

					float lyricsRightWidth = paintHLDEF
							.measureText(lyricsRight);
					float textRightX = getWidth() - lyricsRightWidth - 10;
					// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
					textRightX = Math.max(textRightX, 10);
					drawBackground(canvas, lyricsRight, textRightX,
							(SCALEIZEWORDDEF + INTERVAL) * 2);

					canvas.drawText(lyricsRight, textRightX,
							(SCALEIZEWORDDEF + INTERVAL) * 2, paintHLDEF);
				}

				textY = (SCALEIZEWORDDEF + INTERVAL);

			} else {

				if (currentTextWidth > getWidth()) {
					if (lineLyricsHLWidth >= getWidth() / 2) {
						if ((currentTextWidth - lineLyricsHLWidth) >= getWidth() / 2) {
							highLightLrcMoveX = (getWidth() / 2 - lineLyricsHLWidth);
						} else {
							highLightLrcMoveX = getWidth() - currentTextWidth
									- 10;
						}
					} else {
						highLightLrcMoveX = 10;
					}
					// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
					textX = highLightLrcMoveX;
				} else {
					// 如果歌词宽度小于view的宽
					textX = getWidth() - currentTextWidth - 10;
				}

				// 画下一句的歌词
				if (lyricsLineNum + 1 != lyricsLineTreeMap.size()) {
					String lyricsLeft = lyricsLineTreeMap
							.get(lyricsLineNum + 1).getLineLyrics();

					drawBackground(canvas, lyricsLeft, 10, SCALEIZEWORDDEF
							+ INTERVAL);

					canvas.drawText(lyricsLeft, 10, SCALEIZEWORDDEF + INTERVAL,
							paintHLDEF);
				}

				textY = (SCALEIZEWORDDEF + INTERVAL) * 2;
			}

			// save和restore是为了剪切操作不影响画布的其它元素
			canvas.save();
			drawBackground(canvas, currentLyrics, textX, textY);
			// 画当前歌词
			canvas.drawText(currentLyrics, textX, textY, paintHLDEF);

			canvas.clipRect(textX, textY - currentTextHeight, textX
					+ lineLyricsHLWidth, textY + currentTextHeight);

			canvas.drawText(currentLyrics, textX, textY, paintHLED);

			canvas.restore();
		}
	}

	/**
	 * 绘画默认歌词提示
	 * 
	 * @param canvas
	 * @param tip
	 */
	private void drawDefText(Canvas canvas, String tip) {

		FontMetrics fm = paintHLDEF.getFontMetrics();
		float textWidth = paintHLDEF.measureText(tip);// 用画笔测量歌词的宽度
		int textHeight = (int) Math.ceil(fm.descent - fm.top) + 2;

		canvas.save();

		float leftX = (getWidth() - textWidth) / 2;
		float heightY = (getHeight() + textHeight) / 2;

		drawBackground(canvas, tip, leftX, heightY);
		canvas.drawText(tip, leftX, heightY, paintHLDEF);

		// 设置过渡的颜色和进度
		canvas.clipRect(leftX, heightY - textHeight, leftX + textWidth / 2,
				heightY + textHeight);

		canvas.drawText(tip, leftX, heightY, paintHLED);
		canvas.restore();

	}

	/**
	 * 描绘轮廓
	 * 
	 * @param canvas
	 * @param string
	 * @param x
	 * @param y
	 */
	private void drawBackground(Canvas canvas, String string, float x, float y) {
		canvas.drawText(string, x - 1, y, paintBackgruond);
		canvas.drawText(string, x + 1, y, paintBackgruond);
		canvas.drawText(string, x, y + 1, paintBackgruond);
		canvas.drawText(string, x, y - 1, paintBackgruond);
	}

	/**
	 * * @param scrollMaxYProgress 最大滑动进度
	 * 
	 * @param lyricsLineTreeMap
	 */
	public void init(KscLyricsParserUtil kscLyricsParser) {

		this.kscLyricsParser = kscLyricsParser;
		lyricsLineTreeMap = kscLyricsParser.getLyricsLineTreeMap();
		lyricsLineNum = -1;
		lyricsWordIndex = -1;
		lineLyricsHLWidth = 0;
		lyricsWordHLEDTime = 0;
		highLightLrcMoveX = 0;

		invalidate();
	}

	/**
	 * 根据播放进度绘画歌词
	 * 
	 * @param playProgress
	 */
	public void showLrc(int playProgress) {
		this.progress = playProgress;
		if (kscLyricsParser == null)
			return;
		int newLyricsLineNum = kscLyricsParser
				.getLineNumberFromCurPlayingTime(playProgress);
		if (newLyricsLineNum != lyricsLineNum) {
			lyricsLineNum = newLyricsLineNum;
			highLightLrcMoveX = 0;
		}
		lyricsWordIndex = kscLyricsParser.getDisWordsIndexFromCurPlayingTime(
				lyricsLineNum, playProgress);

		lyricsWordHLEDTime = kscLyricsParser.getLenFromCurPlayingTime(
				lyricsLineNum, playProgress);

		if (oldFontSizeScale == fontSizeScale) {
			// 字体在切换时，不进行刷新，免得会出现闪屏的问题
			invalidate();
		}
		if (oldFontSizeScale != fontSizeScale) {
			oldFontSizeScale = fontSizeScale;
		}
	}

	public boolean getHasKsc() {
		return hasKsc;
	}

	public void setHasKsc(boolean hasKsc) {
		this.hasKsc = hasKsc;
		invalidate();
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(
					MessageIntent.DESKSCMANYLINELRCCOLOR)) {
				initColor();
				mHandler.sendEmptyMessage(0);
			} else if (messageIntent.getAction().equals(
					MessageIntent.DESKSCMANYLINEFONTSIZE)) {
				initSizeWord();
				showLrc(progress);
				mHandler.sendEmptyMessage(0);
			}
		}
	}
}
