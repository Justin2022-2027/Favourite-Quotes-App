package com.example.favouritequotesapp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FavoriteQuotes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "quotes";
    private static final String COL_ID = "id";
    private static final String COL_QUOTE = "quote";
    private static final String COL_AUTHOR = "author";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_QUOTE + " TEXT NOT NULL, "
                + COL_AUTHOR + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // CREATE
    public long insertQuote(String quote, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUOTE, quote);
        values.put(COL_AUTHOR, author);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    // READ
    public Cursor getAllQuotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
    }

    // UPDATE
    public int updateQuote(int id, String quote, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUOTE, quote);
        values.put(COL_AUTHOR, author);
        int rows = db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // DELETE
    public int deleteQuote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}
