package com.example.accountview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TranDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "clientDB.db";
    public static final String TABLE_NAME = "CTransact";
    public static final String COLUMN_ID = "TransacID";
    public static final String COLUMN_ACT = "Activity";
    public static final String COLUMN_AMOUNT = "Amount";
    public static final int ROW_LIMIT = 10;

    //initialize the database
    public TranDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_ACT + " VARCHAR(10)," + COLUMN_AMOUNT + " REAL );";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public ArrayList<String> loadHandler() {
        ArrayList<String> result = new ArrayList();
        String query = "Select * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String act = cursor.getString(1);
            Float amount = cursor.getFloat(2);
            //result += id + " " + name + " " + balance + System.getProperty("line.separator");
            result.add(id + " " + act + " " + amount);
        } else {
            return result;
        }
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String act = cursor.getString(1);
            Float amount = cursor.getFloat(2);
            //result += id + " " + name + " " + balance + System.getProperty("line.separator");
            result.add(id + " " + act + " " + "$" + amount);
        }
        cursor.close();
        db.close();
        return result;
    }

    public int addHandler(int id, String act, float amount) {
        ArrayList<String> rows = loadHandler();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_ACT, act);
        values.put(COLUMN_AMOUNT, amount);
        SQLiteDatabase db = this.getWritableDatabase();
        if (rows.size() >= ROW_LIMIT) {
            String sid = rows.get(0).substring(0,1);
            db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{sid});
        }
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int) result;
    }
    /*
    public Clients findHandler(String clientname) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ACT + " = " + "'" +
                clientname + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Clients client = new Clients();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            client.setID(Integer.parseInt(cursor.getString(0)));
            client.setClientName(cursor.getString(1));
            client.setBalance(Float.parseFloat(cursor.getString(2)));
        } else {
            client = null;
        }
        cursor.close();
        db.close();
        return client;
    }
     */

    public boolean deleteHandler(int id) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME + " WHERE" + COLUMN_ID + " = " + "'" +
                id + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{
                    String.valueOf(id)});
            cursor.close();
            result = true;
        }
        return result;
    }
/*
    public boolean updateHandler(int id, String name, Float balance) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, id);
        args.put(COLUMN_ACT, name);
        args.put(COLUMN_AMOUNT, balance);
        result = db.update(TABLE_NAME, args, COLUMN_ID + "=" + id, null) > 0;
        return result;
    }
 */
}
