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

    public void AddSession(int stressPercentage, String hourBegin, String date){
        ContentValues cv = new ContentValues();
        int stressLevel = HRVMethods.percLabeling(stressPercentage);
        cv.put("stressLevel", stressLevel);
        cv.put("hourBegin",hourBegin);
        cv.put("stressPercentage", stressPercentage);
        int idReport = GetIdReport(date);
        cv.put("idReport", idReport);
        UpdateSessionCount(idReport);
        UpdateStressAvg(idReport);

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

    // João: alterações
    public void UpdateStressAvg(int idReport) {
        List<Integer> sessions = new ArrayList<>();

        String SELECT_QUERY = String.format("SELECT %s FROM %s JOIN %s ON %s = %s WHERE %s = %s",
                "stressPercentage",
                TABLE_SESSION,
                TABLE_REPORT,
                TABLE_REPORT+".idReport",
                TABLE_SESSION+".idReport",
                TABLE_SESSION+".idReport",
                idReport);

        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor.getCount()==0){
            cursor.close();
            return;
        }

        while(cursor.moveToNext()) {
            sessions.add(cursor.getInt(cursor.getColumnIndexOrThrow("stressPercentage")));
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

    // Obter valor médio do stress em determinado dia (usado no Weekly Report)
    public Integer getAvgPercentageByDate(String date) {
        Integer avgPercentageValue = null;

        String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE date = '%s' ORDER BY %s %s",
                "stressAvg",
                TABLE_REPORT,
                date,
                "idReport",
                "ASC");

        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        if(cursor.getCount()==0){
            cursor.close();
            return avgPercentageValue;
        }

        cursor.moveToFirst();
        avgPercentageValue = cursor.getInt(cursor.getColumnIndexOrThrow("stressAvg"));
        cursor.close();
        return avgPercentageValue;
    }

    // Obter valores de stress para todas as sessões de um dado dia
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
        AddSession(20, "08:00", "25/12/2022");
        AddSession(18, "10:00", "25/12/2022");
        AddSession(41, "12:00", "25/12/2022");
        AddSession(92, "14:00", "25/12/2022");
        AddSession(78, "16:00", "25/12/2022");
        AddSession(69, "18:00", "25/12/2022");


        AddSession(32, "08:00", "26/12/2022");
        AddSession(20, "10:00", "26/12/2022");
        AddSession(34, "12:00", "26/12/2022");
        AddSession(80, "14:00", "26/12/2022");
        AddSession(23, "16:00", "26/12/2022");
        AddSession(20, "18:00", "26/12/2022");

        AddSession(36, "08:00", "27/12/2022");
        AddSession(28, "10:00", "27/12/2022");
        AddSession(39, "12:00", "27/12/2022");
        AddSession(70, "14:00", "27/12/2022");
        AddSession(22, "16:00", "27/12/2022");
        AddSession(20, "18:00", "27/12/2022");

        AddSession(30, "08:00", "28/12/2022");
        AddSession(20, "10:00", "28/12/2022");
        AddSession(35, "12:00", "28/12/2022");
        AddSession(45, "14:00", "28/12/2022");
        AddSession(13, "16:00", "28/12/2022");
        AddSession(20, "18:00", "28/12/2022");

        AddSession(56, "08:00", "29/12/2022");
        AddSession(45, "10:00", "29/12/2022");
        AddSession(68, "12:00", "29/12/2022");
        AddSession(53, "14:00", "29/12/2022");
        AddSession(69, "16:00", "29/12/2022");
        AddSession(70, "18:00", "29/12/2022");

        AddSession(34, "08:00", "30/12/2022");
        AddSession(56, "10:00", "30/12/2022");
        AddSession(76, "12:00", "30/12/2022");
        AddSession(79, "14:00", "30/12/2022");
        AddSession(34, "16:00", "30/12/2022");
        AddSession(56, "18:00", "30/12/2022");

        AddSession(87, "08:00", "31/12/2022");
        AddSession(45, "10:00", "31/12/2022");
        AddSession(32, "12:00", "31/12/2022");
        AddSession(13, "14:00", "31/12/2022");
        AddSession(67, "16:00", "31/12/2022");
        AddSession(89, "18:00", "31/12/2022");

        AddSession(90, "08:00", "1/1/2023");
        AddSession(56, "10:00", "1/1/2023");
        AddSession(67, "12:00", "1/1/2023");
        AddSession(45, "14:00", "1/1/2023");
        AddSession(30, "16:00", "1/1/2023");
        AddSession(14, "18:00", "1/1/2023");

        AddSession(87, "08:00", "2/1/2023");
        AddSession(68, "10:00", "2/1/2023");
        AddSession(78, "12:00", "2/1/2023");
        AddSession(90, "14:00", "2/1/2023");
        AddSession(76, "16:00", "2/1/2023");
        AddSession(79, "18:00", "2/1/2023");

        AddSession(15, "08:00", "3/1/2023");
        AddSession(19, "10:00", "3/1/2023");
        AddSession(30, "12:00", "3/1/2023");
        AddSession(35, "14:00", "3/1/2023");
        AddSession(25, "16:00", "3/1/2023");
        AddSession(22, "18:00", "3/1/2023");

        AddSession(34, "08:00", "4/1/2023");
        AddSession(22, "10:00", "4/1/2023");
        AddSession(34, "12:00", "4/1/2023");
        AddSession(22, "14:00", "4/1/2023");
        AddSession(34, "16:00", "4/1/2023");
        AddSession(22, "18:00", "4/1/2023");

        AddSession(45, "08:00", "5/1/2023");
        AddSession(89, "10:00", "5/1/2023");
        AddSession(56, "12:00", "5/1/2023");
        AddSession(49, "14:00", "5/1/2023");
        AddSession(54, "16:00", "5/1/2023");
        AddSession(30, "18:00", "5/1/2023");

        AddSession(59, "08:00", "6/1/2023");
        AddSession(79, "10:00", "6/1/2023");
        AddSession(90, "12:00", "6/1/2023");
        AddSession(89, "14:00", "6/1/2023");
        AddSession(72, "16:00", "6/1/2023");
        AddSession(22, "18:00", "6/1/2023");

        AddSession(67, "08:00", "7/1/2023");
        AddSession(76, "10:00", "7/1/2023");
        AddSession(65, "12:00", "7/1/2023");
        AddSession(48, "14:00", "7/1/2023");
        AddSession(91, "16:00", "7/1/2023");
        AddSession(78, "18:00", "7/1/2023");

        AddSession(34, "08:00", "8/1/2023");
        AddSession(70, "10:00", "8/1/2023");
        AddSession(32, "12:00", "8/1/2023");
        AddSession(36, "14:00", "8/1/2023");
        AddSession(33, "16:00", "8/1/2023");
        AddSession(15, "18:00", "8/1/2023");

        AddSession(89, "08:00", "9/1/2023");
        AddSession(15, "10:00", "9/1/2023");
        AddSession(10, "12:00", "9/1/2023");
        AddSession(12, "14:00", "9/1/2023");
        AddSession(14, "16:00", "9/1/2023");
        AddSession(18, "18:00", "9/1/2023");

        AddSession(89, "08:00", "10/1/2023");
        AddSession(78, "10:00", "10/1/2023");
        AddSession(73, "12:00", "10/1/2023");
        AddSession(81, "14:00", "10/1/2023");
        AddSession(83, "16:00", "10/1/2023");
        AddSession(90, "18:00", "10/1/2023");

        AddSession(15, "08:00", "11/1/2023");
        AddSession(25, "10:00", "11/1/2023");
        AddSession(19, "12:00", "11/1/2023");
        AddSession(29, "14:00", "11/1/2023");
        AddSession(32, "16:00", "11/1/2023");
        AddSession(21, "18:00", "11/1/2023");

        AddSession(67, "08:00", "12/1/2023");
        AddSession(90, "10:00", "12/1/2023");
        AddSession(92, "12:00", "12/1/2023");
        AddSession(89, "14:00", "12/1/2023");
        AddSession(75, "16:00", "12/1/2023");
        AddSession(83, "18:00", "12/1/2023");

        AddSession(78, "08:00", "13/1/2023");
        AddSession(71, "10:00", "13/1/2023");
        AddSession(65, "12:00", "13/1/2023");
        AddSession(80, "14:00", "13/1/2023");
        AddSession(72, "16:00", "13/1/2023");
        AddSession(78, "18:00", "13/1/2023");

        AddSession(15, "08:00", "14/1/2023");
        AddSession(18, "10:00", "14/1/2023");
        AddSession(22, "12:00", "14/1/2023");
        AddSession(34, "14:00", "14/1/2023");
        AddSession(45, "16:00", "14/1/2023");
        AddSession(12, "18:00", "14/1/2023");

        AddSession(10, "08:00", "15/1/2023");
        AddSession(12, "10:00", "15/1/2023");
        AddSession(16, "12:00", "15/1/2023");
        AddSession(18, "14:00", "15/1/2023");
        AddSession(13, "16:00", "15/1/2023");
        AddSession(17, "18:00", "15/1/2023");

    }
}
