package edu.usc.UscAR.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UscARDatabaseHelper extends SQLiteOpenHelper {

    private static UscARDatabaseHelper sInstance = null;
    private static final String DATABASE_NAME = "uscar.db";
    private static final int DATABASE_VERSION = 1;

    private UscARDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized UscARDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UscARDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUSCARTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        drop(db);
        onCreate(db);
    }

    private void createUSCARTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UscARConstant.UscARField.USC_AR_TABLE + " ( "
                + UscARConstant.UscARField._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UscARConstant.UscARField.AR_ID + " INTEGER, "
                + UscARConstant.UscARField.CODE + " TEXT, "
                + UscARConstant.UscARField.NAME + " TEXT, "
                + UscARConstant.UscARField.SHORT + " TEXT, "
                + UscARConstant.UscARField.LATITUDE + " TEXT, "
                + UscARConstant.UscARField.LONGITUDE + " TEXT, "
                + UscARConstant.UscARField.DATA + " TEXT, "
                + UscARConstant.UscARField.URL + " TEXT, "
                + UscARConstant.UscARField.ADDRESS + " TEXT " + " ) ");
    }

    private void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "
                + UscARConstant.UscARField.USC_AR_TABLE);
    }
}