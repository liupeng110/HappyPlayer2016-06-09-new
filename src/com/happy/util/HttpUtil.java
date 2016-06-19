package com.happy.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.happy.common.Constants;
import com.happy.model.app.DownloadTask;
import com.happy.model.app.HttpResult;
import com.happy.model.app.SongInfo;
import com.happy.model.pc.AppInfo;
import com.happy.model.pc.KscInfo;
import com.happy.model.pc.SingerAvatar;
import com.happy.model.pc.SingerPhoto;
import com.happy.model.pc.Splash;

public class HttpUtil {

	private static DefaultHttpClient httpclient = new DefaultHttpClient();

	/**
	 * 无网络
	 */
	public static final int NONET = 1000;
	/**
	 * 无wifi
	 */
	public static final int NOWIFI = 1001;
	/**
	 * 无结果
	 */
	public static final int NORESULT = 1002;
	/**
	 * 发请求时出错
	 */
	public static final int HTTPERROR = 1003;
	/**
	 * 获取结果失败
	 */
	public static final int RESULTERROR = 1004;
	/**
	 * 获取结果成功
	 */
	public static final int SUCCESS = 1005;
	/**
	 * 封装数据失败
	 */
	public static final int SETRESULTERROR = 1006;

	/**
	 * 基本url
	 */
	// public static final String baseurl =
	// "http://192.168.0.139:8080/HappyPlayer/";

	public static final String baseurl = "http://192.168.1.164:8080/HappyPlayer/";

	// public static final String baseurl =
	// "http://120.24.178.208:8080/HappyPlayer/";
	// public static final String baseurl =
	// "http://222.201.139.162:8080/HappyPlayer/";

	/**
	 * 获取启动页数据
	 * 
	 * @return
	 */
	public static HttpResult<Splash> getSplashMessageByDate(Context context) {
		String url = baseurl + "phone/getSplashMessageByDate";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpResult<Splash> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, false);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int result = json.getInt("result");
				if (result == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				Splash splash = new Splash();

				splash.setSid(json.getString("sid"));
				splash.setTitle(json.getString("title"));
				splash.setCreateTime(json.getString("createTime"));
				splash.setStartTime(json.getString("startTime"));
				splash.setEndTime(json.getString("endTime"));
				splash.setUpdateTime(json.getString("updateTime"));

				httpResult.setModel(splash);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}

		}
		return httpResult;
	}

	/**
	 * 加载启动图片路径
	 * 
	 * @param id
	 * @return
	 */
	public static String getSplashImageByID(String id) {
		String url = baseurl + "phone/getSplashImageByID?id=" + id;
		return url;
	}

	/**
	 * 通过添加时间，获取最新的启动页面数据
	 */
	public static HttpResult<Splash> loadNewSplashByCreateTime(Context context,
			String createTime) {
		String url = baseurl + "phone/loadNewSplashByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<Splash> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<Splash> lists = new ArrayList<Splash>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {
					Splash splash = new Splash();
					JSONObject jsonTemp = jSONArray.getJSONObject(i);

					splash.setSid(jsonTemp.getString("sid"));
					splash.setTitle(jsonTemp.getString("title"));
					splash.setCreateTime(jsonTemp.getString("createTime"));
					splash.setStartTime(jsonTemp.getString("startTime"));
					splash.setEndTime(jsonTemp.getString("endTime"));
					splash.setUpdateTime(jsonTemp.getString("updateTime"));

					lists.add(splash);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过添加时间，加载更多的启动页面数据
	 */
	public static HttpResult<Splash> loadMoreSplashByCreateTime(
			Context context, String createTime) {
		String url = baseurl + "phone/loadMoreSplashByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<Splash> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<Splash> lists = new ArrayList<Splash>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {
					Splash splash = new Splash();
					JSONObject jsonTemp = jSONArray.getJSONObject(i);

					splash.setSid(jsonTemp.getString("sid"));
					splash.setTitle(jsonTemp.getString("title"));
					splash.setCreateTime(jsonTemp.getString("createTime"));
					splash.setStartTime(jsonTemp.getString("startTime"));
					splash.setEndTime(jsonTemp.getString("endTime"));
					splash.setUpdateTime(jsonTemp.getString("updateTime"));

					lists.add(splash);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 获取是否更新app版本信息
	 * 
	 * @param context
	 * @param versionName
	 * @param versionCode
	 * @return
	 */
	public static HttpResult<AppInfo> getAppInfoMessage(Context context,
			String versionName, int versionCode) {
		String url = baseurl + "phone/getAppInfoMessage";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("versionName", versionName));
		params.add(new BasicNameValuePair("versionCode", versionCode + ""));
		HttpResult<AppInfo> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int result = json.getInt("result");
				if (result == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				AppInfo appInfo = new AppInfo();

				appInfo.setAid(json.getString("aid"));
				appInfo.setName(json.getString("name"));
				appInfo.setTitle(json.getString("title"));
				appInfo.setVersionName(json.getString("versionName"));
				appInfo.setVersionCode(json.getString("versionCode"));
				appInfo.setSize(json.getLong("size"));
				appInfo.setSizeStr(json.getString("sizeStr"));
				appInfo.setCreateTime(json.getString("createTime"));
				appInfo.setUpdateTime(json.getString("updateTime"));
				appInfo.setType(json.getString("type"));

				httpResult.setModel(appInfo);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}

		}
		return httpResult;
	}

	/**
	 * 获取下载app文件的路径
	 * 
	 * @param aid
	 * @return
	 */
	public static String getAppInfoDataByID(String aid) {
		String url = baseurl + "phone/getAppInfoDataByID?aid=" + aid;
		return url;
	}

	/**
	 * 获取皮肤主题文件下载路径
	 * 
	 * @param id
	 * @return
	 */
	public static String getSkinThemeDataByID(String sid) {
		String url = baseurl + "phone/getSkinThemeDataByID?sid=" + sid;
		return url;
	}

	/**
	 * 获取皮肤主题预览图片下载路径
	 * 
	 * @param id
	 * @return
	 */
	public static String getSkinThemePreviewImageByID(String sid) {
		String url = baseurl + "phone/getSkinThemePreviewImageByID?sid=" + sid;
		return url;
	}

	/**
	 * 下载歌曲路径
	 * 
	 * @param sid
	 * @return
	 */
	public static String getSongInfoDataByID(String sid) {
		String url = baseurl + "phone/getSongInfoDataByID?sid=" + sid;
		return url;
	}

	/**
	 * 通过添加时间，获取最新的启动页面数据
	 */
	public static HttpResult<DownloadTask> loadNewSkinThemeByCreateTime(
			Context context, String createTime) {
		String url = baseurl + "phone/loadNewSkinThemeByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<DownloadTask> httpResult = getHttpResult(context, url,
				params, 1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<DownloadTask> lists = new ArrayList<DownloadTask>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {
					DownloadTask downloadTask = new DownloadTask();
					JSONObject jsonTemp = jSONArray.getJSONObject(i);

					String sid = jsonTemp.getString("sid");
					downloadTask.setTid(sid);
					downloadTask.settName(jsonTemp.getString("themeName"));
					downloadTask.setFileSize(jsonTemp.getLong("size"));
					downloadTask.setFileSizeStr(jsonTemp.getString("sizeStr"));
					downloadTask.setStatus(DownloadTask.INT);
					String downloadUrl = getSkinThemeDataByID(sid);
					downloadTask.setDownloadUrl(downloadUrl);

					String filePath = Constants.PATH_SKIN + File.separator
							+ sid + ".zip";
					downloadTask.setFilePath(filePath);

					downloadTask.setDownloadedSize(0);
					downloadTask.setType(DownloadTask.SKIN);

					downloadTask.setAddTime(jsonTemp.getString("createTime"));

					lists.add(downloadTask);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过添加时间，加载更多的启动页面数据
	 */
	public static HttpResult<DownloadTask> loadMoreSkinThemeByCreateTime(
			Context context, String createTime) {
		String url = baseurl + "phone/loadMoreSkinThemeByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<DownloadTask> httpResult = getHttpResult(context, url,
				params, 1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<DownloadTask> lists = new ArrayList<DownloadTask>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {
					DownloadTask downloadTask = new DownloadTask();
					JSONObject jsonTemp = jSONArray.getJSONObject(i);

					String sid = jsonTemp.getString("sid");
					downloadTask.setTid(sid);
					downloadTask.settName(jsonTemp.getString("themeName"));
					downloadTask.setFileSize(jsonTemp.getLong("size"));
					downloadTask.setFileSizeStr(jsonTemp.getString("sizeStr"));
					downloadTask.setStatus(DownloadTask.INT);
					String downloadUrl = getSkinThemeDataByID(sid);
					downloadTask.setDownloadUrl(downloadUrl);
					String filePath = Constants.PATH_SKIN + File.separator
							+ sid + ".zip";
					downloadTask.setFilePath(filePath);
					downloadTask.setDownloadedSize(0);
					downloadTask.setType(DownloadTask.SKIN);
					downloadTask.setAddTime(jsonTemp.getString("createTime"));
					lists.add(downloadTask);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过添加时间，获取最新的歌曲列表数据
	 */
	public static HttpResult<SongInfo> loadNewSongInfoByCreateTime(
			Context context, String createTime) {
		String url = baseurl + "phone/loadNewSongInfoByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<SongInfo> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<SongInfo> lists = new ArrayList<SongInfo>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					SongInfo songInfo = new SongInfo();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);
					songInfo.setSid(jsonTemp.getString("sid"));
					String title = jsonTemp.getString("title");
					songInfo.setTitle(title);
					String singer = jsonTemp.getString("singer");
					songInfo.setSinger(singer);
					String displayName = singer + "-" + title;
					songInfo.setDisplayName(displayName);
					songInfo.setDuration(jsonTemp.getLong("duration"));
					songInfo.setDurationStr(jsonTemp.getString("durationStr"));
					songInfo.setSize(jsonTemp.getLong("size"));
					songInfo.setSizeStr(jsonTemp.getString("sizeStr"));
					songInfo.setCreateTime(jsonTemp.getString("createTime"));
					songInfo.setType(SongInfo.NETSONG);
					songInfo.setIslike(SongInfo.UNLIKE);
					songInfo.setDownloadStatus(SongInfo.DOWNLOADING);
					String tempfilePath = Constants.PATH_CACHE_MP3 + File.separator
							+ songInfo.getSid() + ".temp";
					songInfo.setFilePath(tempfilePath);

					lists.add(songInfo);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过添加时间，加载更多的歌曲数据
	 */
	public static HttpResult<SongInfo> loadMoreSongInfoByCreateTime(
			Context context, String createTime) {
		String url = baseurl + "phone/loadMoreSongInfoByCreateTime";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("createTime", createTime));
		HttpResult<SongInfo> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<SongInfo> lists = new ArrayList<SongInfo>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					SongInfo songInfo = new SongInfo();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);
					songInfo.setSid(jsonTemp.getString("sid"));
					String title = jsonTemp.getString("title");
					songInfo.setTitle(title);
					String singer = jsonTemp.getString("singer");
					songInfo.setSinger(singer);
					String displayName = singer + "-" + title;
					songInfo.setDisplayName(displayName);
					songInfo.setDuration(jsonTemp.getLong("duration"));
					songInfo.setDurationStr(jsonTemp.getString("durationStr"));
					songInfo.setSize(jsonTemp.getLong("size"));
					songInfo.setSizeStr(jsonTemp.getString("sizeStr"));
					songInfo.setCreateTime(jsonTemp.getString("createTime"));
					songInfo.setType(SongInfo.NETSONG);
					songInfo.setIslike(SongInfo.UNLIKE);
					songInfo.setDownloadStatus(SongInfo.DOWNLOADING);
					String tempfilePath = Constants.PATH_CACHE_MP3 + File.separator
							+ songInfo.getSid() + ".temp";
					songInfo.setFilePath(tempfilePath);

					lists.add(songInfo);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过key获取歌曲信息
	 */
	public static HttpResult<SongInfo> getSongInfoByKey(Context context,
			String key) {
		String url = baseurl + "phone/getSongInfoByKey";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		HttpResult<SongInfo> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, true);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<SongInfo> lists = new ArrayList<SongInfo>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					SongInfo songInfo = new SongInfo();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);
					songInfo.setSid(jsonTemp.getString("sid"));
					String title = jsonTemp.getString("title");
					songInfo.setTitle(title);
					String singer = jsonTemp.getString("singer");
					songInfo.setSinger(singer);
					String displayName = singer + "-" + title;
					songInfo.setDisplayName(displayName);
					songInfo.setDuration(jsonTemp.getLong("duration"));
					songInfo.setDurationStr(jsonTemp.getString("durationStr"));
					songInfo.setSize(jsonTemp.getLong("size"));
					songInfo.setSizeStr(jsonTemp.getString("sizeStr"));
					songInfo.setCreateTime(jsonTemp.getString("createTime"));
					songInfo.setType(SongInfo.NETSEARCHSONG);
					songInfo.setIslike(SongInfo.UNLIKE);
					songInfo.setDownloadStatus(SongInfo.DOWNLOADING);

					lists.add(songInfo);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 通过歌手名称获取歌手头像图片数据
	 */
	public static HttpResult<SingerAvatar> getSingerAvatarBySinger(
			Context context, String singer) {
		String url = baseurl + "phone/getSingerAvatarBySinger";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("singer", singer));
		HttpResult<SingerAvatar> httpResult = getHttpResult(context, url,
				params, 1 * 1000, 2 * 1000, false);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<SingerAvatar> lists = new ArrayList<SingerAvatar>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					SingerAvatar singerAvatar = new SingerAvatar();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);
					singerAvatar.setSid(jsonTemp.getString("sid"));
					String mSinger = jsonTemp.getString("singer");
					singerAvatar.setSinger(mSinger);
					String createTime = jsonTemp.getString("createTime");
					singerAvatar.setCreateTime(createTime);
					singerAvatar
							.setUpdateTime(jsonTemp.getString("updateTime"));

					lists.add(singerAvatar);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 获取歌手头像图片
	 */
	public static String getSingerAvatarImageByID(String sid) {
		String url = baseurl + "phone/getSingerAvatarImageByID?sid=" + sid;
		return url;
	}

	/**
	 * 通过歌手名称获取歌手写真图片数据
	 */
	public static HttpResult<SingerPhoto> getSingerPhotoBySinger(
			Context context, String singer) {
		String url = baseurl + "phone/getSingerPhotoBySinger";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("singer", singer));
		HttpResult<SingerPhoto> httpResult = getHttpResult(context, url,
				params, 1 * 1000, 2 * 1000, false);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<SingerPhoto> lists = new ArrayList<SingerPhoto>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					SingerPhoto singerPhoto = new SingerPhoto();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);
					singerPhoto.setSid(jsonTemp.getString("sid"));
					String mSinger = jsonTemp.getString("singer");
					singerPhoto.setSinger(mSinger);
					String createTime = jsonTemp.getString("createTime");
					singerPhoto.setCreateTime(createTime);
					singerPhoto.setUpdateTime(jsonTemp.getString("updateTime"));

					lists.add(singerPhoto);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 获取歌手写真图片 imageid 1/2/3
	 */
	public static String getSingerPhotoImageByID(String sid, String imageid) {
		String url = baseurl + "phone/getSingerPhotoImageByID?sid=" + sid
				+ "&imageid=" + imageid;
		return url;
	}

	/**
	 * 查询歌词
	 */
	public static HttpResult<KscInfo> getKscInfoByOther(Context context,
			String title, String singer) {
		String url = baseurl + "phone/getKscInfoByOther";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("songName", title));
		params.add(new BasicNameValuePair("artist", singer));
		HttpResult<KscInfo> httpResult = getHttpResult(context, url, params,
				1 * 1000, 2 * 1000, false);
		int status = httpResult.getStatus();
		if (status == SUCCESS) {
			List<KscInfo> lists = new ArrayList<KscInfo>();
			String temp = httpResult.getResultStr();
			JSONObject json;
			try {
				json = new JSONObject(temp);
				int size = json.getInt("size");
				if (size == 0) {
					httpResult.setStatus(NORESULT);
					return httpResult;
				}

				JSONArray jSONArray = json.getJSONArray("rows");

				for (int i = 0; i < jSONArray.length(); i++) {

					KscInfo kscInfo = new KscInfo();

					JSONObject jsonTemp = jSONArray.getJSONObject(i);

					kscInfo.setKid(jsonTemp.getString("kid"));
					kscInfo.setArtist(jsonTemp.getString("artist"));
					kscInfo.setSongName(jsonTemp.getString("songName"));
					kscInfo.setSize(jsonTemp.getLong("size"));
					kscInfo.setSizeStr(jsonTemp.getString("sizeStr"));
					kscInfo.setCreateTime(jsonTemp.getString("createTime"));
					kscInfo.setUpdateTime(jsonTemp.getString("updateTime"));
					kscInfo.setType(jsonTemp.getString("type"));

					lists.add(kscInfo);
				}

				httpResult.setModels(lists);

			} catch (Exception e) {
				ToastUtil.showText("封装数据失败,请稍候再试!");
				httpResult.setStatus(SETRESULTERROR);
				e.printStackTrace();
			}
		}
		return httpResult;
	}

	/**
	 * 歌词文件下载路径
	 * 
	 * @param kid
	 * @return
	 */
	public static String getKscInfoDataByID(String kid) {
		String url = baseurl + "phone/getKscInfoDataByID?kid=" + kid;
		return url;
	}

	/**
	 * 获取http请求后发送过来的数据
	 * 
	 * @param <T>
	 * 
	 * @param context
	 * @param REQUEST_TIMEOUT
	 *            设置请求超时时间
	 * @param SO_TIMEOUT
	 *            设置等待数据超时时间
	 * @param url
	 *            请求地址
	 * @param params
	 *            传参数
	 * @param isShowTip
	 *            显示提示信息
	 * @return返回从地址发送过来的数据
	 */
	private static <T> HttpResult<T> getHttpResult(Context context, String url,
			List<NameValuePair> params, int REQUEST_TIMEOUT, int SO_TIMEOUT,
			boolean isShowTip) {
		HttpResult<T> httpResult = getResponseText(context, url, params,
				REQUEST_TIMEOUT, SO_TIMEOUT);
		int status = httpResult.getStatus();
		String msg = "";
		switch (status) {
		case NONET:
			msg = "无网络状态";
			break;
		case NOWIFI:
			msg = "非wifi状态";
			break;
		case HTTPERROR:
			msg = "服务器异常";
			break;
		case RESULTERROR:
			msg = "请求异常";
			break;
		default:
			break;
		}
		if (isShowTip && !msg.equals(""))
			ToastUtil.showText(msg);
		return httpResult;

	}

	/**
	 * 获取http请求后发送过来的数据
	 * 
	 * @param <T>
	 * 
	 * @param context
	 * @param REQUEST_TIMEOUT
	 *            设置请求超时时间
	 * @param SO_TIMEOUT
	 *            设置等待数据超时时间
	 * @param url
	 *            请求地址
	 * @param params
	 *            传参数
	 * @return返回从地址发送过来的数据
	 */
	private static <T> HttpResult<T> getResponseText(Context context,
			String url, List<NameValuePair> params, int REQUEST_TIMEOUT,
			int SO_TIMEOUT) {
		HttpResult<T> httpResult = new HttpResult<T>();
		if (!NetUtil.isNetworkAvailable(context)) {
			httpResult.setStatus(NONET);
			return httpResult;
		}
		// if (Constants.isWifi) {
		// if (!NetUtil.isWifi(context)) {
		// httpResult.setStatus(NOWIFI);
		// return httpResult;
		// }
		// }
		HttpPost httpost = null;
		try {
			httpost = new HttpPost(url);
			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			HttpResponse response = httpclient.execute(httpost);
			int flag = response.getStatusLine().getStatusCode();
			if (flag == 200) {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity, HTTP.UTF_8);
				if (result == null || result.equals("")) {
					httpResult.setStatus(NORESULT);
				} else {
					httpResult.setStatus(SUCCESS);
					httpResult.setResultStr(result);
				}
			} else {
				httpResult.setStatus(RESULTERROR);
			}
		} catch (IOException e) {
			httpResult.setStatus(HTTPERROR);
			e.printStackTrace();
		} finally {
			if (httpost != null) {
				httpost.abort();
			}
		}
		return httpResult;
	}
}
