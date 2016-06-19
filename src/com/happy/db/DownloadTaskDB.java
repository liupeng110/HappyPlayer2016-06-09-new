package com.happy.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.happy.model.app.DownloadTask;

public class DownloadTaskDB {
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "downloadtaskTbl";

	/**
	 * 建表语句
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + "("
			+ "tid text," + "tName text," + "status int," + "downloadUrl text,"
			+ "filePath text," + "fileSize long," + "downloadedSize long,"
			+ "addTime text," + "finishTime text," + "type int" + ")";

	private static DownloadTaskDB _downloadTaskDB;

	private SQLiteDatabase db;

	private SQLDBHlper mDBHlper;

	public DownloadTaskDB(Context context) {
		mDBHlper = SQLDBHlper.getSQLDBHlper(context);
	}

	public static DownloadTaskDB getDownloadTaskDB(Context context) {
		if (_downloadTaskDB == null) {
			_downloadTaskDB = new DownloadTaskDB(context);
		}
		return _downloadTaskDB;
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public void add(DownloadTask task) {
		ContentValues values = new ContentValues();

		values.put("tid", task.getTid());
		values.put("tName", task.gettName());
		values.put("status", task.getStatus());
		values.put("downloadUrl", task.getDownloadUrl());
		values.put("filePath", task.getFilePath());
		values.put("fileSize", task.getFileSize());
		values.put("downloadedSize", task.getDownloadedSize());
		values.put("addTime", task.getAddTime());
		values.put("finishTime", task.getFinishTime());
		values.put("type", task.getType());

		insert(values);
	}

	/**
	 * 
	 * @Title: insert
	 * @Description: (插入数据)
	 * @param: @param values
	 * @param: @param skinTheme 该参数用于观察者通知使用
	 * @return: void
	 * @throws
	 */
	private void insert(ContentValues values) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.insert(TBL_NAME, null, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取相关的Cursor
	 * 
	 * @param type
	 */
	public Cursor query(int type) {
		db = mDBHlper.getReadableDatabase();
		// Cursor cursor = db.query(TBL_NAME, new String[] { "type" },
		// " type=?",
		// new String[] { type + "" }, null, null, "addTime asc");

		Cursor c = db
				.rawQuery(
						"select * from downloadtaskTbl where type=? order by addTime desc",
						new String[] { type + "" });
		return c;
	}

	/**
	 * 获取所有的任务
	 * 
	 * @return
	 */
	public List<DownloadTask> getAllDownloadTask(int type) {
		List<DownloadTask> list = new ArrayList<DownloadTask>();
		Cursor cursor = query(type);
		while (cursor.moveToNext()) {
			DownloadTask downloadTask = getDownloadTask(cursor);
			File file = new File(downloadTask.getFilePath());
			if (!file.exists()) {
				delete(downloadTask.getTid());
				continue;
			}
			list.add(downloadTask);
		}
		cursor.close();
		return list;
	}

	/**
	 * 
	 * @Title: getSongInfo
	 * @Description: (通过id来获取默认的皮肤主题)
	 * @param: @param id
	 * @param: @return
	 * @return: SkinTheme
	 * @throws
	 */
	public DownloadTask getDownloadTask(String tid, int type) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.rawQuery("select * from " + TBL_NAME
				+ " where tid=? and type=?",
				new String[] { tid + "", type + "" });
		if (!cursor.moveToNext()) {
			return null;
		}
		DownloadTask downloadTask = getDownloadTask(cursor);
		File file = new File(downloadTask.getFilePath());
		if (!file.exists()) {
			delete(downloadTask.getTid());
			return null;
		}
		cursor.close();
		return downloadTask;
	}

	/**
	 * 根据cursor来获取下载的任务内容
	 * 
	 * @param cursor
	 * @return
	 */
	private DownloadTask getDownloadTask(Cursor cursor) {

		DownloadTask downloadTask = new DownloadTask();

		downloadTask.setTid(cursor.getString(cursor.getColumnIndex("tid")));

		int status = cursor.getInt(cursor.getColumnIndex("status"));
		// if (status != DownloadTask.DOWNLOAD_FINISH || status !=
		// DownloadTask.WAITING) {
		// downloadTask.setStatus(DownloadTask.INT);
		// } else {
		downloadTask.setStatus(status);
		// }

		downloadTask.settName(cursor.getString(cursor.getColumnIndex("tName")));

		downloadTask.setDownloadUrl(cursor.getString(cursor
				.getColumnIndex("downloadUrl")));
		downloadTask.setFilePath(cursor.getString(cursor
				.getColumnIndex("filePath")));
		downloadTask.setFileSize(cursor.getLong(cursor
				.getColumnIndex("fileSize")));
		downloadTask.setDownloadedSize(cursor.getLong(cursor
				.getColumnIndex("downloadedSize")));
		downloadTask.setAddTime(cursor.getString(cursor
				.getColumnIndex("addTime")));
		downloadTask.setFinishTime(cursor.getString(cursor
				.getColumnIndex("finishTime")));
		downloadTask.setType(cursor.getInt(cursor.getColumnIndex("type")));

		return downloadTask;
	}

	/**
	 * 根据tid来任务类型来判断该任务是否存在
	 * 
	 * @param tid
	 * @param type
	 * @return
	 */
	public boolean taskIsExists(String tid, int type) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.query(TBL_NAME, new String[] { "tid", "type" },
				" tid=? and type=?", new String[] { tid, type + "" }, null,
				null, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: (通过sid来删除相关的数据)
	 * @param: @param tid
	 * @return: void
	 * @throws
	 */
	public void delete(String tid) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.delete(TBL_NAME, "tid=?", new String[] { tid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param tid
	 * @param type
	 */
	public void deletes(String tid, int type) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.delete(TBL_NAME, "tid!=? and type=?", new String[] { tid,
					type + "" });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新任务
	 * 
	 * @param task
	 */
	public void update(DownloadTask task) {
		db = mDBHlper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", task.getStatus());
		values.put("fileSize", task.getFileSize());
		values.put("downloadedSize", task.getDownloadedSize());
		values.put("finishTime", task.getFinishTime());
		try {
			db.update(TBL_NAME, values, "tid=? and type=?",
					new String[] { task.getTid(), task.getType() + "" });
		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

}
