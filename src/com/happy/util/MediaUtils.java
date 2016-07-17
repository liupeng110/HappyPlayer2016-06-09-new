package com.happy.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import com.happy.model.app.SongInfo;
import com.zlm.audio.AudioFileReader;
import com.zlm.audio.model.AudioInfo;
import com.zlm.audio.util.AudioUtil;

public class MediaUtils {
	/**
	 * 通过文件获取mp3的相关数据信息
	 * 
	 * @param filePath
	 * @return
	 */

	public static SongInfo getSongInfoByFile(String filePath) {
		File sourceFile = new File(filePath);
		if (!sourceFile.exists())
			return null;
		SongInfo songInfo = null;
		try {

			AudioFileReader audioFileReader = AudioUtil
					.getAudioFileReaderByFilePath(filePath);
			if (audioFileReader == null)
				return null;

			// AudioFileIO.logger.setLevel(Level.SEVERE);
			// ID3v23Frame.logger.setLevel(Level.SEVERE);
			// ID3v23Tag.logger.setLevel(Level.SEVERE);
			// MP3File mp3file = new MP3File(sourceFile);
			// MP3AudioHeader header = mp3file.getMP3AudioHeader();
			// if (header == null)
			// return null;
			songInfo = new SongInfo();
			// 歌曲时长
			// String durationStr = header.getTrackLengthAsString();
			// long duration = getTrackLength(durationStr);
			// if (sourceFile.length() < 1024 * 1024 || duration < 5000) {
			// return null;
			// }

			AudioInfo audioInfo = audioFileReader.read(sourceFile);
			String durationStr = audioInfo.getDurationStr();
			long duration = audioInfo.getDuration();
			if (sourceFile.length() < 1024 * 1024 || duration < 5000) {
				return null;
			}

			String fileExt = MediaUtils.getFileExt(sourceFile.getPath());
			songInfo.setFileExt(fileExt);

			// 文件名
			String displayName = sourceFile.getName();
			int pos = displayName.lastIndexOf(".");
			if (pos != -1) {
				displayName = displayName.substring(0, pos);
			}
			String artist = "";
			String title = "";
			if (displayName.contains("-")) {
				String[] titleArr = displayName.split("-");
				artist = titleArr[0].trim();
				title = titleArr[1].trim();
			} else {
				title = displayName;
			}

			songInfo.setSid(IDGenerate.getId());
			songInfo.setDisplayName(displayName);
			songInfo.setSinger(artist);
			songInfo.setTitle(title);
			songInfo.setDuration(duration);
			songInfo.setDurationStr(durationStr);
			songInfo.setSize(sourceFile.length());
			songInfo.setSizeStr(getFileSize(sourceFile.length()));
			songInfo.setFilePath(filePath);
			songInfo.setType(SongInfo.LOCALSONG);
			songInfo.setIslike(SongInfo.UNLIKE);
			songInfo.setDownloadStatus(SongInfo.DOWNLOADED);
			songInfo.setCreateTime(DateUtil.dateToString(new Date()));

			// mp3file = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return songInfo;

	}

	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(int time) {

		time /= 1000;
		int minute = time / 60;
		// int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	/**
	 * 计算文件的大小，返回相关的m字符串
	 * 
	 * @param fileS
	 * @return
	 */
	public static String getFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static String getFileExt(String filePath) {
		int pos = filePath.lastIndexOf(".");
		if (pos == -1)
			return "";
		return filePath.substring(pos + 1).toLowerCase();
	}

	/**
	 * 获取歌曲长度
	 * 
	 * @param trackLengthAsString
	 * @return
	 */
	private static long getTrackLength(String trackLengthAsString) {

		if (trackLengthAsString.contains(":")) {
			String temp[] = trackLengthAsString.split(":");
			if (temp.length == 2) {
				int m = Integer.parseInt(temp[0]);// 分
				int s = Integer.parseInt(temp[1]);// 秒
				int currTime = (m * 60 + s) * 1000;
				return currTime;
			}
		}
		return 0;
	}
}
