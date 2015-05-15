/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cn.daming.deskclock;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AlarmProvider extends ContentProvider {
    private static SQLiteOpenHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    public static String wokingtable = "alarms" ;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("com.cn.daming.deskclock", "alarm", ALARMS);
        sURLMatcher.addURI("com.cn.daming.deskclock", "alarm/#", ALARMS_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "alarms.db";
        private static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");
            db.execSQL("CREATE TABLE "+wokingtable+" (" +
                       "_id INTEGER PRIMARY KEY," +
                       "hour INTEGER, " +
                       "minutes INTEGER, " +
                       "daysofweek INTEGER, " +
                       "alarmtime INTEGER, " +
                       "enabled INTEGER, " +
                       "vibrate INTEGER, " +
                       "message TEXT, " +
                       "flag INTEGER, " +
                       "socket INTEGER, " +
                       
            		"alert TEXT);");

            // insert default alarms
//            String insertMe = "INSERT INTO alarms " +
//                    "(hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, flag, socket,alert) " +
//                    "VALUES ";
//            db.execSQL(insertMe + "(8, 30, 31, 0, 0, 1, '风扇', 0,0,'');");
//            db.execSQL(insertMe + "(9, 00, 96, 0, 0, 1, '电灯', 0,1,'');");
            
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            if (true) Log.v("wangxianming",
                    "Upgrading "+wokingtable+" database from version " +
                    oldVersion + " to " + currentVersion +
                    ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+wokingtable);
            onCreate(db);
        }
    }

    public AlarmProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                qb.setTables(wokingtable);
                break;
            case ALARMS_ID:
                qb.setTables(wokingtable);
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs,
                              null, null, sort);

        if (ret == null) {
            if (true) Log.v("wangxianming", "Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public String getType(Uri url) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");

        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/"+wokingtable;
            case ALARMS_ID:
                return "vnd.android.cursor.item/"+wokingtable;
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");

        int count;
        long rowId = 0;
        int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID: {
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update(wokingtable, values, "_id=" + rowId, null);
                break;
            }
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + url);
            }
        }
        Log.v("wangxianming", "*** notifyChange() rowId: " + rowId + " url " + url);
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");

        if (sURLMatcher.match(url) != ALARMS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + url);
        }

        ContentValues values = new ContentValues(initialValues);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(wokingtable, Alarm.Columns.MESSAGE, values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + url);
        }
        Log.v("wangxianming", "Added alarm rowId = " + rowId);

        Uri newUrl = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    public int delete(Uri url, String where, String[] whereArgs) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        long rowId = 0;
        switch (sURLMatcher.match(url)) {
            case ALARMS:
                count = db.delete(wokingtable, where, whereArgs);
                break;
            case ALARMS_ID:
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete(wokingtable, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }
    public static void copytable(String tablename) {
    	wokingtable = PreferencesUtils.getString("wokingtable", "alarms");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE "+tablename+" (" +
                "_id INTEGER PRIMARY KEY," +
                "hour INTEGER, " +
                "minutes INTEGER, " +
                "daysofweek INTEGER, " +
                "alarmtime INTEGER, " +
                "enabled INTEGER, " +
                "vibrate INTEGER, " +
                "message TEXT, " +
                "flag INTEGER, " +
                "socket INTEGER, " +   
     		"alert TEXT)");//+" AS SELECT * FROM "+wokingtable
        db.execSQL("insert into "+tablename+" select * from "+wokingtable);
        
        
    }
    public static void deltetable(String tablename) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.execSQL("DROP TABLE "+tablename);
    }
    public static List<String> getAlltable() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//         db.q("SELECT * FROM sqlite_master WHERE type='table'");
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);  
        List<String > list = new ArrayList<String>();
        while(cursor.moveToNext()){
        	
        	String name = cursor.getString(cursor.getColumnIndex("name"));
        	if (name.equals("android_metadata")) {
				continue;
			}
        	list.add(name);
        	System.out.println("name "+name);
        }
        return list ;
    }

}
