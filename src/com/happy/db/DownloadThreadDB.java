package com.happy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.happy.model.app.DownloadThreadInfo;

/**
 * 下载任务线程表
 * 
 * @author zhangliangming
 * 
 */
public class DownloadThreadDB {
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "downloadThreadTbl";

	/**
	 * 建表语句
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + "("
			+ "tid text," + "threadNum int," + "threadID int,"
			+ "startIndex long," + "endIndex long," + "type int" + ")";

	private static DownloadThreadDB _downloadThreadDB;

	private SQLiteDatabase db;

	private SQLDBHlper mDBHlper;

	public DownloadThreadDB(Context context) {
		mDBHlper = SQLDBHlper.getSQLDBHlper(context);
	}

	public static DownloadThreadDB getDownloadThreadDB(Context context) {
		if (_downloadThreadDB == null) {
			_downloadThreadDB = new DownloadThreadDB(context);
		}
		return _downloadThreadDB;
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public void add(DownloadThreadInfo downloadThreadInfo) {
		ContentValues values = new ContentValues();

		values.put("tid", downloadThreadInfo.getTid());
		values.put("threadNum", downloadThreadInfo.getThreadNum());
		values.put("threadID", downloadThreadInfo.getThreadID());
		values.put("startIndex", downloadThreadInfo.getStartIndex());
		values.put("endIndex", downloadThreadInfo.getEndIndex());
		values.put("type", downloadThreadInfo.getType());

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
	 * 获取下载任务线程
	 * 
	 * @param tid
	 * @param threadNum
	 * @param threadID
	 * @return
	 */
	public DownloadThreadInfo getDownloadThreadInfo(String tid, int threadNum,
			int threadID, int type) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.rawQuery("select * from " + TBL_NAME
				+ " where tid=? and threadNum=? and threadID=? and type=?",
				new String[] { tid + "", threadNum + "", threadID + "",
						type + "" });
		if (!cursor.moveToNext()) {
			return null;
		}
		DownloadThreadInfo downloadThreadInfo = getDownloadThreadInfo(cursor);
		cursor.close();
		return downloadThreadInfo;
	}

	private DownloadThreadInfo getDownloadThreadInfo(Cursor cursor) {
		DownloadThreadInfo downloadThreadInfo = new DownloadThreadInfo();

		downloadThreadInfo
				.setTid(cursor.getString(cursor.getColumnIndex("tid")));
		downloadThreadInfo.setThreadNum(cursor.getInt(cursor
				.getColumnIndex("threadNum")));
		downloadThreadInfo.setThreadID(cursor.getInt(cursor
				.getColumnIndex("threadID")));
		downloadThreadInfo.setStartIndex(cursor.getInt(cursor
				.getColumnIndex("startIndex")));
		downloadThreadInfo.setEndIndex(cursor.getInt(cursor
				.getColumnIndex("endIndex")));
		downloadThreadInfo
				.setType(cursor.getInt(cursor.getColumnIndex("type")));

		return downloadThreadInfo;
	}

	/**
	 * 更新任务
	 * 
	 * @param task
	 */
	public void update(String tid, int threadNum, int threadID, int startIndex,
			int type) {
		db = mDBHlper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("startIndex", startIndex);

		try {
			db.update(TBL_NAME, values,
					"tid=? and threadNum=? and threadID=? and type=?",
					new String[] { tid, threadNum + "", threadID + "",
							type + "" });
		} catch (SQLException e) {
			Log.i("error", "update failed");
		}
	}

}
