package com.happy.widget.lrc;

import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.happy.common.Constants;
import com.happy.model.app.KscLyricsLineInfo;
import com.happy.model.app.MessageIntent;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.R;
import com.happy.util.KscLyricsParserUtil;

/**
 * 锁屏多行歌词视图
 * 
 * @author Administrator
 * 
 */
public class LrcKscManyLineLyricsView extends View implements Observer {
	/**
	 * 是否有ksc歌词
	 */
	private boolean hasKsc = false;
	/**
	 * 是否画时间线
	 * 
	 * **/
	private boolean mIsDrawTimeLine = false;

	/**
	 * 默认画笔
	 */
	private Paint paint;
	/**
	 * 默认高亮未读画笔
	 */
	private Paint paintHLDEF;
	/**
	 * 高亮已读画笔
	 */
	private Paint paintHLED;

	// /**
	// * 轮廓画笔
	// */
	// private Paint paintBackgruond;

	/** 画时间线的画时间线 ***/
	private Paint mPaintForTimeLine;
	/**
	 * 显示放大缩小的歌词文字的大小值
	 */
	private int SCALEIZEWORDDEF = 15;
	/**
	 * 默认字体大小
	 */
	private int SIZEWORD = SCALEIZEWORDDEF;
	/**
	 * 高亮字体大小
	 */
	private int SIZEWORDHL = 0;

	/**
	 * 歌词每行的间隔
	 */
	private int INTERVAL = 30;

	private Context context;

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
	 * 上一行歌词,方便缩小字体
	 */
	private int oldLyricsLineNum = -1;
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

	/** 控制文字缩放的因子 **/
	private float mCurFraction = 1.0f;

	/**
	 * 歌词在Y轴上的偏移量
	 */
	private float offsetY = 0;
	/**
	 * 歌词在Y轴上的上一次偏移量
	 */
	private float oldOffsetY = 0;

	/**
	 * 歌词滑动的最大进度
	 */
	private int scrollMaxYProgress;
	/**
	 * 是否正在滑动
	 */
	private boolean blScroll = false;
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

	public LrcKscManyLineLyricsView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LrcKscManyLineLyricsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LrcKscManyLineLyricsView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		this.context = context;

		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/weiruanyahei14M.ttf");

		paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setTypeface(typeFace);

		paintHLDEF = new Paint();
		paintHLDEF.setDither(true);
		paintHLDEF.setAntiAlias(true);
		paintHLDEF.setColor(Color.WHITE);
		paintHLDEF.setTypeface(typeFace);

		paintHLED = new Paint();
		paintHLED.setDither(true);
		paintHLED.setAntiAlias(true);
		paintHLED.setTypeface(typeFace);

		// paintBackgruond = new Paint();
		// paintBackgruond.setColor(Color.BLACK);
		// paintBackgruond.setTypeface(typeFace);
		// paintBackgruond.setDither(true);
		// paintBackgruond.setAntiAlias(true);

		mPaintForTimeLine = new Paint();
		mPaintForTimeLine.setDither(true);
		mPaintForTimeLine.setAntiAlias(true);
		mPaintForTimeLine.setTypeface(typeFace);

		initColor();

		ObserverManage.getObserver().addObserver(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (SIZEWORDHL == 0) {
			initSizeWord();
		}

		if (!hasKsc) {
			// 根据提示语的长度，和歌词视图的宽度来设置字体的大小
			String tip = context.getString(R.string.lrc_tip);
			drawDefText(canvas, tip);
		} else {
			drawLrcText(canvas);
		}

		// 画时间线和时间线
		if (mIsDrawTimeLine) {

			mPaintForTimeLine.setTextSize(SIZEWORDHL);

			String timeStr = kscLyricsParser.timeParserString(progress);
			FontMetrics fm = mPaintForTimeLine.getFontMetrics();
			int height = (int) Math.ceil(fm.descent - fm.top) + 2;
			float y = getHeight() / 2 + getScrollY();

			// paintBackgruond.setTextSize(SIZEWORDHL);
			// drawBackground(canvas, timeStr, 0, y + height);

			canvas.drawText(timeStr, 0, y + height, mPaintForTimeLine);
			canvas.drawLine(0, y, getWidth(), y, mPaintForTimeLine);
		}

		super.onDraw(canvas);
	}

	/**
	 * 初始化字体大小
	 */
	private void initSizeWord() {

		fontSizeScale = Constants.lrcFontSize + 100;
		SCALEIZEWORDDEF = (int) ((float) fontSizeScale / 100 * SIZEWORD);
		SIZEWORDHL = SCALEIZEWORDDEF + 8;
	}

	/***
	 * 被始化颜色
	 */
	private void initColor() {
		paintHLED
				.setColor(parserColor(Constants.lrcColorStr[Constants.lrcColorIndex]));
		mPaintForTimeLine
				.setColor(parserColor(Constants.lrcColorStr[Constants.lrcColorIndex]));

	}

	/**
	 * 绘画默认的文本内容
	 * 
	 * @param canvas
	 */
	private void drawDefText(Canvas canvas, String tip) {

		paintHLDEF.setTextSize(SIZEWORDHL);
		paintHLED.setTextSize(SIZEWORDHL);

		FontMetrics fm = paintHLDEF.getFontMetrics();
		float textWidth = paintHLDEF.measureText(tip);// 用画笔测量歌词的宽度
		int textHeight = (int) Math.ceil(fm.descent - fm.top) + 2;
		//
		// LinearGradient linearGradient = new LinearGradient(0, 0, 0,
		// height,
		// new int[] { 0x33ffffff, 0xffffffff, 0x33ffffff }, new float[] {
		// 0, 0.5f, 1 }, Shader.TileMode.MIRROR);

		// LinearGradient linearGradient = new LinearGradient(0, 0, 0,
		// height,
		// new int[] { parserColor("#fe0000"), parserColor("#ff8041"),
		// parserColor("#ffff00") }, null, Shader.TileMode.MIRROR);

		// paint.setShader(linearGradient);

		// paint.setColor(Color.WHITE);

		// 未读
		// LinearGradient linearGradient = new LinearGradient(0, 0, 0,
		// textHeight,
		// new int[] { parserColor("#00348a"), parserColor("#0080c0"),
		// parserColor("#03cafc") }, null, Shader.TileMode.MIRROR);
		// paintHLDEF.setShader(linearGradient);

		// drawBackground(canvas, tip, (getWidth() - textWidth) / 2,
		// (getHeight() + height) / 2);

		// save和restore是为了剪切操作不影响画布的其它元素
		canvas.save();

		float leftX = (getWidth() - textWidth) / 2;
		float heightY = (getHeight() + textHeight) / 2;

		canvas.drawText(tip, leftX, heightY, paintHLDEF);

		// 已经读
		// LinearGradient linearGradiented = new LinearGradient(0, 0, 0,
		// textHeight, new int[] { parserColor("#82f7fd"),
		// parserColor("#ffffff"), parserColor("#03e9fc") }, null,
		// Shader.TileMode.MIRROR);
		// paintHLED.setShader(linearGradiented);

		// 设置过渡的颜色和进度
		canvas.clipRect(leftX, heightY - textHeight, leftX + textWidth / 2,
				heightY + textHeight);

		// drawBackground(canvas, tip, (getWidth() - textWidth) / 2,
		// (getHeight() + height) / 2);

		canvas.drawText(tip, leftX, heightY, paintHLED);
		canvas.restore();
	}

	// /**
	// * 描绘轮廓
	// *
	// * @param canvas
	// * @param string
	// * @param x
	// * @param y
	// */
	// private void drawBackground(Canvas canvas, String string, float x, float
	// y) {
	// canvas.drawText(string, x - 1, y, paintBackgruond);
	// canvas.drawText(string, x + 1, y, paintBackgruond);
	// canvas.drawText(string, x, y + 1, paintBackgruond);
	// canvas.drawText(string, x, y - 1, paintBackgruond);
	// }

	/**
	 * 绘画歌词文本
	 * 
	 * @param canvas
	 */
	private void drawLrcText(Canvas canvas) {
		int alphaValue = 5;
		// 画当前歌词之前的歌词
		for (int i = lyricsLineNum - 1; i >= 0; i--) {
			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i < (SCALEIZEWORDDEF)) {
				break;
			}

			if (i == oldLyricsLineNum) {
				// 因为有缩放效果，有需要动态设置歌词的字体大小
				float textSize = SIZEWORDHL - (SIZEWORDHL - SCALEIZEWORDDEF)
						* mCurFraction;
				paint.setTextSize(textSize);
			} else {// 画其他的歌词
				paint.setTextSize(SCALEIZEWORDDEF);
			}

			String text = lyricsLineTreeMap.get(i).getLineLyrics();
			float textWidth = paint.measureText(text);
			float textX = (getWidth() - textWidth) / 2;

			// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
			textX = Math.max(textX, 10);

			paint.setColor(Color.argb(255 - alphaValue, 255, 255, 255));

			canvas.drawText(text, textX, offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* i, paint);

			alphaValue += 10;

		}

		alphaValue = 5;
		// 画当前歌词之后的歌词
		for (int i = lyricsLineNum + 1; i < lyricsLineTreeMap.size(); i++) {
			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i > getHeight()
					- (SCALEIZEWORDDEF)) {
				break;
			}

			if (i == oldLyricsLineNum) {
				// 因为有缩放效果，有需要动态设置歌词的字体大小
				float textSize = SIZEWORDHL - (SIZEWORDHL - SCALEIZEWORDDEF)
						* mCurFraction;
				paint.setTextSize(textSize);
			} else {// 画其他的歌词
				paint.setTextSize(SCALEIZEWORDDEF);
			}

			String text = lyricsLineTreeMap.get(i).getLineLyrics();
			float textWidth = paint.measureText(text);
			float textX = (getWidth() - textWidth) / 2;
			// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
			textX = Math.max(textX, 10);

			paint.setColor(Color.argb(255 - alphaValue, 255, 255, 255));
			canvas.drawText(text, textX, offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* i, paint);

			alphaValue += 10;
		}

		// 画当前高亮的歌词行
		if (lyricsLineNum != -1) {

			// float textX = (getWidth() - lineLyricsWidth) / 2;
			// 普通歌词
			// canvas.drawText(lineLyrics, textX, offsetY + (SIZEWORD +
			// INTERVAL)
			// * lyricsLineNum, paintHLED);

			// 因为有缩放效果，有需要动态设置歌词的字体大小
			float textSize = SCALEIZEWORDDEF + (SIZEWORDHL - SCALEIZEWORDDEF)
					* mCurFraction;
			paintHLDEF.setTextSize(textSize);
			paintHLED.setTextSize(textSize);

			KscLyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap
					.get(lyricsLineNum);
			// 整行歌词
			String lineLyrics = kscLyricsLineInfo.getLineLyrics();
			float lineLyricsWidth = paintHLED.measureText(lineLyrics);

			// ktv歌词
			if (lyricsWordIndex == -1) {
				lineLyricsHLWidth = lineLyricsWidth;
			} else {
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
			}

			float textX = 0;
			if (lineLyricsWidth > getWidth()) {
				if (lineLyricsHLWidth >= getWidth() / 2) {
					if ((lineLyricsWidth - lineLyricsHLWidth) >= getWidth() / 2) {
						highLightLrcMoveX = (getWidth() / 2 - lineLyricsHLWidth);
					} else {
						highLightLrcMoveX = getWidth() - lineLyricsWidth - 10;
					}
				} else {
					highLightLrcMoveX = 10;
				}
				// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
				textX = highLightLrcMoveX;
			} else {
				// 如果歌词宽度小于view的宽
				textX = (getWidth() - lineLyricsWidth) / 2;
			}

			// save和restore是为了剪切操作不影响画布的其它元素
			canvas.save();

			// 画当前歌词
			canvas.drawText(lineLyrics, textX, offsetY
					+ (SCALEIZEWORDDEF + INTERVAL) * lyricsLineNum, paintHLDEF);

			// ktv过渡效果
			FontMetrics fm = paintHLDEF.getFontMetrics();
			int height = (int) Math.ceil(fm.descent - fm.top) + 2;
			canvas.clipRect(textX, offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* lyricsLineNum - height, textX + lineLyricsHLWidth,
					offsetY + (SCALEIZEWORDDEF + INTERVAL) * lyricsLineNum
							+ height);

			// 画当前歌词
			canvas.drawText(lineLyrics, textX, offsetY
					+ (SCALEIZEWORDDEF + INTERVAL) * lyricsLineNum, paintHLED);
			canvas.restore();
		}
	}

	private float startRawX = 0, startRawY = 0;
	/**
	 * 当触摸歌词View时，保存为当前触点的Y轴坐标
	 * 
	 * 滑动的进度
	 */
	private float touchY = 0;

	/**
	 * 滑动事件 滑动歌词来对歌曲进行快进，原理好简单，先获取 你滑动的距离，每滑动单位1，则歌曲播放进度 100ms 这样就完成了歌词对歌曲的快进
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!hasKsc) {
			return super.onTouchEvent(event);
		}
		float tt = event.getY();
		float rawX = event.getRawX();
		int sumX = (int) (rawX - startRawX);
		int sumY = (int) (event.getRawY() - startRawY);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startRawX = event.getRawX();
			startRawY = event.getRawY();

			// if (hideTime < 0) {
			// hideTime = 200;
			// mHandler.post(upDateTime);
			// } else {
			// hideTime = 200;
			// }

			break;
		case MotionEvent.ACTION_MOVE:

			if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10) {

				blScroll = true;
				mIsDrawTimeLine = true;

				touchY = tt - touchY;
				progress = (int) (progress - touchY * 100);
				if (progress < 0) {
					progress = 0;
				}
				if (progress > scrollMaxYProgress) {
					progress = scrollMaxYProgress;
				}

				showLrc(progress);
				invalidate();
			}

			// if (hideTime < 0) {
			// hideTime = 200;
			// mHandler.post(upDateTime);
			// } else {
			// hideTime = 200;
			// }

			break;
		case MotionEvent.ACTION_UP:
			blScroll = false;
			mIsDrawTimeLine = false;
			invalidate();
			if (sumX > -10 && sumX < 10 && sumY > -10 && sumY < 10) {
				if (onLrcClickListener != null) {
					onLrcClickListener.onClick();
				}
			} else {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress(progress);
				ObserverManage.getObserver().setMessage(songMessage);
			}

			startRawX = 0;
			startRawY = 0;

			break;
		}
		touchY = tt;
		return false;
	}

	// private int hideTime = -1;

	// private Handler mHandler = new Handler() {
	//
	// };

	// Runnable upDateTime = new Runnable() {
	//
	// @Override
	// public void run() {
	// if (hideTime >= 0) {
	// hideTime -= 50;
	// mHandler.postDelayed(upDateTime, 10);
	// } else {
	// blScroll = false;
	// mIsDrawTimeLine = false;
	// invalidate();
	// }
	//
	// }
	// };

	/**
	 * * @param scrollMaxYProgress 最大滑动进度
	 * 
	 * @param lyricsLineTreeMap
	 */
	public void init(int scrollMaxYProgress, KscLyricsParserUtil kscLyricsParser) {

		this.scrollMaxYProgress = scrollMaxYProgress;
		this.kscLyricsParser = kscLyricsParser;
		lyricsLineTreeMap = kscLyricsParser.getLyricsLineTreeMap();
		offsetY = getHeight() / 2 + (SCALEIZEWORDDEF + INTERVAL);
		oldLyricsLineNum = -1;
		lyricsLineNum = -1;
		lyricsWordIndex = -1;
		lineLyricsHLWidth = 0;
		lyricsWordHLEDTime = 0;
		highLightLrcMoveX = 0;
		mCurFraction = 1.0f;

		invalidate();
	}

	/**
	 * 根据播放进度绘画歌词
	 * 
	 * @param playProgress
	 */
	public void showLrc(int playProgress) {
		// 非滑动情况下，保存当前的播放进度
		if (!blScroll) {
			this.progress = playProgress;
		}
		if (kscLyricsParser == null)
			return;
		int newLyricsLineNum = kscLyricsParser
				.getLineNumberFromCurPlayingTime(playProgress);
		if (newLyricsLineNum == -1) {
			offsetY = getHeight() / 2 + (SCALEIZEWORDDEF + INTERVAL);
			// 进度为0，初始化相关的数据
			lyricsLineNum = -1;
			lyricsWordIndex = -1;
			lineLyricsHLWidth = 0;
			lyricsWordHLEDTime = 0;
			highLightLrcMoveX = 0;
			mCurFraction = 1.0f;
		} else {
			// 往上下移动的总距离,字体大小改变后，要修改oldOffsetY的位置
			int sy = (SCALEIZEWORDDEF + INTERVAL);
			if (lyricsLineNum != newLyricsLineNum
					|| oldFontSizeScale != fontSizeScale) {
				lyricsLineNum = newLyricsLineNum;
				oldOffsetY = getHeight() / 2 - (SCALEIZEWORDDEF + INTERVAL)
						* lyricsLineNum + sy;
			}
			// 每次view刷新时移动往上下移动的距离,设置时间，就会有动画的效果
			float dy = kscLyricsParser.getOffsetDYFromCurPlayingTime(
					lyricsLineNum, playProgress, sy);
			offsetY = oldOffsetY - dy;

			// 另一行歌词，所以把之前设置的高亮移动显示的x坐标设置为0
			highLightLrcMoveX = 0;

			if (newLyricsLineNum > lyricsLineNum) {
				oldLyricsLineNum = newLyricsLineNum + 1;
			} else {
				oldLyricsLineNum = newLyricsLineNum - 1;
			}
			mCurFraction = dy / sy;
		}

		lyricsWordIndex = kscLyricsParser.getDisWordsIndexFromCurPlayingTime(
				lyricsLineNum, playProgress);

		lyricsWordHLEDTime = kscLyricsParser.getLenFromCurPlayingTime(
				lyricsLineNum, playProgress);

		if (!blScroll && oldFontSizeScale == fontSizeScale) {
			// 字体在切换时，不进行刷新，免得会出现闪屏的问题
			invalidate();
		}
		if (oldFontSizeScale != fontSizeScale) {
			oldFontSizeScale = fontSizeScale;
		}
	}

	/**
	 * 获取快进时的时间歌词 供进度条使用
	 * 
	 * @param playProgress
	 */
	public String getTimeLrc(int playProgress) {
		String lrc = "";
		if (!hasKsc)
			return lrc;
		if (kscLyricsParser == null)
			return lrc;
		int index = kscLyricsParser
				.getLineNumberFromCurPlayingTime(playProgress);
		if (lyricsLineTreeMap == null || index >= lyricsLineTreeMap.size())
			return lrc;
		KscLyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap.get(index);
		if (kscLyricsLineInfo == null)
			return lrc;
		lrc = kscLyricsLineInfo.getLineLyrics();
		return lrc;
	}

	public boolean getBlScroll() {
		return blScroll;
	}

	public void setBlScroll(boolean blScroll) {
		this.blScroll = blScroll;
		mIsDrawTimeLine = false;
		invalidate();
	}

	public boolean getHasKsc() {
		return hasKsc;
	}

	public void setHasKsc(boolean hasKsc) {
		this.hasKsc = hasKsc;
		invalidate();
	}

	/**
	 * 解析颜色字符串
	 * 
	 * @param value
	 *            颜色字符串 #edf8fc,255
	 * @return
	 */
	private int parserColor(String value) {
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

	private OnLrcClickListener onLrcClickListener;

	public void setOnLrcClickListener(OnLrcClickListener onLrcClickListener) {
		this.onLrcClickListener = onLrcClickListener;
	}

	public interface OnLrcClickListener {
		void onClick();
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(
					MessageIntent.KSCMANYLINELRCCOLOR)) {
				initColor();
				invalidate();
			} else if (messageIntent.getAction().equals(
					MessageIntent.KSCMANYLINEFONTSIZE)) {
				initSizeWord();
				showLrc(progress);
				invalidate();
			}
		}
	}

}
