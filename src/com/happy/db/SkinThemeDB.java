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

import com.happy.model.app.SkinThemeApp;
import com.happy.util.DateUtil;

public class SkinThemeDB {
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "skinthemeTbl";
	/**
	 * 建表语句
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + "("
			+ "sid text," + "id text," + "themeName text," + "assetsType int,"
			+ "previewPath text," + "unZipPath text," + "previewUrl text,"
			+ "downloadUrl text," + "themeType int," + "fileSize long,"
			+ "fileSizeStr text," + "progerssFileSize long," + "status int,"
			+ "addtime text," + "downloadPath text" + ")";

	private static SkinThemeDB _SkinThemeDB;

	private SQLiteDatabase db;

	private SQLDBHlper mDBHlper;

	public SkinThemeDB(Context context) {
		mDBHlper = SQLDBHlper.getSQLDBHlper(context);
	}

	public static SkinThemeDB getSkinThemeDB(Context context) {
		if (_SkinThemeDB == null) {
			_SkinThemeDB = new SkinThemeDB(context);
		}
		return _SkinThemeDB;
	}

	/**
	 * 
	 * @Title: add
	 * @Description: (添加主题)
	 * @param: @param skinTheme
	 * @return: void
	 * @throws
	 */
	public void add(SkinThemeApp skinTheme) {
		ContentValues values = new ContentValues();

		skinTheme.setSid(getSID());
		values.put("sid", skinTheme.getSid());
		values.put("id", skinTheme.getID());
		values.put("themeName", skinTheme.getThemeName());
		values.put("assetsType", skinTheme.getAssetsType());
		values.put("previewPath", skinTheme.getPreviewPath());
		values.put("unZipPath", skinTheme.getUnZipPath());
		values.put("previewUrl", skinTheme.getPreviewUrl());
		values.put("downloadUrl", skinTheme.getDownloadUrl());
		values.put("themeType", skinTheme.getThemeType());
		values.put("fileSize", skinTheme.getFileSize());
		values.put("fileSizeStr", skinTheme.getFileSizeStr());
		values.put("progerssFileSize", skinTheme.getProgerssFileSize());
		values.put("status", skinTheme.getStatus());
		values.put("addtime", DateUtil.dateToOtherString(new Date()));
		values.put("downloadPath", skinTheme.getDownloadPath());

		insert(values);
	}

	/**
	 * 
	 * @Title: getSID
	 * @Description: (获取自动生成的sid)
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	private String getSID() {
		String sid = new Date().getTime() + "_" + new Date().getTime();
		return sid;
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
	 */
	public Cursor query() {
		db = mDBHlper.getReadableDatabase();
		Cursor c = db.query(TBL_NAME, null, null, null, null, null,
				"addtime asc");
		return c;
	}

	/**
	 * 
	 * @Title: getAllSkinTheme
	 * @Description: (获取所有的皮肤主题)
	 * @param: @return
	 * @return: List<SkinTheme>
	 * @throws
	 */
	public List<SkinThemeApp> getAllSkinTheme() {
		List<SkinThemeApp> list = new ArrayList<SkinThemeApp>();
		Cursor cursor = query();
		while (cursor.moveToNext()) {
			SkinThemeApp skinTheme = getSkinTheme(cursor);
			// 网络类型，要判断本地皮肤包是否存在，如果不存在，则删除相关的皮肤数据
			if (skinTheme.getAssetsType() == SkinThemeApp.NET) {
				File file = new File(skinTheme.getDownloadPath());
				if (!file.exists()) {
					delete(skinTheme.getSid());
					continue;
				}
			}
			list.add(skinTheme);
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
	public SkinThemeApp getSkinThemeInfo(String id) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.rawQuery(
				"select * from " + TBL_NAME + " where id=?", new String[] { id
						+ "" });
		if (!cursor.moveToNext()) {
			return null;
		}
		SkinThemeApp skinTheme = getSkinTheme(cursor);
		if (skinTheme.getAssetsType() == SkinThemeApp.NET) {
			File file = new File(skinTheme.getDownloadPath());
			if (!file.exists()) {
				delete(skinTheme.getSid());
				return null;
			}
		}
		cursor.close();
		return skinTheme;
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: (通过sid来删除相关的数据)
	 * @param: @param sid
	 * @return: void
	 * @throws
	 */
	private void delete(String sid) {
		db = mDBHlper.getWritableDatabase();
		try {
			db.delete(TBL_NAME, "sid=?", new String[] { sid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: getSkinTheme
	 * @Description: (通过cursor获取相关的数据)
	 * @param: @param cursor
	 * @param: @return
	 * @return: SkinTheme
	 * @throws
	 */
	private SkinThemeApp getSkinTheme(Cursor cursor) {
		SkinThemeApp skinTheme = new SkinThemeApp();

		skinTheme.setSid(cursor.getString(cursor.getColumnIndex("sid")));
		skinTheme.setID(cursor.getString(cursor.getColumnIndex("id")));
		skinTheme.setThemeName(cursor.getString(cursor
				.getColumnIndex("themeName")));
		int assetsType = cursor.getInt(cursor.getColumnIndex("assetsType"));
		skinTheme.setAssetsType(assetsType);
		if (assetsType != SkinThemeApp.LOCAL) {
			// skinTheme.setPreviewUrl(cursor.getString(cursor
			// .getColumnIndex("previewUrl")));
			// skinTheme.setDownloadUrl(cursor.getString(cursor
			// .getColumnIndex("downloadUrl")));
			// skinTheme.setThemeType(cursor.getInt(cursor
			// .getColumnIndex("themeType")));
			// skinTheme.setFileSize(cursor.getLong(cursor
			// .getColumnIndex("fileSize")));
			// skinTheme.setFileSizeStr(cursor.getString(cursor
			// .getColumnIndex("fileSizeStr")));
			// skinTheme.setProgerssFileSize(cursor.getLong(cursor
			// .getColumnIndex("progerssFileSize")));
			// skinTheme.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
			skinTheme.setAddTime(cursor.getString(cursor
					.getColumnIndex("addtime")));
			skinTheme.setDownloadPath(cursor.getString(cursor
					.getColumnIndex("downloadPath")));
		} else {
			skinTheme.setPreviewPath(cursor.getString(cursor
					.getColumnIndex("previewPath")));
		}
		skinTheme.setUnZipPath(cursor.getString(cursor
				.getColumnIndex("unZipPath")));
		return skinTheme;
	}

	/**
	 * 
	 * @Title: skinThemeIsExists
	 * @Description: (根据皮肤的id来判断皮肤是否存在)
	 * @param: @param id
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public boolean skinThemeIsExists(String id) {
		db = mDBHlper.getReadableDatabase();
		// 第一个参数String：表名
		// 第二个参数String[]:要查询的列名
		// 第三个参数String：查询条件
		// 第四个参数String[]：查询条件的参数
		// 第五个参数String:对查询的结果进行分组
		// 第六个参数String：对分组的结果进行限制
		// 第七个参数String：对查询的结果进行排序
		Cursor cursor = db.query(TBL_NAME, new String[] { "id" }, " id=?",
				new String[] { id }, null, null, null);
		if (!cursor.moveToNext()) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

}
