package edu.usc.UscAR.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.usc.UscAR.custom.CustomGeoObject;

public class UscARPersister {
    private static final String TAG = "UscARPersister";

    private static UscARPersister sPersister;

    private Context mContext;
    private ContentResolver mContentResolver;

    private UscARPersister(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    /**
     * Get or create if not exist an instance of UscARPersister
     */
    public static UscARPersister getUscARPersister(Context context) {
        if ((sPersister == null) || !context.equals(sPersister.mContext)) {
            sPersister = new UscARPersister(context);
        }

        return sPersister;
    }

    public Uri insertUscAR(CustomGeoObject obj) {
        ContentValues values = new ContentValues(8);
        values.put(UscARConstant.UscARField.AR_ID, obj.getmId());
        values.put(UscARConstant.UscARField.CODE, obj.getCode());
        values.put(UscARConstant.UscARField.NAME, obj.getmName());
        values.put(UscARConstant.UscARField.SHORT, obj.getDescription());
        values.put(UscARConstant.UscARField.LATITUDE, String.valueOf(obj.getLatitude()));
        values.put(UscARConstant.UscARField.LONGITUDE, String.valueOf(obj.getLongitude()));
        values.put(UscARConstant.UscARField.URL, obj.getUrl());
        values.put(UscARConstant.UscARField.ADDRESS, obj.getAddress());
        return mContentResolver.insert(UscARConstant.UscARField.CONTENT_URI, values);
    }
}
