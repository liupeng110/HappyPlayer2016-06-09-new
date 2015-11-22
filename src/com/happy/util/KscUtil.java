package com.happy.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.happy.common.Constants;
import com.happy.db.SongDB;
import com.happy.manage.KscLyricsManage;
import com.happy.model.app.HttpResult;
import com.happy.model.app.SongMessage;
import com.happy.model.pc.KscInfo;
import com.happy.observable.ObserverManage;

/**
 * 歌词处理类
 * 
 * @author zhangliangming
 * 
 */
public class KscUtil {

	/**
	 * 加载ksc歌词文件
	 * 
	 * @param context
	 * @param sid
	 * @param title
	 * @param singer
	 * @param kscUrl
	 */
	public static void loadKsc(Context context, String sid, String title,
			String singer, String displayName, String kscUrl, int type) {
		// 先判断本地有没有歌词文件
		// 如果有，则加载歌词文件
		// 如果没有，则判断kscUrl是否为空
		// 如果kscUrl为空，则从服务器获取kscUrl的路径，然后下载ksc歌词文件
		// 如果kscUrl不为空，则直接下载ksc歌词文件

		String kscFilePath = Constants.PATH_KSC + File.separator + displayName
				+ ".ksc";

		File kscFile = new File(kscFilePath);
		if (!kscFile.exists()) {
			if (kscUrl == null || kscUrl.equals("")) {
				getKscInfoFormNet(context, sid, title, singer, kscFilePath,
						type);
			} else {
				downloadKscFile(sid, kscUrl, kscFilePath, type);
			}
		} else {

			SongMessage songMessage = new SongMessage();

			if (type == SongMessage.KSCTYPELRC) {

				songMessage.setType(SongMessage.LRCKSCLOADED);
			} else if (type == SongMessage.KSCTYPEDES) {

				songMessage.setType(SongMessage.DESKSCLOADED);
			} else if (type == SongMessage.KSCTYPELOCK) {

				songMessage.setType(SongMessage.LOCKKSCLOADED);
			}

			songMessage.setKscFilePath(kscFilePath);
			songMessage.setSid(sid);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);
		}
	}

	/**
	 * 从服务器获取歌词文件信息
	 * 
	 * @param context
	 * @param sid
	 * @param title
	 * @param singer
	 * @param kscFilePath
	 */
	private static void getKscInfoFormNet(final Context context,
			final String sid, final String title, final String singer,
			final String kscFilePath, final int type) {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String kscUrl = null;

				HttpResult<KscInfo> httpResult = HttpUtil.getKscInfoByOther(
						context, title, singer);
				int status = httpResult.getStatus();
				if (status == HttpUtil.SUCCESS) {
					List<KscInfo> lists = httpResult.getModels();
					KscInfo kscInfo = lists.get(0);
					kscUrl = HttpUtil.getKscInfoDataByID(kscInfo.getKid());
					updateDB(context, sid, kscUrl);
				}
				return kscUrl;
			}

			@Override
			protected void onPostExecute(String kscUrl) {
				if (kscUrl != null && !kscUrl.equals("")) {
					downloadKscFile(sid, kscUrl, kscFilePath, type);
				}
			}

		}.execute("");
	}

	/**
	 * 更新数据库
	 * 
	 * @param context
	 * @param sid
	 * @param kscUrl
	 */
	protected static void updateDB(Context context, String sid, String kscUrl) {
		SongDB.getSongInfoDB(context).updateSongKscUrl(sid, kscUrl);
	}

	/**
	 * 下载ksc歌词文件
	 * 
	 * @param kscUrl
	 * @param kscFilePath
	 */
	private static void downloadKscFile(final String sid, final String kscUrl,
			final String kscFilePath, final int type) {

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {

				downloadFile(sid, kscUrl, kscFilePath, type);

				return null;
			}

			@Override
			protected void onPostExecute(String kscUrl) {
			}
		}.execute("");
	}

	/**
	 * 下载文件
	 * 
	 * @param kscUrl
	 * @param kscFilePath
	 */
	protected static void downloadFile(final String sid, String kscUrl,
			final String kscFilePath, final int type) {
		// OutputStream output = null;
		// try {
		// URL url = new URL(kscUrl);
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//
		// File file = new File(kscFilePath);
		// InputStream input = conn.getInputStream();
		// if (file.exists()) {
		// return;
		// } else {
		// if (!file.getParentFile().exists()) {
		// file.getParentFile().mkdirs();
		// }
		// file.createNewFile();// 新建文件
		// output = new FileOutputStream(file);
		// // 读取大文件
		// byte[] buffer = new byte[4 * 1024];
		// while (input.read(buffer) != -1) {
		// output.write(buffer);
		// }
		// output.flush();
		// }
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// output.close();
		//
		// // 下载成功，则通知页面加载歌词文件
		//
		// SongMessage songMessage = new SongMessage();
		// songMessage.setType(SongMessage.KSCDOWNLOADED);
		// songMessage.setKscFilePath(kscFilePath);
		// songMessage.setSid(sid);
		// // 通知
		// ObserverManage.getObserver().setMessage(songMessage);
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		File file = new File(kscFilePath);
		// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if (file.exists()) {
			file.delete();
		}
		try {
			// 构造URL
			URL url = new URL(kscUrl);
			// 打开连接
			URLConnection con = url.openConnection();
			// 获得文件的长度
			// int contentLength = con.getContentLength();
			// System.out.println("长度 :" + contentLength);
			// 输入流
			InputStream is = con.getInputStream();

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int blen;
			while ((blen = is.read(buffer)) > -1) {
				baos.write(buffer, 0, blen);
			}
			baos.flush();
			is.close();

			// 解析歌词
			new Thread() {

				@Override
				public void run() {
					try {
						KscLyricsManage.parserKscLyricsByKscInputStream(sid,
								new ByteArrayInputStream(baos.toByteArray()),
								type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();
			// 保存歌词文件

			new Thread() {

				@Override
				public void run() {
					try {
						// 1K的数据缓冲
						byte[] bs = new byte[1024];
						// 读取到的数据长度
						int len;
						// 输出的文件流
						OutputStream os = new FileOutputStream(kscFilePath);
						InputStream fileStream = new ByteArrayInputStream(
								baos.toByteArray());
						// 开始读取
						while ((len = fileStream.read(bs)) != -1) {
							os.write(bs, 0, len);
						}
						// 完毕，关闭所有链接
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// finally {
		// try {
		// // 下载成功，则通知页面加载歌词文件
		// SongMessage songMessage = new SongMessage();
		// songMessage.setType(SongMessage.KSCDOWNLOADED);
		// // songMessage.setKscFilePath(kscFilePath);
		// songMessage.setSid(sid);
		// // 通知
		// ObserverManage.getObserver().setMessage(songMessage);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}
}
