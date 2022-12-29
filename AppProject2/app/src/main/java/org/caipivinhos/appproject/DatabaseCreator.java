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
    private static final String TABLE_MEDIUM = "MediumValues";

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
        db.execSQL("CREATE TABLE User (idUser INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, gender TEXT NOT NULL CHECK(gender=='female' OR gender=='male'), age INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE Report (idReport INTEGER PRIMARY KEY AUTOINCREMENT, stressAvg DOUBLE, sessionCount INTEGER DEFAULT 0, hourBegin INTEGER NOT NULL, comment TEXT, mood TEXT, date TEXT NOT NULL, idUser INTEGER NOT NULL REFERENCES User);");
        db.execSQL("CREATE TABLE Session (idSession INTEGER PRIMARY KEY AUTOINCREMENT, stressLevel INTEGER NOT NULL, stressPercentage INTEGER NOT NULL, hourBegin TEXT NOT NULL, idReport INTEGER NOT NULL REFERENCES Report);");
        db.execSQL("CREATE TABLE MediumValues (id INTEGER PRIMARY KEY AUTOINCREMENT, value DOUBLE NOT NULL, gender TEXT NOT NULL CHECK(gender=='female' OR gender=='male'), ageMin INTEGER NOT NULL, ageMax INTEGER NOT NULL);");

        setMediumValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIUM);
        onCreate(db);
    }

    private void setMediumValues(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put("value", 39.7);
        cv.put("gender", "male");
        cv.put("ageMin", 15);
        cv.put("ageMax", 34);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 32.0);
        cv.put("gender", "male");
        cv.put("ageMin", 35);
        cv.put("ageMax", 44);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 23.0);
        cv.put("gender", "male");
        cv.put("ageMin", 45);
        cv.put("ageMax", 54);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 19.9);
        cv.put("gender", "male");
        cv.put("ageMin", 55);
        cv.put("ageMax", 64);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 19.1);
        cv.put("gender", "male");
        cv.put("ageMin", 65);
        cv.put("ageMax", 74);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 42.9);
        cv.put("gender", "female");
        cv.put("ageMin", 15);
        cv.put("ageMax", 34);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 35.4);
        cv.put("gender", "female");
        cv.put("ageMin", 35);
        cv.put("ageMax", 44);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 26.3);
        cv.put("gender", "female");
        cv.put("ageMin", 45);
        cv.put("ageMax", 54);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 21.4);
        cv.put("gender", "female");
        cv.put("ageMin", 55);
        cv.put("ageMax", 64);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 19.1);
        cv.put("gender", "female");
        cv.put("ageMin", 65);
        cv.put("ageMax", 74);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 0.0);
        cv.put("gender", "female");
        cv.put("ageMin", 10);
        cv.put("ageMax", 20);
        db.insertOrThrow("MediumValues", null, cv);

        cv.put("value", 0.0);
        cv.put("gender", "female");
        cv.put("ageMin", 10);
        cv.put("ageMax", 20);
        db.insertOrThrow("MediumValues", null, cv);
    }
}
