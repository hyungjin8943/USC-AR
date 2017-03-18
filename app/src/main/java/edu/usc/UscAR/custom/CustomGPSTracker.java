package edu.usc.UscAR.custom;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.util.math.Distance;
import edu.usc.UscAR.R;
/**
 * Created by Youngmin on 2016. 5. 29..
 */
public class CustomGPSTracker extends Service implements LocationListener {

    private BeyondarFragmentSupport mBeyondarFragment;

    private Context mContext;

    public static Double mlatitude = 0d;
    public static Double mlongitude = 0d;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    double speed, direction;
    // The minimum distance to change Updates in meters
    // private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    // The minimum time between updates in milliseconds
    // private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
    private static final long MIN_TIME_BW_UPDATES = 1000;
    // Declaring a Location Manager
    protected LocationManager locationManager;

    public CustomGPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            Log.d("Getting location", "Location found");
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    // Stop using GPS listener
    // Calling this function will stop using GPS in your app
    public void stopUsingGPS() {

        if (locationManager != null) {
            // Toast.makeText(mContext, "GPS must be stopped!!!", Toast.LENGTH_LONG).show();
            locationManager.removeUpdates(CustomGPSTracker.this);
            locationManager = null;
        }
    }

    // Function to get latitude
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    // Function to get longitude
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }

    // Function to check GPS/wifi enabled ( @return boolean )
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    // Function to show settings alert dialog
    // On pressing Settings button will launch Settings Options
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            speed = location.getSpeed();
            direction = location.getBearing();
        }
        this.location = location;

        CustomHelperClass.latitude = getLatitude();
        CustomHelperClass.longitude = getLongitude();
        CustomCameraActivity.updateTextValues();

        Toast.makeText(mContext, "GPS Tracker latitude: " + CustomHelperClass.latitude, Toast.LENGTH_LONG).show();
        Log.e("GPS Tracker latitude", Double.toString(CustomHelperClass.latitude));

        // CustomHelperClass.sharedWorld.setGeoPosition(34.0217, -118.284);
        CustomHelperClass.sharedWorld.setGeoPosition(CustomHelperClass.latitude, CustomHelperClass.longitude);


        USCMapJSONLocal uscMapLocal = new USCMapJSONLocal();
        String jsonStr = uscMapLocal.readFromFile(mContext);
        uscMapLocal.jsonConverter(mContext, jsonStr);

        CustomHelperClass.sharedWorld.clearWorld();
        CustomHelperClass.sharedWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        for(int i=0; i<uscMapLocal.buildingArray.size(); i++) {

            CustomGeoObject geoObject = new CustomGeoObject(i);
            geoObject = uscMapLocal.buildingArray.get(i);

            // Log.e("distance2", Double.toString(Distance.calculateDistanceMeters(CustomHelperClass.longitude, CustomHelperClass.latitude, geoObject.getLongitude(), geoObject.getLatitude())));
            // Log.e("distance2", Double.toString(Distance.calculateDistanceMeters(-118.284, 34.0217, geoObject.getLongitude(), geoObject.getLatitude())));

            // double distance = Distance.calculateDistanceMeters(-118.284, 34.0217, geoObject.getLongitude(), geoObject.getLatitude());
            double distance = Distance.calculateDistanceMeters(CustomHelperClass.longitude, CustomHelperClass.latitude, geoObject.getLongitude(), geoObject.getLatitude());

            if(distance < 50) {
                String image = "viewimage_" + geoObject.getmId();
                int imageResource = mContext.getResources().getIdentifier(image, "drawable", mContext.getPackageName());

                geoObject.setGeoPosition(geoObject.getLatitude(), geoObject.getLongitude());
                geoObject.setImageResource(imageResource); // geoObject.setImageResource(R.drawable.creature_1);
                geoObject.setName(geoObject.getmName()); // Beyondar Object Id
                CustomHelperClass.sharedWorld.addBeyondarObject(geoObject);
            }
        }



    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

