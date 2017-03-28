/*
 * Copyright (C) 2014 BeyondAR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usc.UscAR;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.usc.UscAR.R;

import edu.usc.UscAR.custom.CustomCameraActivity;
import edu.usc.UscAR.custom.CustomGeoObject;
import edu.usc.UscAR.db.UscARPersister;

public class BeyondarExamples extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;
    private static final String TAG = "BeyondarExamples";

    //    public static final String ARPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/USCAR";
    public static final String ARPath = "/sdcard/USCAR";
    public static final String ARImagePath = ARPath + "/ar_images/";

    private Context mContext;

    private Toolbar mToolbar;

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = BeyondarExamples.this;
        imageView = (ImageView) findViewById(R.id.ar_backgroud);
        initActionBar();

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            // Camera permission has not been granted.

            Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CAMERA);
        }

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
        makeDirectory();
    }

    private void startCameraActivity() {
        Intent intent = new Intent(BeyondarExamples.this, CustomCameraActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomWorldHelper.sharedWorld = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_import:
                ImportAsynTask asyncTask = new ImportAsynTask();
                asyncTask.execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImportAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            importJsonFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mContext, mContext.getString(R.string.import_done), Toast.LENGTH_SHORT).show();
        }
    }

    private void importJsonFile() {
        String jsonStr = getReadJsonFile();
        if (jsonStr != null) {
            Log.i(TAG, "jsonStr =" + jsonStr);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonStr);
                JSONArray ar = jsonObject.getJSONArray("ar");
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject jsonObj = ar.getJSONObject(i);
                    CustomGeoObject poi = new CustomGeoObject();
                    poi.setmId(jsonObj.getString("ar_id"));
                    poi.setCode(jsonObj.getString("code"));
                    poi.setmName(jsonObj.getString("name"));
                    poi.setLatitude(jsonObj.getDouble("latitude"));
                    poi.setLongitude(jsonObj.getDouble("longitude"));
                    poi.setPhoto(jsonObj.getString("photo"));
                    poi.setUrl(jsonObj.getString("url"));
                    poi.setAddress(jsonObj.getString("address"));
                    poi.setDescription(jsonObj.getString("short"));
                    UscARPersister.getUscARPersister(mContext.getApplicationContext()).insertUscARFromExtension(poi);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getReadJsonFile() {
        File myFile = new File(ARPath + "/ar.json");
        FileInputStream fIn = null;
        try {
            fIn = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader myReader = new BufferedReader(
                new InputStreamReader(fIn));
        String aDataRow = "";
        StringBuilder aBuffer = new StringBuilder();
        try {
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer.append(aDataRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aBuffer.toString();
    }

    private void makeDirectory() {
        String dirPath = ARPath;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }
}
