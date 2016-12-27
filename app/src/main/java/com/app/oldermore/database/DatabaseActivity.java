package com.app.oldermore.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("Instantiatable")
public class DatabaseActivity extends SQLiteOpenHelper {
    private static String DB_NAME = "oldermore_mdb.sqlite";
    private static String TABLE_USER = "user";
    private static Integer BUFFER_SIZE = 128;
    private SQLiteDatabase myDataBase;
    private final Context context;
    private DecimalFormat df = new DecimalFormat("#,###,###.##");
    String message = "";

    public DatabaseActivity(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    public void openDatabase() {
        File dbFolder = context.getDatabasePath(DB_NAME).getParentFile();
        if (!dbFolder.exists()) {
            dbFolder.mkdir();
        }

        File dbFile = context.getDatabasePath(DB_NAME);

        if (!dbFile.exists()) {
            try {
                copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
    }

    private void copyDatabase(File targetDbFile) throws IOException {
        // ***********************************************************************************
        // Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(targetDbFile);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        // ***********************************************************************************
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InsertLogin(String user_id, String username, String password, String user_image, String type) {
        SQLiteDatabase db;
        try {
            db = this.getReadableDatabase(); // Read Data
            String strSQL = "SELECT * FROM " + TABLE_USER + " ";
            Cursor cursor = db.rawQuery(strSQL, null);
            Integer count = cursor.getCount();
            cursor.close();
            db = this.getWritableDatabase(); // Update or Insert to database
            if (count > 0) {
                String strSQLInsert = "DELETE FROM " + TABLE_USER + " ";
                db.execSQL(strSQLInsert);
            }
            String strSQLInsert = "INSERT INTO " + TABLE_USER
                    + "(user_id, username, password, user_image, type) VALUES(" + user_id + ", '" + username + "', '" + password
                    + "', '" + user_image + "', '" + type + "') ";
            db.execSQL(strSQLInsert);
            db.close();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void DeleleLogin() {
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase(); // Update Delete or Insert to database
            String strSQLInsert = "DELETE FROM " + TABLE_USER + " ";
            db.execSQL(strSQLInsert);
            db.close();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public ArrayList<HashMap<String, String>> CheckLogin() {
        try {
            ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data
            String strSQL = "SELECT user_id, username, password, user_image, type FROM "
                    + TABLE_USER + " LIMIT 1 ";
            Cursor cursor = db.rawQuery(strSQL, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        map = new HashMap<String, String>();
                        map.put("user_id", cursor.getString(0));
                        map.put("username", cursor.getString(1));
                        map.put("password", cursor.getString(2));
                        map.put("user_image", cursor.getString(3));
                        map.put("type", cursor.getString(4));
                        MyArrList.add(map);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            db.close();
            return MyArrList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
