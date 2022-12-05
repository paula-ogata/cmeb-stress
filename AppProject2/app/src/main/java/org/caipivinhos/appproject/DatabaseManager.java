package org.caipivinhos.appproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";

    private static final String TABLE_USER = "User";
    private static final String TABLE_SESSION = "SESSION";
    private static final String TABLE_REPORT = "REPORT";

    public static final String TITLE = "title";
    public static final String VALUE = "value";

    public DatabaseManager( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            onCreate(db);
        }
    }

    /*
    public static synchronized DatabaseManager getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }
    */

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE User (idUser INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, pin INTEGER, gender TEXT NOT NULL CHECK(gender=='Female' OR gender=='Male'), age INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE Report (idReport INTEGER PRIMARY KEY AUTOINCREMENT, stressAvg INTEGER, sessionCount INTEGER DEFAULT 0, comment TEXT, date TEXT NOT NULL, idUser INTEGER NOT NULL REFERENCES User);");
        db.execSQL("CREATE TABLE Session (idSession INTEGER PRIMARY KEY AUTOINCREMENT, rrAvg INTEGER NOT NULL, stressLevel INTEGER NOT NULL, hourBegin TEXT NOT NULL, duration DOUBLE NOT NULL, idReport INTEGER NOT NULL REFERENCES Report);");
    }

    public boolean AddUser(String name, String gender, Integer age){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("gender", gender);
        cv.put("age",age);
        db.beginTransaction();
        try{
            db.insertOrThrow("User", null, cv);
            return true;
        } catch(android.database.SQLException sqlException) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean AddSession(Integer stressLevel, String hourBegin, Double duration, String date){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("stressLevel", stressLevel);
        cv.put("hourBegin",hourBegin);
        cv.put("duration", duration);
        int idReport = GetIdReport(date);
        cv.put("idReport", idReport);
        UpdateSessionCount(idReport);

        db.beginTransaction();
        try{
            db.insertOrThrow("Session", null, cv);
            return true;
        } catch(android.database.SQLException sqlException) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public Integer GetIdReport(String date) {
        List<Integer> idReports = new ArrayList<>();
        Integer idReport;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT_QUERY = String.format("SELECT idReport FROM %s WHERE date = %s",
                TABLE_REPORT,
                date);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()!=0){
            idReport = cursor.getInt(cursor.getColumnIndexOrThrow("idReport"));
        } else {
            CreateReport(date);
            idReport = GetIdReport(date);
        }
        return idReport;
    }

    private void CreateReport(String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("userID", MainActivity.userId);
        db.replace(DATABASE_NAME, null, values);
    }

    public Integer GetUserId(String username) {
        int idUser = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT_QUERY = String.format("SELECT idUser FROM %s WHERE name = %s",
                TABLE_USER,
                username);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()!=0){
            idUser = cursor.getInt(cursor.getColumnIndexOrThrow("idReport"));
        }
        return idUser;
    }

    public void AddComment(String comment, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_QUERY = String.format("UPDATE %s SET comment=%s WHERE idUser = %s AND date = %s" ,
                TABLE_REPORT,
                comment,
                MainActivity.userId,
                date);

        db.execSQL(UPDATE_QUERY);
    }

    private void UpdateSessionCount(int idReport) {
        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_QUERY = String.format("UPDATE %s SET sessionCount = sessionCount + 1 WHERE idReport = %s",
                TABLE_REPORT,
                idReport);
        db.execSQL(UPDATE_QUERY);
    }

    public void UpdateStressAvg(int idReport) {
        List<Integer> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE idReport = %s",
                "stressLevel",
                TABLE_USER,
                idReport);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            return;
        }

        while(cursor.moveToNext()) {
            sessions.add(cursor.getInt(cursor.getColumnIndexOrThrow("stressLevel")));
        }

        int aux = 0;
        for(int i=0; i<sessions.size(); i++) {
            aux += sessions.get(i);
        }

        int average = aux/sessions.size();

        String UPDATE_QUERY = String.format("UPDATE %s SET %s = %s WHERE idReport = %s",
                TABLE_REPORT,
                "stressAvg",
                average,
                idReport);
        db.execSQL(UPDATE_QUERY);
    }

    public HashMap<String, Integer> getStressLevelsReport (String date) {
        HashMap<String, Integer> values = new HashMap<String, Integer>();
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT_QUERY = String.format("SELECT %s, %s FROM %s, %s WHERE date = %s ORDER BY %s %s",
                "stressLevel",
                "hourBegin",
                TABLE_REPORT,
                TABLE_SESSION,
                date,
                "idSession",
                "ASC");

        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            return values;
        }

        while(cursor.moveToNext()) {
            int stressLevel = cursor.getInt(cursor.getColumnIndexOrThrow("stressLevel"));
            String hourBegin = cursor.getString(cursor.getColumnIndexOrThrow("hourBegin"));
            values.put(hourBegin, stressLevel);
        }

        return values;
    }
}
