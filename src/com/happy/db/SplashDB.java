package com.happy.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.happy.model.pc.Splash;

public class SplashDB {
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "splashTbl";
	/**
	 * 建表语句
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + "("
			+ "sid text," + "title text," + "createTime text,"
			+ "startTime text," + "endTime text," + "updateTime text" + ")";

	private static SplashDB _SplashDB;

	private SQLiteDatabase db;

	private SQLDBHlper mDBHlper;

	public SplashDB(Context context) {
		mDBHlper = SQLDBHlper.getSQLDBHlper(context);
	}

	public static SplashDB getSplashDB(Context context) {
		if (_SplashDB == null) {
			_SplashDB = new SplashDB(context);
		}
		return _SplashDB;
	}

	/**
	 * 
	 * @Title: add
	 * @Description: (添加主题)
	 * @param: @param splash
	 * @return: void
	 * @throws
	 */
	public void add(Splash splash) {
		ContentValues values = new ContentValues();

		values.put("sid", splash.getSid());
		values.put("title", splash.getTitle());
		values.put("createTime", splash.getCreateTime());
		values.put("startTime", splash.getStartTime());
		values.put("endTime", splash.getEndTime());
		values.put("updateTime", splash.getUpdateTime());

		insert(values);
	}

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
	 */
	public Cursor query() {
		db = mDBHlper.getReadableDatabase();
		Cursor c = db.query(TBL_NAME, null, null, null, null, null,
				"createTime desc");
		return c;
	}

	public List<Splash> getAllSplash() {
		List<Splash> lists = new ArrayList<Splash>();
		Cursor cursor = query();
		while (cursor.moveToNext()) {
			Splash splash = getSplash(cursor);
			lists.add(splash);
		}
		cursor.close();

		return lists;
	}

	private Splash getSplash(Cursor cursor) {
		Splash splash = new Splash();

		splash.setSid(cursor.getString(cursor.getColumnIndex("sid")));
		splash.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		splash.setCreateTime(cursor.getString(cursor
				.getColumnIndex("createTime")));
		splash.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
		splash.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
		splash.setUpdateTime(cursor.getString(cursor
				.getColumnIndex("updateTime")));

		return splash;
	}

	/**
	 * 
	 * @Title: splashIsExists
	 * @param: @param sid
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public boolean splashIsExists(String sid) {
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

}
