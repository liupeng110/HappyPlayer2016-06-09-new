package com.happy.util;

import java.io.File;

/**
 * 音乐文件的过滤
 * 
 */
public class AudioFilter {

	/**
	 * 
	 * @param f
	 * @return
	 */
	public static boolean acceptFilter(File f) {
		String fileName = f.getName().toLowerCase();
		return fileName.endsWith(".mp3") || fileName.endsWith(".flac")
				|| fileName.endsWith(".ape") || fileName.endsWith(".wav");
	}

}
