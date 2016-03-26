package com.ms.todo122;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {


    private static final String TAG = "DBAdapter"; //used for logging database version changes

    // Field Names:
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_STATUS = "status";

    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_TASK, KEY_STATUS};

    // Column Numbers for each Field Name:
    public static final int COL_ROWID = 0;
    public static final int COL_TASK = 1;
    public static final int COL_STATUS = 2;

    // DataBase info:
    public static final String DATABASE_NAME = "dbToDo";
    public static final String DATABASE_TABLE = "mainToDo";
    public static final int DATABASE_VERSION = 3;

    //SQL statement to create database
    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TASK + " TEXT NOT NULL, "
                    + KEY_STATUS + " INTEGER "
                    + ");";


    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }


    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }


    // Add a new set of values to be inserted into the database.
    public long insertRow(String task) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TASK, task);
        initialValues.put(KEY_STATUS, 0);
        // Insert the data into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + " = " + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public boolean deleteCompleted() {
        int set = 1;
        String where = KEY_STATUS + " = " + set;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }


    // Return all data in the database.
    public Cursor getAllRows() {
        int set = 0;
        String where = KEY_STATUS + "= " + set;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public long getProfilesCount() {
        long cnt = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);
        db.close();
        return cnt;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String task) {
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TASK, task);
        newValues.put(KEY_STATUS, 0);
        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    //for completed button task
    public Cursor completed() {
        int set = 1;
        String selectQuery = KEY_STATUS + " = " + set;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, selectQuery, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    //set status to 1
    public boolean setStatus(long rowId, int set) {
        set = 1;
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValue = new ContentValues();
        newValue.put(KEY_STATUS, set);
        return db.update(DATABASE_TABLE, newValue, where, null) != 0;
    }

    public int getToDoCount() {
        String countQuery = "SELECT  * FROM " + DATABASE_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);


        }
    }


}