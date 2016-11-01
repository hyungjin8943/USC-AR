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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.beyondar.example.R;

import edu.usc.UscAR.custom.CustomCameraActivity;

public class BeyondarExamples extends Activity {

    private static final int REQUEST_CAMERA = 0;
    private static final String TAG = "CustomCameraActivity";

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.ar_backgroud);

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
}
