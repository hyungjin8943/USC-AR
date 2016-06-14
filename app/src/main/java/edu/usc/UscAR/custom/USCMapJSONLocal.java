package edu.usc.UscAR.custom;

import android.content.Context;
import android.content.res.AssetManager;
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

/**
 * Created by Youngmin on 2016. 6. 1..
 */
public class USCMapJSONLocal  {

    public static ArrayList<PointOfInterest> buildingArray = new ArrayList<PointOfInterest>();

    public String readFromFile(Context context) {

        String ret = "";

        try {

            AssetManager assetManager = context.getAssets();

            InputStream inputStream = assetManager.open("mapJson.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Log.e("aaa", "Json: " + ret);


            }
        }
        catch (FileNotFoundException e) {
            Log.e("aaa", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("aaa", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void jsonConverter(String jsonStr) {

        try {

            JSONArray jsonArr = new JSONArray(jsonStr);

            for(int i=0; i<jsonArr.length(); i++) {

                JSONObject jsonObj = jsonArr.getJSONObject(i);
                PointOfInterest poi = new PointOfInterest();

                // Log.e("jsonArr", jsonObj.getString("code")); // short name
                jsonObj.getString("id"); // id number
                jsonObj.getString("name"); // full name
                jsonObj.getString("latitude");
                jsonObj.getString("longitude");
                jsonObj.getString("photo");
                jsonObj.getString("url");
                jsonObj.getString("address");

                poi.setId(jsonObj.getString("id"));
                poi.setCode(jsonObj.getString("code"));

                poi.setmName(jsonObj.getString("name"));

                poi.setLatitude(jsonObj.getDouble("latitude"));
                poi.setLongitude(jsonObj.getDouble("longitude"));
                poi.setPhoto(jsonObj.getString("photo"));
                poi.setUrl(jsonObj.getString("url"));
                poi.setAddress(jsonObj.getString("address"));
                poi.setDescription(jsonObj.getString("short"));

                buildingArray.add(i,poi);
            }
            // for(int i=0; i<buildingArray.size(); i++)
            //    Log.e("json",buildingArray.get(i).getCode());
        }
        catch(JSONException e){
            e.printStackTrace();
        }


    }

}
