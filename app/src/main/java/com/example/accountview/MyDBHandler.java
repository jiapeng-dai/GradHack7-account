package com.example.accountview;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "clientDB.db";
    public static final String TABLE_NAME = "Client";
    public static final String COLUMN_ID = "ClientID";
    public static final String COLUMN_NAME = "ClientName";
    public static final String COLUMN_BALANCE = "ClientBalance";
    //initialize the database
    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " VARCHAR(30)," + COLUMN_BALANCE + " REAL );";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
    public String loadHandler() {
        String result = "";
        String query = "Select * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Float balance = cursor.getFloat(2);
            result += id + " " + name + " " + balance + System.getProperty("line.separator");
        } else {
            return result;
        }
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Float balance = cursor.getFloat(2);
            result += id + " " + name + " " + balance + System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
    public int addHandler(Clients client) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, client.getID());
        values.put(COLUMN_NAME, client.getClientName());
        values.put(COLUMN_BALANCE, client.getBalance());
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int)result;
    }
    public Clients findHandler(String clientname) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = " + "'" +
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
    public boolean updateHandler(int id, String name, Float balance) {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, id);
        args.put(COLUMN_NAME, name);
        args.put(COLUMN_BALANCE, balance);
        result = db.update(TABLE_NAME, args, COLUMN_ID + "=" + id, null) > 0;
        return result;
    }
}
