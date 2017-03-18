package edu.usc.UscAR.pref;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PreferenceProvider extends ContentProvider {

    private final static String TAG = "PreferenceProvider";

    private SQLiteOpenHelper mOpenHelper;

    private final static String VND_ANDROID_DIR_USCAR_PREF = "vnd.android-dir/uscar_pref";

    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int USC_AR_PREF = 0;

    static {
        sURLMatcher.addURI("uscar_pref", "pref", USC_AR_PREF);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = PreferenceDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int match = sURLMatcher.match(uri);

        switch (match) {
            case USC_AR_PREF:
                qb.setTables(PreferenceConstants.PreferenceField.PREFERENCE_TABLE);
                break;
            default:
                return null;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret;
        ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);

        String extraSelection = null;
        String table = null;
        switch (match) {
            case USC_AR_PREF:
                table = PreferenceConstants.PreferenceField.PREFERENCE_TABLE;
                break;

            default:
                return 0;
        }

        ContentValues finalValues;
        finalValues = new ContentValues(values);

        int updatedRows = 0;
        String finalSelection = concatSelections(selection, extraSelection);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            updatedRows = db.update(table, finalValues, finalSelection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (Throwable ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            db.endTransaction();
        }

        if (updatedRows > 0) {
            notifyChange(uri);
        }
        return updatedRows;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new IllegalStateException("insert unsupport!!!");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new IllegalStateException("delete unsupport!!!");
    }

    @Override
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);

        switch (match) {
            case USC_AR_PREF:
                return VND_ANDROID_DIR_USCAR_PREF;
            default:
                return "*/*";
        }
    }

    private static String concatSelections(String selection1, String selection2) {
        if (TextUtils.isEmpty(selection1)) {
            return selection2;
        } else if (TextUtils.isEmpty(selection2)) {
            return selection1;
        } else {
            return selection1 + " AND " + selection2;
        }
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(PreferenceConstants.PreferenceField.CONTENT_URI, null);
    }
}
