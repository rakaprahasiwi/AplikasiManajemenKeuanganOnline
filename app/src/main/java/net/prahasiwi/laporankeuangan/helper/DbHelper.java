package net.prahasiwi.laporankeuangan.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PRAHASIWI on 20/03/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

//    private final static String URL = "http://nginovasi.com/retrofit21/"; //server NGI
    private final static String URL = "http://lapkeapp.000webhostapp.com/retrofit21/"; //server free

    private final static String DATABASE_NAME = "db_user";
    private final static String TABLE_USER = "tb_user";
    private final static String TABLE_SERVER= "tb_server";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private final static String URL_SERVER = "url";
    private final static int DATABASE_VERSION = 4;

    private final static String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
            + " (" + EMAIL + " VARCHAR(50), "
            + PASSWORD + " VARCHAR(20)) ";
    private final static String CREATE_TABLE_SERVER = "CREATE TABLE " + TABLE_SERVER
            +"("+ URL_SERVER + " VARCHAR(100)) ";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_SERVER);
        String sql = "INSERT INTO " + TABLE_SERVER + " ( " + URL_SERVER + ") VALUES ('"+URL+"');";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
        onCreate(db);
    }

    public long insertdata(String email, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EMAIL, email);
        contentValues.put(PASSWORD, pass);
        long id = db.insert(TABLE_USER, null, contentValues);
        db.close();
        return id;
    }

    public long delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER,null,null);
    }

    public String getEmailDb(){
        String email = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columName = {EMAIL};
        Cursor cursor = db.query(TABLE_USER, columName, null, null,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                email = cursor.getString(cursor.getColumnIndex(EMAIL));
            }
        }
        db.close();
        return email;
    }

    public boolean isTableEmpty(){
        boolean empty = true;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_USER, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();
        return empty;
    }

    public String getUrlServer(){
        String url = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columName = {URL_SERVER};
        Cursor cursor = db.query(TABLE_SERVER, columName, null, null,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                url = cursor.getString(cursor.getColumnIndex(URL_SERVER));
            }
        }
        db.close();
        return url;
    }

    public void setUrlServer(String newurl){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(URL_SERVER, newurl);
        db.update(TABLE_SERVER,values, null,null);
    }
}
