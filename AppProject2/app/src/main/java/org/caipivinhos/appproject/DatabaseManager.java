package org.caipivinhos.appproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DatabaseManager {

    private static final String TABLE_USER = "User";
    private static final String TABLE_SESSION = "Session";
    private static final String TABLE_REPORT = "Report";
    private static final String TABLE_MEDIUM = "MediumValues";
    private final SQLiteDatabase db;

    public DatabaseManager(Context context) {
        db = (new DatabaseCreator(context)).getWritableDatabase();
    }

    public void AddUser(String name, String gender, Integer age){
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("gender", gender);
        cv.put("age",age);

        db.insertOrThrow("User", null, cv);
    }

    public void AddSession(double rrAvg, int stressPercentage, String hourBegin, String date){
        ContentValues cv = new ContentValues();
        int stressLevel = HRVMethods.percLabeling(stressPercentage);
        cv.put("rrAvg", rrAvg);
        cv.put("stressLevel", stressLevel);
        cv.put("hourBegin",hourBegin);
        cv.put("stressPercentage", stressPercentage);
        int idReport = GetIdReport(date);
        cv.put("idReport", idReport);
        UpdateSessionCount(idReport);

        db.insertOrThrow("Session", null, cv);
    }

    public double getMediumLevel() {
        double mediumLevel = 0;
        String gender = "";
        int age = 0;
        String SELECT_1 = String.format("SELECT %s, %s FROM %s",
                "gender",
                "age",
                TABLE_USER);
        Cursor cursor1 = db.rawQuery(SELECT_1, null);
        if(cursor1.getCount()!=0){
            cursor1.moveToNext();
            gender = cursor1.getString(cursor1.getColumnIndexOrThrow("gender"));
            age = cursor1.getInt(cursor1.getColumnIndexOrThrow("age"));
        }
        cursor1.close();

        String SELECT_2 = String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s > %s AND %s < %s",
                "value",
                TABLE_MEDIUM,
                "gender",
                gender,
                age,
                "ageMin",
                age,
                "ageMax"
                );
        Cursor cursor2 = db.rawQuery(SELECT_2, null);
        if(cursor2.getCount()!=0){
            cursor2.moveToNext();
            mediumLevel = cursor2.getDouble(cursor2.getColumnIndexOrThrow("value"));
        }
        cursor2.close();

        return mediumLevel;

    }

    private Integer GetIdReport(String date) {
        Integer idReport;
        String SELECT_QUERY = String.format("SELECT idReport FROM %s WHERE date = '%s'",
                TABLE_REPORT,
                date);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()!=0){
            cursor.moveToNext();
            idReport = cursor.getInt(cursor.getColumnIndexOrThrow("idReport"));
        } else {
            CreateReport(date);
            idReport = GetIdReport(date);
        }

        cursor.close();
        return idReport;
    }

    private void CreateReport(String date) {
        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(calendar.get(Calendar.MINUTE) >= 30) {
            hour += 1;
        }

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("idUser", MainActivity.userId);
        //hour = ----------------------------------> Definir várias horas quando for para simular dados
        values.put("hourBegin", hour);
        db.replace(TABLE_REPORT, null, values);
    }

    public Integer GetUserId() {
        int idUser = 5;
        String SELECT_QUERY = String.format("SELECT idUser FROM %s",
                TABLE_USER);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToNext();
        if(cursor.getCount()!=0){
            idUser = cursor.getInt(cursor.getColumnIndexOrThrow("idUser"));
        }

        cursor.close();
        return idUser;
    }

    public String GetUserName() {
        String user =  "";
        String SELECT_QUERY = String.format("SELECT name FROM %s",
                TABLE_USER);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()!=0){
            cursor.moveToNext();
            user = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        }

        cursor.close();
        return user;
    }

    public void AddComment(String comment, String date) {
        String UPDATE_QUERY = String.format("UPDATE %s SET comment='%s' WHERE idUser = %s AND date = '%s'" ,
                TABLE_REPORT,
                comment,
                MainActivity.userId,
                date);

        db.execSQL(UPDATE_QUERY);
    }

    public String GetComment(String date) {
        String comment;
        String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                "comment",
                TABLE_REPORT,
                "date",
                date);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            cursor.close();
            return null;
        }

        cursor.moveToNext();
        comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));

        cursor.close();
        return comment;
    }

    private void UpdateSessionCount(int idReport) {
        String UPDATE_QUERY = String.format("UPDATE %s SET sessionCount = sessionCount + 1 WHERE idReport = %s",
                TABLE_REPORT,
                idReport);
        db.execSQL(UPDATE_QUERY);
    }


    // VER MELHOR------------------------------------------------ >
    public void UpdateStressAvg(int idReport) {
        List<Integer> sessions = new ArrayList<>();
        String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE idReport = %s",
                "stressLevel",
                TABLE_USER,
                idReport);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            cursor.close();
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
        cursor.close();

        String UPDATE_QUERY = String.format("UPDATE %s SET %s = %s WHERE idReport = %s",
                TABLE_REPORT,
                "stressAvg",
                average,
                idReport);
        db.execSQL(UPDATE_QUERY);
    }

    public ArrayList<Integer> getSessionsPercentageByDate(String date) {
        ArrayList<Integer> percentageValues = new ArrayList<>();

        String SELECT_QUERY = String.format("SELECT %s FROM %s JOIN %s ON %s = %s WHERE date = '%s' ORDER BY %s %s",
                "stressPercentage",
                TABLE_REPORT,
                TABLE_SESSION,
                TABLE_REPORT+".idReport",
                TABLE_SESSION+".idReport",
                date,
                "idSession",
                "ASC");

        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            cursor.close();
            return percentageValues;
        }

        while(cursor.moveToNext()) {
            int rrAvg = cursor.getInt(cursor.getColumnIndexOrThrow("stressPercentage"));
            percentageValues.add(rrAvg);
        }

        cursor.close();
        return percentageValues;
    }

    public int getHourBeginReport(String date) {
        int hour;
        String SELECT_QUERY ="SELECT hourBegin FROM Report WHERE date = '" + date + "'";
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        if(cursor.getCount()!=0){
            cursor.moveToNext();
            hour = cursor.getInt(cursor.getColumnIndexOrThrow("hourBegin"));
        } else {
            hour = -1;
        }

        cursor.close();
        return hour;
    }

    public int[] getStressLevelsPieChart(String date) {
        int[] stressLevels = new int[3];
        String SELECT_QUERY ="SELECT stressLevel FROM Session JOIN Report ON Session.idReport = Report.idReport WHERE date = '" + date + "'";
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()) {
                int sL = cursor.getInt(cursor.getColumnIndexOrThrow("stressLevel"));
                switch (sL) {
                    case 0:
                       stressLevels[0] += 1;
                       break;
                    case 1:
                        stressLevels[1] += 1;
                        break;
                    case 2:
                        stressLevels[2] += 1;
                        break;
                }
            }
        }
        cursor.close();
        return stressLevels;
    }

    public void simulateData() {
        AddSession(0.0,20, "00:00", "13/12/2022");
        AddSession(0.0,20, "00:00", "13/12/2022");
        AddSession(0.0,35, "00:00", "13/12/2022");
        AddSession(0.0,99, "00:00", "13/12/2022");
        AddSession(0.0,10, "00:00", "13/12/2022");
        AddSession(0.0,87, "00:00", "13/12/2022");
        AddSession(0.0,90, "00:00", "13/12/2022");
        AddSession(0.0,56, "00:00", "13/12/2022");
        AddSession(0.0,42, "00:00", "13/12/2022");
        AddSession(0.0,75, "00:00", "13/12/2022");
        AddSession(0.0,38, "00:00", "13/12/2022");
        AddSession(0.0,93, "00:00", "13/12/2022");
        AddSession(0.0,63, "00:00", "13/12/2022");
        AddSession(0.0,89, "00:00", "13/12/2022");
        AddSession(0.0,59, "00:00", "13/12/2022");

        AddSession(0.0,19, "00:00", "14/12/2022");
        AddSession(0.0,45, "00:00", "14/12/2022");
        AddSession(0.0,78, "00:00", "14/12/2022");
        AddSession(0.0,34, "00:00", "14/12/2022");
        AddSession(0.0,10, "00:00", "14/12/2022");
        AddSession(0.0,65, "00:00", "14/12/2022");
        AddSession(0.0,34, "00:00", "14/12/2022");
        AddSession(0.0,79, "00:00", "14/12/2022");
        AddSession(0.0,68, "00:00", "14/12/2022");
        AddSession(0.0,80, "00:00", "14/12/2022");
        AddSession(0.0,90, "00:00", "14/12/2022");
        AddSession(0.0,95, "00:00", "14/12/2022");
        AddSession(0.0,23, "00:00", "14/12/2022");
        AddSession(0.0,65, "00:00", "14/12/2022");
        AddSession(0.0,79, "00:00", "14/12/2022");

        AddSession(0.0,14, "00:00", "15/12/2022");
        AddSession(0.0,25, "00:00", "15/12/2022");
        AddSession(0.0,78, "00:00", "15/12/2022");
        AddSession(0.0,90, "00:00", "15/12/2022");
        AddSession(0.0,34, "00:00", "15/12/2022");
        AddSession(0.0,35, "00:00", "15/12/2022");
        AddSession(0.0,88, "00:00", "15/12/2022");
        AddSession(0.0,97, "00:00", "15/12/2022");
        AddSession(0.0,79, "00:00", "15/12/2022");
        AddSession(0.0,67, "00:00", "15/12/2022");
        AddSession(0.0,67, "00:00", "15/12/2022");
        AddSession(0.0,56, "00:00", "15/12/2022");
        AddSession(0.0,45, "00:00", "15/12/2022");
        AddSession(0.0,34, "00:00", "15/12/2022");
        AddSession(0.0,22, "00:00", "15/12/2022");
    }
}
