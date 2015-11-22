package com.happy.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.KscLyricsParserUtil;

public class KscLyricsManage {

	/**
	 * 当前歌词的歌曲sid
	 */
	private static String mSid = "";

	/**
	 * 当前歌词解析器
	 */
	private static KscLyricsParserUtil mKscLyricsParser = null;

	/**
	 * 通过歌曲的sid和歌词路径获取歌词解析器
	 * 
	 * @param sid
	 * @param kscFilePath
	 * @return
	 */
	public static KscLyricsParserUtil getKscLyricsParser(String sid,
			String kscFilePath) {
		if (sid.equals(mSid)) {
			if (mKscLyricsParser == null) {
				mKscLyricsParser = new KscLyricsParserUtil(kscFilePath);
			}
		} else {
			mSid = sid;
			mKscLyricsParser = new KscLyricsParserUtil(kscFilePath);
		}
		return mKscLyricsParser;
	}

	/**
	 * 
	 * @param sid
	 * @param kscInputStream
	 * @return
	 */
	public static KscLyricsParserUtil getKscLyricsParserByKscInputStream(
			String sid) {
		if (sid.equals(mSid)) {
			if (mKscLyricsParser == null) {
				mKscLyricsParser = new KscLyricsParserUtil();
			}
		} else {
			mSid = sid;
			mKscLyricsParser = new KscLyricsParserUtil();

		}
		return mKscLyricsParser;
	}

	/**
	 * 解析歌词通过数据流
	 * 
	 * @param kscInputStream
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void parserKscLyricsByKscInputStream(String sid,
			InputStream kscInputStream, int type) throws NumberFormatException,
			IOException {
		mSid = sid;
		if (mKscLyricsParser == null) {
			mKscLyricsParser = new KscLyricsParserUtil();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				kscInputStream, "GB2312"));
		mKscLyricsParser.parserksc(br);

		SongMessage songMessage = new SongMessage();

		if (type == SongMessage.KSCTYPELRC) {

			songMessage.setType(SongMessage.LRCKSCDOWNLOADED);
		} else if (type == SongMessage.KSCTYPEDES) {

			songMessage.setType(SongMessage.DESKSCDOWNLOADED);
		} else if (type == SongMessage.KSCTYPELOCK) {

			songMessage.setType(SongMessage.LOCKKSCDOWNLOADED);
		}
		// songMessage.setKscFilePath(kscFilePath);
		songMessage.setSid(sid);
		// 通知
		ObserverManage.getObserver().setMessage(songMessage);
	}
}
