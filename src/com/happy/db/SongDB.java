package com.happy.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.happy.model.app.SongInfo;
import com.happy.model.app.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.DateUtil;
import com.happy.util.PingYinUtil;

public class SongDB {
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "songTbl";

	/**
	 * 建表语句
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + "("
			+ "sid text," + "displayName text," + "title text,"
			+ "singer text," + "duration long," + "durationStr text,"
			+ "size long," + "sizeStr text," + "filePath text," + "type int,"
			+ "islike int," + "category text," + "childCategory text,"
			+ "createTime text," + "albumUrl text," + "singerPIC text,"
			+ "kscUrl text,"
			+ "fileExt text,"
			// + "singerPIC1 text,"
			// + "singerPIC2 text," + "singerPIC3 text,"
			+ "downloadUrl text," + "downloadProgress long,"
			+ "downloadStatus int" + ")";

	private static SongDB _SongInfoDB;

	private SQLiteDatabase db;

	private SQLDBHlper mDBHlper;

	public SongDB(Context context) {
		mDBHlper = SQLDBHlper.getSQLDBHlper(context);
	}

	public static SongDB getSongInfoDB(Context context) {
		if (_SongInfoDB == null) {
			_SongInfoDB = new SongDB(context);
		}
		return _SongInfoDB;
	}

	/**
	 * 添加歌曲到本地播放列表
	 * 
	 * @param songInfo
	 */
	public void add(SongInfo songInfo) {
		ContentValues values = new ContentValues();

		values.put("sid", songInfo.getSid());
		values.put("displayName", songInfo.getDisplayName());
		values.put("title", songInfo.getTitle());
		values.put("singer", songInfo.getSinger());
		values.put("duration", songInfo.getDuration());
		values.put("durationStr", songInfo.getDurationStr());
		values.put("size", songInfo.getSize());
		values.put("sizeStr", songInfo.getSizeStr());
		values.put("filePath", songInfo.getFilePath());
		values.put("fileExt", songInfo.getFileExt());
		values.put("type", songInfo.getType());
		values.put("islike", songInfo.getIslike());
		values.put("createTime", songInfo.getCreateTime());
		values.put("albumUrl", songInfo.getAlbumUrl());
		values.put("singerPIC", songInfo.getSingerPIC());
		values.put("kscUrl", songInfo.getKscUrl());
		// values.put("singerPIC1", songInfo.getSingerPIC1());
		// values.put("singerPIC2", songInfo.getSingerPIC2());
		// values.put("singerPIC3", songInfo.getSingerPIC3());
		values.put("downloadUrl", songInfo.getDownloadUrl());
		values.put("downloadProgress", songInfo.getDownloadProgress());
		values.put("downloadStatus", songInfo.getDownloadStatus());

		String category = PingYinUtil.getPingYin(songInfo.getDisplayName())
				.toUpperCase();
		char cat = category.charAt(0);
		if (cat <= 'Z' && cat >= 'A') {
			songInfo.setCategory(cat + "");
			songInfo.setChildCategory(category);
		} else {
			songInfo.setCategory("^");
			songInfo.setChildCategory(category);
		}

		values.put("category", songInfo.getCategory());
		values.put("childCategory", songInfo.getChildCategory());

		insert(values, songInfo);
	}

	/**
	 * 插入ContentValues
	 */
	private void insert(ContentValues values, SongInfo songInfo) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.insert(TBL_NAME, null, values);

			if (songInfo.getDownloadStatus() == SongInfo.DOWNLOADED) {
				SongMessage songMessage = new SongMessage();
				songMessage.setSongInfo(songInfo);
				songMessage.setType(SongMessage.ADDMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取相关的Cursor
	 */
	public Cursor query() {
		db = mDBHlper.getReadableDatabase();
		Cursor c = db.query(TBL_NAME, null, null, null, null, null,
				"category asc , childCategory asc");
		return c;
	}

	/**
	 * 获取歌曲列表
	 * 
	 * @return
	 */
	// public List<SongInfo> getAllSong() {
	// List<SongInfo> list = new ArrayList<SongInfo>();
	// Cursor cursor = query();
	// while (cursor.moveToNext()) {
	// SongInfo songInfo = getSongInfo(cursor);
	// File file = new File(songInfo.getFilePath());
	// if (!file.exists()) {
	// delete(songInfo.getSid());
	// } else {
	// list.add(songInfo);
	// }
	// }
	// cursor.close();
	// return list;
	// }

	/**
	 * 获取所有的分类
	 * 
	 * @return
	 */
	public List<String> getAllLikeCategory() {
		List<String> list = new ArrayList<String>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(true, TBL_NAME, new String[] { "category" },
				"islike= ?", new String[] { SongInfo.LIKE + "" }, null, null,
				"category asc , childCategory asc", null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex("category")));
		}
		cursor.close();
		String baseCategory = "^";
		if (!list.contains(baseCategory)) {
			list.add(baseCategory);
		}
		return list;
	}

	/**
	 * 获取所有的分类
	 * 
	 * @return
	 */
	public List<String> getAllLocalSongCategory() {
		List<String> list = new ArrayList<String>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(true, TBL_NAME, new String[] { "category" },
				"downloadStatus= ?", new String[] { SongInfo.DOWNLOADED + "" },
				null, null, "category asc , childCategory asc", null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex("category")));
		}
		cursor.close();
		String baseCategory = "^";
		if (!list.contains(baseCategory)) {
			list.add(baseCategory);
		}
		return list;
	}

	/**
	 * 获取所有分类的歌曲列表
	 * 
	 * @param category
	 * @return
	 */
	public List<SongInfo> getAllLocalCategorySong(String category) {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null,
				"category= ? and downloadStatus= ?", new String[] { category,
						SongInfo.DOWNLOADED + "" }, null, null,
				"childCategory asc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			File file = new File(songInfo.getFilePath());
			if (!file.exists()) {
				delete(songInfo.getSid());
			} else {
				list.add(songInfo);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * 
	 * @param category
	 * @return
	 */
	public List<SongInfo> getAllLikeCategorySong(String category) {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null, "category= ? and islike= ?",
				new String[] { category, SongInfo.LIKE + "" }, null, null,
				"childCategory asc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			File file = new File(songInfo.getFilePath());
			if (!file.exists()) {
				delete(songInfo.getSid());
			} else {
				list.add(songInfo);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取所有喜欢的歌曲列表
	 * 
	 * @return
	 */
	public List<SongInfo> getAllLikeSong() {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null, "islike= ?",
				new String[] { SongInfo.LIKE + "" }, null, null,
				"childCategory asc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			File file = new File(songInfo.getFilePath());
			if (!file.exists()) {
				delete(songInfo.getSid());
			} else {
				list.add(songInfo);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取正在下载的歌曲
	 * 
	 * @return
	 */
	public List<SongInfo> getDownloadSong(int status) {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null,
				"type= ? and downloadStatus= ?", new String[] {
						SongInfo.DOWNLOADSONG + "", status + "" }, null, null,
				"createTime desc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			list.add(songInfo);
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取所有的本地歌曲列表
	 * 
	 * @return
	 */
	public List<SongInfo> getAllLocalSong() {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null, "downloadStatus= ?",
				new String[] { SongInfo.DOWNLOADED + "" }, null, null,
				"category asc , childCategory asc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			File file = new File(songInfo.getFilePath());
			if (!file.exists()) {
				delete(songInfo.getSid());
			} else {
				list.add(songInfo);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取所有的推荐歌曲
	 * 
	 * @param isInit
	 *            是否是初始化，app启动时，先把没下载完成的歌曲删除
	 * 
	 * @return
	 */
	public List<SongInfo> getAllRecommendSong(boolean isInit) {
		List<SongInfo> list = new ArrayList<SongInfo>();
		db = mDBHlper.getReadableDatabase();
		Cursor cursor = db.query(TBL_NAME, null, "type= ?",
				new String[] { SongInfo.NETSONG + "" }, null, null,
				"createTime desc", null);
		while (cursor.moveToNext()) {
			SongInfo songInfo = getSongInfo(cursor);
			if (songInfo.getDownloadProgress() < songInfo.getSize()) {
				songInfo.setDownloadProgress(0);
				if (isInit) {
					File songFile = new File(songInfo.getFilePath());
					if (songFile.exists()) {
						songFile.deleteOnExit();
					}
				}
				updateSongDownloadProgress(songInfo.getSid(), 0);
			}
			// File file = new File(songInfo.getFilePath());
			// if (!file.exists()) {
			// delete(songInfo.getSid());
			// } else {
			list.add(songInfo);
			// }
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取本地歌曲总数
	 * 
	 * @return
	 */
	public int getCount() {
		db = mDBHlper.getReadableDatabase();
		String args[] = { SongInfo.DOWNLOADED + "" };
		Cursor cursor = db.rawQuery("select count(*)from " + TBL_NAME
				+ " WHERE downloadStatus=?", args);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/**
	 * 通过Cursor来提取相关的SongInfo数据
	 * 
	 * @param c
	 * @return
	 */
	private SongInfo getSongInfo(Cursor cursor) {
		SongInfo song = new SongInfo();

		song.setSid(cursor.getString(cursor.getColumnIndex("sid")));
		song.setDisplayName(cursor.getString(cursor
				.getColumnIndex("displayName")));
		song.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		song.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
		song.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
		song.setDurationStr(cursor.getString(cursor
				.getColumnIndex("durationStr")));
		song.setFileExt(cursor.getString(cursor.getColumnIndex("fileExt")));
		song.setSize(cursor.getLong(cursor.getColumnIndex("size")));
		song.setSizeStr(cursor.getString(cursor.getColumnIndex("sizeStr")));
		song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
		song.setType(cursor.getInt(cursor.getColumnIndex("type")));
		song.setIslike(cursor.getInt(cursor.getColumnIndex("islike")));
		song.setCategory(cursor.getString(cursor.getColumnIndex("category")));
		song.setChildCategory(cursor.getString(cursor
				.getColumnIndex("childCategory")));
		song.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
		song.setAlbumUrl(cursor.getString(cursor.getColumnIndex("albumUrl")));
		song.setSingerPIC(cursor.getString(cursor.getColumnIndex("singerPIC")));
		song.setKscUrl(cursor.getString(cursor.getColumnIndex("kscUrl")));
		// song.setSingerPIC1(cursor.getString(cursor.getColumnIndex("singerPIC1")));
		// song.setSingerPIC2(cursor.getString(cursor.getColumnIndex("singerPIC2")));
		// song.setSingerPIC3(cursor.getString(cursor.getColumnIndex("singerPIC3")));
		song.setDownloadUrl(cursor.getString(cursor
				.getColumnIndex("downloadUrl")));
		song.setDownloadProgress(cursor.getLong(cursor
				.getColumnIndex("downloadProgress")));
		song.setDownloadStatus(cursor.getInt(cursor
				.getColumnIndex("downloadStatus")));

		return song;
	}

	/**
	 * 通过sid来获取歌曲的相关信息
	 * 
	 * @param sid
	 * @return
	 */
	public SongInfo getSongInfo(String sid) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.rawQuery("select * from " + TBL_NAME
				+ " where sid=?", new String[] { sid + "" });
		if (!cursor.moveToNext()) {
			return null;
		}
		SongInfo song = getSongInfo(cursor);
		cursor.close();
		return song;
	}

	/**
	 * 通过sid来获取歌曲的相关信息
	 * 
	 * @param sid
	 * @return
	 */
	public SongInfo getSongInfo(String sid, int type) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.rawQuery("select * from " + TBL_NAME
				+ " where sid=? and type=? ", new String[] { sid + "",
				type + "" });
		if (!cursor.moveToNext()) {
			return null;
		}
		SongInfo song = getSongInfo(cursor);
		cursor.close();
		return song;
	}

	/**
	 * 删除sid的相关数据
	 */
	public void delete(String sid) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.delete(TBL_NAME, "sid=?", new String[] { sid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有的数据
	 */
	public void delete() {
		db = mDBHlper.getWritableDatabase();
		try {
			db.execSQL("drop table if exists " + TBL_NAME);
			db.execSQL(CREATE_TBL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过文件路径来判断其是否在数据库中
	 * 
	 * @param path
	 * @return
	 */
	public boolean songIsExists(String fileName) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.query(TBL_NAME, new String[] { "displayName" },
				" displayName=?", new String[] { fileName }, null, null, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * 根据类型和歌曲id判断歌曲是否存在
	 * 
	 * @param sid
	 * @param type
	 * @return
	 */
	public boolean songIsExistsByTypeAndSid(String sid, int type) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.query(TBL_NAME, new String[] { "sid" },
				" sid=? and type=?", new String[] { sid, type + "" }, null,
				null, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * 通过sid来判断歌曲是否已经存在
	 * 
	 * @param sid
	 * @return
	 */
	public boolean songIsExistsBySID(String sid) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.query(TBL_NAME, new String[] { "sid" }, " sid=?",
				new String[] { sid }, null, null, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * 更新歌曲的下载进度
	 * 
	 * @param sID
	 * @param downloadProgress
	 */
	public void updateSongDownloadProgress(String sid, long downloadProgress,
			int type) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("downloadProgress", downloadProgress);
		try {
			db.update(TBL_NAME, values, "sid=? and type=?", new String[] {
					sid + "", type + "" });

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

	/**
	 * 更新歌曲的下载进度
	 * 
	 * @param sID
	 * @param downloadProgress
	 */
	public void updateSongDownloadProgress(String sid, long downloadProgress) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("downloadProgress", downloadProgress);
		try {
			db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });

			SongMessage songMessage = new SongMessage();
			SongInfo songInfo = getSongInfo(sid, SongInfo.NETSONG);
			songInfo.setDownloadProgress(downloadProgress);
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.UPDATEMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

	/**
	 * 更新完成的歌曲下载进度和状态
	 * 
	 * @param songInfo
	 */
	public void updateSongDownloaded(SongInfo songInfo) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("downloadProgress", songInfo.getSize());
		values.put("downloadStatus", SongInfo.DOWNLOADED);
		try {
			db.update(TBL_NAME, values, "sid=?",
					new String[] { songInfo.getSid() + "" });

			SongMessage songMessage = new SongMessage();
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.ADDMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}

	}

	/**
	 * 更新网络歌曲下载进度
	 * 
	 * @param songInfo
	 */
	public void updateNetSongDownloaded(SongInfo mSongInfo) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("downloadProgress", mSongInfo.getSize());
		values.put("downloadStatus", SongInfo.DOWNLOADED);
		values.put("createTime", DateUtil.dateToString(new Date()));
		try {
			db.update(TBL_NAME, values, "sid=? and type=?", new String[] {
					mSongInfo.getSid() + "", mSongInfo.getType() + "" });

			SongInfo songInfo = getSongInfo(mSongInfo.getSid(),
					mSongInfo.getType());
			SongMessage songMessage = new SongMessage();
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.ADDMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}

	}

	/**
	 * 更新歌曲的专辑图片
	 * 
	 * @param sid
	 * @param albumUrlID
	 */
	public void updateSongAlbumUrl(String sid, String albumUrlID) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("albumUrl", albumUrlID);
		try {
			db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });

			SongMessage songMessage = new SongMessage();
			SongInfo songInfo = getSongInfo(sid);
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.UPDATEMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

	/**
	 * 更新歌曲的歌词下载路径
	 * 
	 * @param kscUrl
	 */
	public void updateSongKscUrl(String sid, String kscUrl) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("kscUrl", kscUrl);
		try {
			db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });

			SongMessage songMessage = new SongMessage();
			SongInfo songInfo = getSongInfo(sid);
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.UPDATEMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

	/**
	 * 更新歌手的写真路径
	 * 
	 * @param sid
	 * @param singerPIC
	 *            歌手写真路径
	 */
	public void updateSongSingerPIC(String sid, String singerPIC) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("singerPIC", singerPIC);
		try {
			db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });

			SongMessage songMessage = new SongMessage();
			SongInfo songInfo = getSongInfo(sid);
			songMessage.setSongInfo(songInfo);
			songMessage.setType(SongMessage.UPDATEMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);

		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

	// /**
	// * 更新歌手专辑图片
	// *
	// * @param sid
	// * @param singerPIC1ID
	// * @param singerPIC2ID
	// * @param singerPIC3ID
	// */
	// public void updateSongSingerPIC(String sid, String singerPIC1ID,
	// String singerPIC2ID, String singerPIC3ID) {
	// db = mDBHlper.getReadableDatabase();
	// ContentValues values = new ContentValues();
	// values.put("singerPIC1", singerPIC1ID);
	// values.put("singerPIC2", singerPIC2ID);
	// values.put("singerPIC3", singerPIC3ID);
	// try {
	// db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });
	// } catch (SQLException e) {
	// Log.i("error", "update failed");
	// }
	// }

	/**
	 * 更新歌曲的喜欢状态
	 * 
	 * @param sid
	 * @param status
	 *            是否喜欢的状态
	 */
	public void updatLikeSong(String sid, int status) {
		db = mDBHlper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("islike", status);
		try {
			db.update(TBL_NAME, values, "sid=?", new String[] { sid + "" });
		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}
}