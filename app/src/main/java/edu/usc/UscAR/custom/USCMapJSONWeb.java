package edu.usc.UscAR.custom;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Youngmin on 2016. 6. 1..
 */
public class USCMapJSONWeb extends AsyncTask<Void, Void, String> { // Async function for USC Map JSON file

    @Override
    protected String doInBackground(Void... params) {

        try {
            URL url = new URL("http://web-app.usc.edu/maps/all_map_data2.js");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // connection

            conn.setRequestMethod("GET"); // Get method

            int responseCode = conn.getResponseCode(); // get response code
            Log.e("responseCode", Integer.toString(responseCode));

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

            // TODO: not found check
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }

            Log.e("jsonStr", builder.toString());
            return builder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonStr) {

        try {
            String temp = jsonStr.substring(10);
               // String temp2 = jsonStr.replace("markers = ","");

            JSONArray jsonArr = new JSONArray(temp);

            for(int i=0; i<jsonArr.length(); i++) {

                JSONObject jsonObj = jsonArr.getJSONObject(i);

                // Log.e("jsonArr", jsonObj.getString("code")); // short name
                jsonObj.getString("id"); // id number
                jsonObj.getString("name"); // full name
                jsonObj.getString("latitude");
                jsonObj.getString("longitude");
                jsonObj.getString("photo");
                jsonObj.getString("url");
                jsonObj.getString("address");
            }

            // Log.e("json",jsonObj.getString("id"));
        }
        catch(JSONException e){
            e.printStackTrace();
        }


    }
}
