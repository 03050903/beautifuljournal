package com.maple.beautyjournal.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.maple.beautyjournal.provider.Beauty.Brand;
import com.maple.beautyjournal.provider.Beauty.Category;
import com.maple.beautyjournal.provider.Beauty.Function;
import com.maple.beautyjournal.provider.Beauty.Area;

/**
 * Created by tian on 13-7-4.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "beauty.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s1 = new String("CREATE TABLE IF NOT EXISTS " + Brand.TABLE_NAME + "("
            + Brand._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Brand.NAME + " TEXT NOT NULL, "
            + Brand.ENGLISH_NAME + " TEXT, "
            + Brand.FIRST_CHAR + " TEXT NOT NULL, "
            + Brand.FIRST_ENGLISH_CHAR + " TEXT, "
            + Brand.DESCRIPTION + " TEXT, "
            + Brand.BRAND_ID + " TEXT NOT NULL, "
            + Brand.FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
            + Brand.NOTES + " TEXT, "
            + Brand.PINYIN + " TEXT, "
            + Brand.PICTURE + " BLOB, "
            + Brand.DATA1 + " TEXT, "
            + Brand.DATA2 + " TEXT, "
            + Brand.DATA3 + " TEXT, "
            + Brand.DATA4 + " TEXT); ");
        Log.d(DB_NAME, s1);
        db.execSQL(s1);
        String s2= new String("CREATE TABLE IF NOT EXISTS " + Category.TABLE_NAME + "("
            + Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Category.NAME + " TEXT NOT NULL, "
            + Category.CATEGORY_ID + " TEXT NOT NULL, "
            + Category.SUB_CATEGORY + " TEXT NOT NULL, "
            + Category.SUB_CATEGORY_ID + " TEXT NOT NULL, "
            + Category.SUB_SUB_CATEGORY + " TEXT NOT NULL, "
            + Category.SUB_SUB_CATEGORY_ID + " TEXT NOT NULL, "
            + Category.NOTES + " TEXT, "
            + Category.FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
            + Category.PICTURE + " BLOB, "
            + Category.DATA1 + " TEXT, "
            + Category.DATA2 + " TEXT, "
            + Category.DATA3 + " TEXT, "
            + Category.DATA4 + " TEXT);");
        Log.d(DB_NAME, s2);
        db.execSQL(s2);
        String s3 = new String("CREATE TABLE IF NOT EXISTS " + Function.TABLE_NAME + "(" + Function._ID + " INTEGER PRIMARY KEY " +
                           "AUTOINCREMENT, " + Function.NAME + " TEXT NOT NULL, " + Function.FUNCTION_ID + " TEXT NOT" +
                           " NULL, " + Function.SUB_FUNCTION + " TEXT NOT NULL, " + Function.SUB_FUNCTION_ID + " TEXT" +
                           " NOT NULL, " + Function.FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
                           "" + Function.NOTES + " TEXT, " + Function.PICTURE + " BLOB, " + Function.DATA1 + " TEXT, " +
                           "" + Function.DATA2 + " TEXT, " + Function.DATA3 + " TEXT, " + Function.DATA4 + " TEXT);");
        Log.d(DB_NAME, s3);
        db.execSQL(s3);

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Area.TABLE_NAME + "("
                   + Area._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + Area.PROVINCE + " TEXT NOT NULL, "
                   + Area.CITY + " TEXT NOT NULL, "
                   + Area.DISTRICT + " TEXT NOT NULL, "
                   + Area.PAY_AT_ARRIVAL + " INTEGER NOT NULL DEFAULT 0, "
                   + Area.POS + " INTEGER NOT NULL DEFAULT 0"
                   + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO implement upgrade function
    }
}
