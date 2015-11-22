package com.happy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

import com.happy.logger.LoggerManage;

/**
 * 
 * zip文件解压
 * 
 */
public class UnzipUtil {
	private static LoggerManage logger = null;

	/**
	 * 解压Assets中的文件
	 * 
	 * @param context上下文对象
	 * @param assetName压缩包文件名
	 *            (不包含后缀名) * @param assetPath压缩包的路径
	 * @param outputDirectory输出目录
	 * @throws IOException
	 */
	public static boolean unAssetsZip(Context context, String assetName,
			String assetPath, String outputDirectory) {
		if (logger == null) {
			logger = LoggerManage.getZhangLogger(context);
		}
		// File file = new File(outputDirectory + File.separator + assetName);
		// if (file.exists()) {
		// return true;
		// }
		// 打开压缩文件
		try {
			InputStream inputStream = context.getAssets().open(assetPath);
			return unZip(context, inputStream, outputDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.e(e.toString());
		}
		return false;
	}

	/**
	 * 
	 * @Title: unZip
	 * @Description: (解压文件)
	 * @param: @param context
	 * @param: @param zipName 压缩包文件名(不包含后缀名)
	 * @param: @param zipPath 压缩包路径
	 * @param: @param outputDirectory
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public static boolean unZip(Context context, String zipName,
			String zipPath, String outputDirectory) {
		if (logger == null) {
			logger = LoggerManage.getZhangLogger(context);
		}
		File file = new File(outputDirectory + File.separator + zipName);
		if (file.exists()) {
			return true;
		}
		// 打开压缩文件
		try {
			InputStream inputStream = new FileInputStream(zipPath);
			return unZip(context, inputStream, outputDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.e(e.toString());
		}
		return false;
	}

	/**
	 * 解压文件
	 * 
	 * @param context上下文对象
	 * @param inputStream文件输入流
	 * @param outputDirectory输出目录
	 * @throws IOException
	 */
	private static boolean unZip(Context context, InputStream inputStream,
			String outputDirectory) {
		if (logger == null) {
			logger = LoggerManage.getZhangLogger(context);
		}
		// 创建解压目标目录
		File file = new File(outputDirectory);
		// 如果目标目录不存在，则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			// 读取一个进入点
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			// 使用1Mbuffer
			byte[] buffer = new byte[1024 * 1024];
			// 解压时字节计数
			int count = 0;
			// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
			while (zipEntry != null) {
				// 如果是一个目录
				if (zipEntry.isDirectory()) {
					// String name = zipEntry.getName();
					// name = name.substring(0, name.length() - 1);
					file = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					file.mkdir();
				} else {
					// 如果是文件
					file = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					// 创建该文件
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
				// 定位到下一个文件入口
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.e(e.toString());
		}
		return false;
	}

}