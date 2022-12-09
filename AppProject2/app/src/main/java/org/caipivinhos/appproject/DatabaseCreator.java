package org.caipivinhos.appproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCreator extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";
    private static final String TABLE_USER = "User";
    private static final String TABLE_SESSION = "SESSION";
    private static final String TABLE_REPORT = "REPORT";

    public DatabaseCreator( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

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

        /*
        ContentValues cv = new ContentValues();

        cv.put("name", "ze");
        cv.put("gender", "Male");
        cv.put("age", 21);
        db.insertOrThrow("User", null, cv);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
