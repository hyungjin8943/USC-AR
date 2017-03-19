package edu.usc.UscAR.custom;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.usc.UscAR.db.UscARConstant;
import edu.usc.UscAR.db.UscARPersister;

/**
 * Created by Youngmin on 2016. 6. 1..
 */
public class USCMapJSONLocal {

    public static ArrayList<CustomGeoObject> buildingArray = new ArrayList<CustomGeoObject>();

    public String readFromFile(Context context) {

        String ret = "";

        try {

            AssetManager assetManager = context.getAssets();

            InputStream inputStream = assetManager.open("mapJson.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Log.e("aaa", "Json: " + ret);


            }
        } catch (FileNotFoundException e) {
            Log.e("aaa", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("aaa", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void jsonConverter(Context context, String jsonStr) {
        try {

            JSONArray jsonArr = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArr.length(); i++) {

                JSONObject jsonObj = jsonArr.getJSONObject(i);
                CustomGeoObject poi = new CustomGeoObject();

                // Log.e("jsonArr", jsonObj.getString("code")); // short name
//                jsonObj.getString("id"); // id number
//                jsonObj.getString("name"); // full name
//                jsonObj.getString("latitude");
//                jsonObj.getString("longitude");
//                jsonObj.getString("photo");
//                jsonObj.getString("url");
//                jsonObj.getString("address");

                poi.setmId(jsonObj.getString("id"));
                poi.setCode(jsonObj.getString("code"));
                poi.setmName(jsonObj.getString("name"));
                poi.setLatitude(jsonObj.getDouble("latitude"));
                poi.setLongitude(jsonObj.getDouble("longitude"));
                poi.setPhoto(jsonObj.getString("photo"));
                poi.setUrl(jsonObj.getString("url"));
                poi.setAddress(jsonObj.getString("address"));
                poi.setDescription(jsonObj.getString("short"));

                UscARPersister.getUscARPersister(context.getApplicationContext()).insertUscAR(poi);

                buildingArray.add(i, poi);
            }
            // for(int i=0; i<buildingArray.size(); i++)
            //    Log.e("json",buildingArray.get(i).getCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void databseConverter(Context context) {
        Cursor cursor = context.getContentResolver().query(UscARConstant.UscARField
                .CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }
        int index = 0;
        while (cursor.moveToNext()) {
            CustomGeoObject poi = new CustomGeoObject();
            poi.setmId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.AR_ID))));
            poi.setCode(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.CODE)));
            poi.setmName(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.NAME)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.SHORT)));
            poi.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.LATITUDE))));
            poi.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.LONGITUDE))));
            poi.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.DATA)));
            poi.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.URL)));
            poi.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(UscARConstant.UscARField.ADDRESS)));

            buildingArray.add(index++, poi);
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
