package edu.usc.UscAR.pref;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PreferenceManager {
    public static final String KEY_USC_AR_DEFAULT_INIT = "usc_ar_default_init";

    private static PreferenceManager mInstance;

    private Context mContext;
    private ContentResolver mContentResolver;

    private PreferenceManager(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public static PreferenceManager getPreferenceManager(Context context) {
        if ((mInstance == null) || !context.equals(mInstance.mContext)) {
            mInstance = new PreferenceManager(context);
        }
        return mInstance;
    }

    public void putValue(String key, int value) {
        ContentValues values = new ContentValues(1);
        if (key == KEY_USC_AR_DEFAULT_INIT) {
            values.put(PreferenceConstants.PreferenceField.DEFAULT_INIT, value);
        }
        mContentResolver.update(PreferenceConstants.PreferenceField.CONTENT_URI, values, null, null);
    }

    public int getValue(String key, int value) {
        String field = null;
        if (key == KEY_USC_AR_DEFAULT_INIT) {
            field = PreferenceConstants.PreferenceField.DEFAULT_INIT;
        }
        int retVal = value;
        Cursor cursor = mContentResolver.query(PreferenceConstants.PreferenceField.CONTENT_URI, new String[]{field}, null, null, null);

        if (cursor == null) {
            return retVal;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return retVal;
        }

        cursor.moveToNext();
        retVal = cursor.getInt(cursor.getColumnIndexOrThrow(field));

        if (cursor != null) {
            cursor.close();
        }
        return retVal;
    }
}
