package com.maple.beautyjournal.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.maple.beautyjournal.provider.Beauty.Brand;
import com.maple.beautyjournal.provider.Beauty.Category;
import com.maple.beautyjournal.provider.Beauty.Function;
import com.maple.beautyjournal.provider.Beauty.Area;

/**
 * Created by tian on 13-7-4.
 */
public class BeautyProvider extends ContentProvider {
    private static final String TAG = "BeautyProvider";
    private static final UriMatcher sMatcher;
    private static final int BRANDS = 0;
    private static final int BRAND_ID = 1;
    private static final int CATEGORIES = 100;
    private static final int CATEGORY_ID = 101;
    private static final int CATEGORY_BASE_CATEGORIES = 102;
    private static final int FUNCTIONS = 200;
    private static final int FUNCTION_ID = 201;
    private static final int AREAS = 300;
    private static final int AREA_ID = 301;
    private static final int DISTINCT_PROVINCE = 302;
    private static final int DISTINCT_CITY = 303;

    private DatabaseHelper mHelper;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(Beauty.AUTHORITY, "brand/", BRANDS);
        sMatcher.addURI(Beauty.AUTHORITY, "brand/#", BRAND_ID);
        sMatcher.addURI(Beauty.AUTHORITY, "category", CATEGORIES);
        sMatcher.addURI(Beauty.AUTHORITY, "category/#", CATEGORY_ID);
        sMatcher.addURI(Beauty.AUTHORITY, "category_base", CATEGORY_BASE_CATEGORIES);
        sMatcher.addURI(Beauty.AUTHORITY, "function", FUNCTIONS);
        sMatcher.addURI(Beauty.AUTHORITY, "function/#", FUNCTION_ID);
        sMatcher.addURI(Beauty.AUTHORITY, "area", AREAS);
        sMatcher.addURI(Beauty.AUTHORITY, "area/#", AREA_ID);
        sMatcher.addURI(Beauty.AUTHORITY, "area-distinct-province", DISTINCT_PROVINCE);
        sMatcher.addURI(Beauty.AUTHORITY, "area-distinct-city", DISTINCT_CITY);
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int i = sMatcher.match(uri);
        Log.d(TAG, "query " + i);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;
        String defaultSortOrder = null;
        String groupBy = null;
        switch (i) {
            case BRANDS:
                qb.setTables(Brand.TABLE_NAME);
                defaultSortOrder = Brand.BRAND_ID + " ASC ";
                break;
            case BRAND_ID:
                qb.setTables(Brand.TABLE_NAME);
                qb.appendWhere("_id=" + uri.getPathSegments().get(0));
                break;
            case CATEGORIES:
                qb.setTables(Category.TABLE_NAME);
                defaultSortOrder = Category._ID + " ASC ";
                break;
            case CATEGORY_ID:
                qb.setTables(Category.TABLE_NAME);
                qb.appendWhere("_id=" + uri.getPathSegments().get(0));
                break;
            case CATEGORY_BASE_CATEGORIES:
                qb.setTables(Category.TABLE_NAME);
                qb.setDistinct(true);
                defaultSortOrder = Category.CATEGORY_ID + " ASC ";
                groupBy = Category.CATEGORY_ID;
                break;
            case FUNCTIONS:
                qb.setTables(Function.TABLE_NAME);
                defaultSortOrder = Function._ID + " ASC ";
                groupBy = Function.FUNCTION_ID;
                break;
            case FUNCTION_ID:
                qb.setTables(Function.TABLE_NAME);
                qb.appendWhere("_id=" + uri.getPathSegments().get(0));
                break;
            case AREAS:
                qb.setTables(Area.TABLE_NAME);
                defaultSortOrder = Area._ID + " ASC ";
                break;
            case AREA_ID:
                qb.setTables(Area.TABLE_NAME);
                qb.appendWhere("_id=" + uri.getPathSegments().get(0));
                break;
            case DISTINCT_PROVINCE:
                return db.rawQuery("SELECT DISTINCT " + Area.PROVINCE + " FROM " + Area.TABLE_NAME + " ORDER BY " +
                                           Area._ID, null);
            case DISTINCT_CITY:
                return db
                        .rawQuery("SELECT DISTINCT " + Area.CITY + " FROM " + Area.TABLE_NAME + " WHERE " + Area
                                .PROVINCE + "=\'" + selectionArgs[0] + "\' ORDER BY " + Area._ID, null);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = defaultSortOrder;
        } else {
            orderBy = sortOrder;
        }
        return qb.query(db, projection, selection, selectionArgs, groupBy, null, orderBy);
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case BRANDS:
                return "vnd.android.cursor.dir/vnd.maple.beauty.brand";
            case BRAND_ID:
                return "vnd.android.cursor.item/vnd.maple.beauty.brand";
            case CATEGORIES:
                return "vnd.android.cursor.dir/vnd.maple.beauty.category";
            case CATEGORY_ID:
                return "vnd.android.cursor.item/vnd.maple.beauty.category";
            case FUNCTIONS:
                return "vnd.android.cursor.dir/vnd.maple.beauty.function";
            case FUNCTION_ID:
                return "vnd.android.cursor.item/vnd.maple.beauty.function";
            case AREAS:
                return "vnd.android.cursor.dir/vnd.maple.beauty.area";
            case AREA_ID:
                return "vnd.android.cursor.item/vnd.maple.beauty.area";
            case DISTINCT_CITY:
                return "vnd.android.cursor.item/vnd.maple.beauty.distinct-city";
            case DISTINCT_PROVINCE:
                return "vnd.android.cursor.item/vnd.maple.beauty.distinct-province";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long rowId;
        Uri res = null;
        int i = sMatcher.match(uri);
        Log.d(TAG, "insert matched " + i);
        switch (i) {
            case BRANDS:
                rowId = db.insert(Brand.TABLE_NAME, null, contentValues);
                res = ContentUris.withAppendedId(Brand.CONTENT_URI, rowId);
                break;
            case CATEGORIES:
                rowId = db.insert(Category.TABLE_NAME, null, contentValues);
                res = ContentUris.withAppendedId(Category.CONTENT_URI, rowId);
                break;
            case FUNCTIONS:
                rowId = db.insert(Function.TABLE_NAME, null, contentValues);
                res = ContentUris.withAppendedId(Function.CONTENT_URI, rowId);
                break;
            case AREAS:
                rowId = db.insert(Area.TABLE_NAME, null, contentValues);
                res = ContentUris.withAppendedId(Area.CONTENT_URI, rowId);
                break;
        }
        return res;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Delete is not supported!");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("update is not supported!");
    }
}
