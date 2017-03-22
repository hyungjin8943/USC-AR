package edu.usc.UscAR.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import edu.usc.UscAR.BeyondarExamples;
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

    public Uri insertUscARFromDefault(CustomGeoObject obj) {
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

    public void insertUscARFromExtension(CustomGeoObject obj) {
        boolean isNewData = false;
        Uri arUri = null;
        ContentValues values = new ContentValues(8);
        Cursor cursor = mContentResolver.query(UscARConstant.UscARField.CONTENT_URI, new String[]{UscARConstant.UscARField._ID},
                getSelection(obj.getmId()), null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                arUri = ContentUris.withAppendedId(UscARConstant.UscARField.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(UscARConstant.UscARField._ID)));
            } else {
                isNewData = true;
            }
        } else {
            isNewData = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        values.put(UscARConstant.UscARField.AR_ID, obj.getmId());
        values.put(UscARConstant.UscARField.CODE, obj.getCode());
        values.put(UscARConstant.UscARField.NAME, obj.getmName());
        values.put(UscARConstant.UscARField.SHORT, obj.getDescription());
        values.put(UscARConstant.UscARField.LATITUDE, String.valueOf(obj.getLatitude()));
        values.put(UscARConstant.UscARField.LONGITUDE, String.valueOf(obj.getLongitude()));
        values.put(UscARConstant.UscARField.URL, obj.getUrl());
        values.put(UscARConstant.UscARField.ADDRESS, obj.getAddress());

        if (isNewData) {
            arUri = mContentResolver.insert(UscARConstant.UscARField.CONTENT_URI, values);
        } else {
            mContentResolver.update(arUri, values, null, null);
        }

        if (arUri != null) {
            Log.i(TAG, "photo = " + obj.getPhoto());
            saveResBitmap(arUri, getBitmap(BeyondarExamples.ARImagePath + obj.getPhoto()));
        }
    }

    public void saveResBitmap(Uri uri, Bitmap bitmap) {
        OutputStream outputStream = null;
        try {
            outputStream = mContentResolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getARUri(String ar_id) {
        Uri retVal = null;
        Cursor cursor = mContentResolver.query(UscARConstant.UscARField.CONTENT_URI, new String[]{UscARConstant.UscARField._ID}, getSelection(ar_id), null, null);

        if (cursor == null) {
            return retVal;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return retVal;
        }

        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(UscARConstant.UscARField._ID));
        retVal = ContentUris.withAppendedId(UscARConstant.UscARField.CONTENT_URI, id);
        if (cursor != null) {
            cursor.close();
        }
        return retVal;
    }

    private String getSelection(String ar_id) {
        StringBuilder sb = new StringBuilder();
        sb.append(UscARConstant.UscARField.AR_ID);
        sb.append(" = ");
        sb.append(Integer.valueOf(ar_id));
        return sb.toString();
    }

    public String getPhoto(String ar_id) {
        String retVal = null;
        Cursor cursor = mContentResolver.query(UscARConstant.UscARField.CONTENT_URI, new String[]{UscARConstant.UscARField.DATA}, getSelection(ar_id), null, null);

        if (cursor == null) {
            return retVal;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return retVal;
        }

        cursor.moveToNext();
        retVal = cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.DATA));
        if (cursor != null) {
            cursor.close();
        }
        return retVal;
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        File f = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
