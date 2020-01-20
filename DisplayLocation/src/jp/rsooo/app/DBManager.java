package jp.rsooo.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * DBをいじるクラス
 * @author akira
 *
 */
public class DBManager {
	public SQLiteDatabase mDb;
	public static final int DBVER = 1;
	public static final String TABLENAME = "locationTable";
	public static final String DROP_SQL = "drop table " + TABLENAME + ";";
	public static final String CREATE_SQL = "create table " + TABLENAME + 
									"( _id integer primary key autoincrement," + 
									" number integer not null," + 
									" location text not null)";
			
	public DBHelper dbHelper;
	
	public DBManager(Context c){
		dbHelper = new DBHelper(c);
		mDb = dbHelper.getReadableDatabase();
//		showDB();
	}
	
	public void dropTable(){
		if(mDb != null){
			mDb.execSQL(DROP_SQL);
			mDb = null;
		}
	}
	
	public void showDB(){
		final String[] projection = new String[]{"number", "location"};
		final int NUM_INDEX = 0;
		final int MEMO_INDEX = 1;
		
		Cursor c = mDb.query(TABLENAME, projection, null, null , null, null, "_id DESC");
		
		if(c.moveToFirst()){
			do{
				long id = c.getLong(NUM_INDEX);
				String memo = c.getString(MEMO_INDEX);
				Log.i("TAG", id + ":" + memo);
			}while(c.moveToNext());
		}
	}
	
	public String query(String numStr){
		final String[] projection = new String[]{"number", "location"};
		final int NUM_INDEX = 0;
		final int MEMO_INDEX = 1;
		
		Cursor c = mDb.query(TABLENAME, projection, "number = ?", new String[]{numStr}, null, null, "_id DESC", "1");
		
		if(c.moveToFirst()){
			do{
				long id = c.getLong(NUM_INDEX);
				String memo = c.getString(MEMO_INDEX);
				Log.i("TAG", id + ":" + memo);
				return memo;
			}while(c.moveToNext());
		}
		return null;
	}
	
	

	
	 private static class DBHelper extends SQLiteOpenHelper{

	    	public DBHelper(Context c){
	    		super(c, "testdb.db", null, DBVER);
	    	}
	    	
	    	/**
	    	 * データベースが作成されるときに呼び出される
	    	 * 今回はデータベースは初期化したら後は変更しないのでここで
	    	 * データ追加
	    	 */
			@Override
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				db.execSQL(CREATE_SQL);
				Log.i("debug", "tabke is created");
				initData(db);
				Log.i("debug", "init data");
				
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				Log. i("debug2", "db update");
				db.execSQL(DROP_SQL);
				onCreate(db);
				//db.execSQL(CREATE_SQL);
				//initData(db);
				
			}
	    	
			private void initData(SQLiteDatabase db){
				try{
				db.beginTransaction();
				for (final String data : Data.DATA) {
					String[] datam = data.split(";");
					long num = Long.parseLong(datam[0]);
					String location = datam[1];
					SQLiteStatement stmt = db.compileStatement("insert into "
							+ TABLENAME + " values (?, ?, ?);");
					stmt.bindLong(2, num);
					stmt.bindString(3, location);
					stmt.executeInsert();

				}
				db.setTransactionSuccessful();
				}finally{
					db.endTransaction();
				}
				Log.i("debug", "init data finished");
			}
	    }
}
