package edu.usc.UscAR.pref;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class PreferenceDatabaseHelper extends SQLiteOpenHelper {

    private static PreferenceDatabaseHelper sInstance = null;
    private static final String DATABASE_NAME = "uscar_pref.db";
    private static final int DATABASE_VERSION = 1;

    private String mSql = "INSERT INTO pref (_id, default_init) " +
            "VALUES(?,?)";

    private PreferenceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized PreferenceDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPrefTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        drop(db);
        onCreate(db);
    }

    private void createPrefTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PreferenceConstants.PreferenceField.PREFERENCE_TABLE + " ( "
                + PreferenceConstants.PreferenceField._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PreferenceConstants.PreferenceField.DEFAULT_INIT + " INTEGER " + " ) ");

        insertDefaultValue(db);
    }

    private void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "
                + PreferenceConstants.PreferenceField.PREFERENCE_TABLE);
    }

    private void insertDefaultValue(SQLiteDatabase db) {
        SQLiteStatement insertStatnment = db.compileStatement(mSql);
        insertStatnment.clearBindings();
        insertStatnment.bindString(1, Integer.toString(1));
        insertStatnment.bindString(2, Integer.toString(0));
        insertStatnment.executeInsert();
    }
}