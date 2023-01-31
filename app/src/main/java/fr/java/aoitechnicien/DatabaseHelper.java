package fr.java.aoitechnicien;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aoi_tech.db";
    private static final int DATABASE_VERSION = 1;
    String sql;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        sql = "CREATE TABLE IF NOT EXISTS sync (id INTEGER PRIMARY KEY AUTOINCREMENT, created_at DATETIME NOT NULL)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, lastname TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL, created_at DATETIME NOT NULL, deleted_at DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS item_site (id INTEGER PRIMARY KEY AUTOINCREMENT, fk_item_id INT NOT NULL, fk_site_id INT NOT NULL, label TEXT NOT NULL, created_at DATETIME NOT NULL, deleted_at DATETIME, gen_planning TINYINT(1), uuid CHAR(36) NOT NULL)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS item (id INTEGER PRIMARY KEY AUTOINCREMENT, fk_category_id INT NOT NULL, label TEXT NOT NULL, created_at DATETIME NOT NULL, deleted_at DATETIME)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*sql = "DROP TABLE IF EXISTS user";
        db.execSQL(sql);
        onCreate(db);*/
    }

    public boolean onClean(SQLiteDatabase db) {
        sql = "DROP TABLE IF EXISTS sync";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS user";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS item_site";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS item";
        db.execSQL(sql);
        onCreate(db);
        return true;
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        Log.i("THREAD INFO", " -- DB COUNT :: " + cursor.getCount() + " --");
        cursor.close();
        return exists;
    }

    public boolean isSync(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT id FROM sync WHERE id='1'", null);
        boolean exists = cursor.getCount() > 0;
        Log.i("THREAD INFO", " -- SYNC COUNT :: " + cursor.getCount() + " --");
        cursor.close();
        return exists;
    }
}
