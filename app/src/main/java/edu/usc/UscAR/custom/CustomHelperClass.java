package edu.usc.UscAR.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.beyondar.example.R;

/**
 * Created by Youngmin on 2016. 5. 29..
 */
@SuppressLint("SdCardPath")
public class CustomHelperClass {
    public static final int LIST_TYPE_EXAMPLE_1 = 1;

    public static World sharedWorld;

    private static LocationManager locationManager;
    private static Context myContext;
    private static long locLisMinTime = 2000; // 2 second
    private static float locLisMinDistance = 0; // 0 meter
    private static LocationListener locationListener;

    public static Double latitude = 0d;
    public static Double longitude = 0d;

    public static World generateObjects(final Context context) {
        if (sharedWorld != null) {
            return sharedWorld;
        }
        myContext = context;
        sharedWorld = new World(context);

        // The user can set the default bitmap. This is useful if you are
        // loading images form Internet and the connection get lost
        sharedWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);


        // User position (you can change it using the GPS listeners form Android API)
        // Add location listener to update world position
        CustomGPSTracker gps = new CustomGPSTracker(myContext);
        if(!gps.canGetLocation())
        {
            gps.showSettingsAlert();
        }

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                sharedWorld.setGeoPosition(location.getLatitude(),
                        location.getLongitude());

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // Toast.makeText(myContext, "lat: " + latitude + " long: "
                //        + longitude, Toast.LENGTH_LONG).show();

                CustomCameraActivity.updateTextValues();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locLisMinTime,
                locLisMinDistance, locationListener);

        Location locationGPS = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationGPS != null) {
            Toast.makeText(myContext,"lat: "+locationGPS.getLatitude()+" long: "
                    +locationGPS.getLongitude(),Toast.LENGTH_LONG).show();

            sharedWorld.setGeoPosition(locationGPS.getLatitude(),
                    locationGPS.getLongitude());
        } else {
            Toast.makeText(myContext, "last known location was null",
                    Toast.LENGTH_SHORT).show();

            sharedWorld.setGeoPosition(34.021774292, -118.2829284668); // USC Leavey Library
            // although it might
            // be miles away
        }


        /* Fixed User position */
        // sharedWorld.setGeoPosition(34.021774292, -118.2829284668);

        // Create an object with an image in the app resources.
        // Json file

        // USCMapJSONWeb uscMap = new USCMapJSONWeb();
        // uscMap.execute(); // execute HTTP request and get JSON file

        USCMapJSONLocal uscMapLocal = new USCMapJSONLocal();
        String jsonStr = uscMapLocal.readFromFile(myContext);
        uscMapLocal.jsonConverter(jsonStr);


        for(int i=0; i<uscMapLocal.buildingArray.size(); i++) {

            PointOfInterest poi = new PointOfInterest(i);
            poi = uscMapLocal.buildingArray.get(i);
            poi.setId(poi.getmId());
            // Log.e("lat", Double.toString(poi.getLatitude()));

            poi.setGeoPosition(poi.getLatitude(), poi.getLongitude());
            poi.setImageResource(R.drawable.creature_1);
            poi.setPhoto("http://web-app.usc.edu/venues/ACC.jpg");
            poi.setName(poi.getmId());
            poi.setDescription(poi.getDescription());
            sharedWorld.addBeyondarObject(poi);

            // GeoObject go1 = new GeoObject(i);
            // go1.setGeoPosition(poi.getLatitude(), poi.getLongitude());
            // go1.setImageResource(R.drawable.creature_1);
            // go1.setName(poi.getCode());
            // sharedWorld.addBeyondarObject(go1);

            // Log.e("json", uscMapLocal.buildingArray.get(i).getCode());
        }
        
/*

        GeoObject go1 = new GeoObject(1l);
        go1.setGeoPosition(34.021774292d, -118.282920000d);
        go1.setImageResource(R.drawable.creature_1);
        go1.setName("Creature 1");

        // Is it also possible to load the image asynchronously form internet
        GeoObject go2 = new GeoObject(2l);
        go2.setGeoPosition(34.0530000d, -118.2900000d);
        go2.setImageUri("http://beyondar.github.io/beyondar/images/logo_512.png");
        go2.setName("Online image");

        // Also possible to get images from the SDcard
        GeoObject go3 = new GeoObject(3l);
        go3.setGeoPosition(41.90550959641445d, 2.565873388087619d);
        go3.setImageUri("/sdcard/someImageInYourSDcard.jpeg");
        go3.setName("IronMan from sdcard");

        // And the same goes for the app assets
        GeoObject go4 = new GeoObject(4l);
        go4.setGeoPosition(41.90518862002349d, 2.565662767707665d);
        go4.setImageUri("assets://creature_7.png");
        go4.setName("Image from assets");

        GeoObject go5 = new GeoObject(5l);
        go5.setGeoPosition(34.0540000d, -118.2900000d);
        go5.setImageResource(R.drawable.creature_5);
        go5.setName("Creature 5");

        GeoObject go6 = new GeoObject(6l);
        go6.setGeoPosition(34.0544000d, -118.2904000d);
        go6.setImageResource(R.drawable.creature_6);
        go6.setName("Creature 6");

        GeoObject go7 = new GeoObject(7l);
        go7.setGeoPosition(34.0620000d, -118.2890000d);
        go7.setImageResource(R.drawable.creature_2);
        go7.setName("Creature 2");

        GeoObject go8 = new GeoObject(8l);
        go8.setGeoPosition(34.0620000d, -118.2880000d);
        go8.setImageResource(R.drawable.rectangle);
        go8.setName("Object 8");

        GeoObject go9 = new GeoObject(9l);
        go9.setGeoPosition(34.0640000d, -118.2890000d);
        go9.setImageResource(R.drawable.creature_4);
        go9.setName("Creature 4");


        GeoObject go10 = new GeoObject(10l);
        go10.setGeoPosition(34.0195846558d, -118.2887039185d);
        go10.setImageResource(R.drawable.object_stuff);
        go10.setName("Far away");

        // Add the GeoObjects to the world
        sharedWorld.addBeyondarObject(go1);
        sharedWorld.addBeyondarObject(go2, LIST_TYPE_EXAMPLE_1);
        sharedWorld.addBeyondarObject(go3);
        sharedWorld.addBeyondarObject(go4);
        sharedWorld.addBeyondarObject(go5);
        sharedWorld.addBeyondarObject(go6);
        sharedWorld.addBeyondarObject(go7);
        sharedWorld.addBeyondarObject(go8);
        sharedWorld.addBeyondarObject(go9);
        sharedWorld.addBeyondarObject(go10);

*/

        return sharedWorld;
    }

    public static void Pause() { // when camera view is paused
        locationManager.removeUpdates(locationListener);
    }

    public static void Resume() { // when camera view is resumed

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locLisMinTime,
                locLisMinDistance, locationListener);

        CustomGPSTracker gps = new CustomGPSTracker(myContext);
        if(!gps.canGetLocation())
        {
            Toast.makeText(myContext, "Please turn on GPS", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(myContext, "GPS Signal is captured!", Toast.LENGTH_LONG).show();
        }
    }

}
