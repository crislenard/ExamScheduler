package edu.jru.deleon.crislenard.examscheduler;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DbHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Examsched.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "exam_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "CODE";
    public static final String COL_3 = "SUBJECT";
    public static final String COL_4 = "DATE";
    public static final String COL_5 = "TIME";
    public static final String COL_6 = "NOTES";
    public static final String COL_7 = "STATUS";
    public static final String COL_8 = "BEFORETIME";


    Calendar calendar = Calendar.getInstance();
    int day, min,month,year,hours;

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " + COL_3 + " TEXT, " + COL_4 + " TEXT, " + COL_5 + " TEXT, " + COL_6 + " TEXT, "
                + COL_8 + " TEXT, "
                + COL_7 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String code, String subject, String date, String time, String notes, String status, String beforetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, code);
        contentValues.put(COL_3, subject);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, time);
        contentValues.put(COL_6, notes);
        contentValues.put(COL_7, status);
        contentValues.put(COL_8, beforetime);

        //  Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     *
     * @return
     */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDataUpcoming() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME+" WHERE status='Upcoming'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getDataDone() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME+" WHERE status='Done'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     *
     * @param name
     * @return
     */
    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_1 + " FROM " + TABLE_NAME +
                " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the Sched related fields
     *
     *
     *
     *
     */
    public void updateSched(int id,String oldCourseCode,String coursecode, String coursename, String examDate, String examTime, String notes,String beforetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME +" SET "+ COL_2+"='"+coursecode+"' AND "+ COL_3 +"='"+coursename+"' AND "+
                COL_4+"='"+examDate+"' AND "+ COL_5+"='"+examTime+"' AND "+ COL_6+"='"+notes+"' AND "+COL_8+"='"+beforetime+"' WHERE "+
                COL_1+"="+id+" AND "+COL_2+"='"+oldCourseCode+"'";
        ;
        //+ " AND " + COL_2 + " = '" + oldName + "'";
        // Log.d(TAG, "updateName: query: " + query);
        //  Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }

    public boolean updateSched2(int id,String oldCourseCode,String coursecode, String coursename, String examDate, String examTime, String notes,String beforetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String status = "Upcoming";
       ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,coursecode);
        contentValues.put(COL_3,coursename);
        contentValues.put(COL_4,examDate);
        contentValues.put(COL_5,examTime);
        contentValues.put(COL_6,notes);
        contentValues.put(COL_7,status);
        contentValues.put(COL_8,beforetime);
        int result = db.update(TABLE_NAME,contentValues,"ID =?",new String[]{String.valueOf(id)});
        if (result>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Delete from database
     *
     *
     */
    public void deleteSched(int id, String coursecode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL_1 + " = '" + id + "'" +
                " AND " + COL_2 + " = '" + coursecode + "'";
        //    Log.d(TAG, "deleteName: query: " + query);
        //    Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

    public int countUpcoming() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select Count(*) from " + TABLE_NAME + " where status = 'Upcoming'", null);
        int x = 0;
        if (cursor.moveToFirst()) {
            x = cursor.getInt(0);
        }
        cursor.close();
        return x;
    }

    public int countDone() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select Count(*) from " + TABLE_NAME + " where status = 'Done'", null);
        int x = 0;
        if (cursor.moveToFirst()) {
            x = cursor.getInt(0);
        }
        cursor.close();
        return x;
    }



    public List<String> getDataListView() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst())
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        return list;

    }

    public Cursor getAllData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateStatus(String coursecode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_7 +
                " = 'Done' WHERE " + COL_2 + " = '" + coursecode + "'" +
                " AND " + COL_7 + " = 'Upcoming'";
        // Log.d(TAG, "updateName: query: " + query);
        //  Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }
    public void updateStatusDialog(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_7 +
                " = 'Done' WHERE " + COL_1 + " = " + id +
                " AND " + COL_7 + " = 'Upcoming'";
        // Log.d(TAG, "updateName: query: " + query);
        //  Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }


  /*  public Cursor getInfo(SQLiteDatabase db){

        String [] columns = {COL_2,COL_2,COL_3,COL_4,COL_5,COL_6};
        Cursor cursor = db.query(TABLE_NAME,columns,null,null,null,null,null);

        return cursor;
    }

    public Cursor getInfos(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = this.getInfo(db);

        String coursecode,coursename,date,notes,status;

        while(cursor.moveToNext()){

            coursecode = cursor.getString(cursor.getColumnIndex(COL_2));
            coursename = cursor.getString(cursor.getColumnIndex(COL_3));
            date = cursor.getString(cursor.getColumnIndex(COL_4));
            notes = cursor.getString(cursor.getColumnIndex(COL_5));
            status = cursor.getString(cursor.getColumnIndex(COL_6));

            ExamSchedule examSchedule = new ExamSchedule(coursecode,coursename,date,notes,status);

        }


        return cursor;
    }

     public Cursor notifExam(){

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        String yearsystem,hoursystem;
        yearsystem= day+"/"+month+"/"+year;
        hoursystem=hours+":"+min;

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = fmt.parse(hoursystem);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat frmtOut = new SimpleDateFormat("hh:mm a");
        String formattedTime = frmtOut.format(date);

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME+" WHERE "+COL_4+"='"+yearsystem+"' AND "+COL_5+"='"+formattedTime+"'", null);


        return cursor;
    }*/


}
