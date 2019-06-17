package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KesriTripDB extends SQLiteOpenHelper {

    public KesriTripDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "kesritripsqlitedb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usertripdetails(utno INTEGER PRIMARY KEY AUTOINCREMENT, dbtid text, srclocname text, srclat text, srclon text, haltlocname text, haltlat text, haltlon text,destlocname text, destlat text, destlon text, startdate text, breakfasttime text, lunchtime text, dinnertime text, hotelrating text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            return;
        }
        db.execSQL("DROP TABLE IF EXISTS usertripdetails");
        onCreate(db);
    }

    protected void storetripdetails(String dbtid, String srclocname, String srclat, String srclon, String haltlocname, String haltlat, String haltlon, String destlocname, String destlat, String destlon, String startdate, String breakfasttime, String lunchtime, String dinnertime, String hotelrating) {
        deleteTripDetails();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dbtid", dbtid);
        contentValues.put("srclocname", srclocname);
        contentValues.put("srclat", srclat);
        contentValues.put("srclon", srclon);
        contentValues.put("haltlocname", haltlocname);
        contentValues.put("haltlat", haltlat);
        contentValues.put("haltlon", haltlon);
        contentValues.put("destlocname", destlocname);
        contentValues.put("destlat", destlat);
        contentValues.put("destlon", destlon);
        contentValues.put("startdate", startdate);
        contentValues.put("breakfasttime", breakfasttime);
        contentValues.put("lunchtime", lunchtime);
        contentValues.put("dinnertime", dinnertime);
        contentValues.put("hotelrating", hotelrating);

        Cursor cur = db.rawQuery("SELECT * FROM usertripdetails WHERE dbtid=?", new String[]{dbtid});
        if (cur.moveToNext()) {

        } else {
            long id = db.insert("usertripdetails", null, contentValues);
        }
    }

    protected void deleteTripDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("usertripdetails", null, null);
    }

    protected Cursor getTripDetails(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM usertripdetails",null);
        return cur;
    }
}