package edu.usc.UscAR.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.beyondar.android.util.math.Distance;
import com.beyondar.android.world.World;
import com.beyondar.example.R;

/**
 * Created by Youngmin on 2016. 5. 29..
 */
@SuppressLint("SdCardPath")
public class CustomHelperClass {
    public static final int LIST_TYPE_EXAMPLE_1 = 1;

    public static World sharedWorld;
    // public static CustomGPSTracker gpsTracker;

    private static Context myContext;

    public static Double latitude = 0d;
    public static Double longitude = 0d;

    public static World generateObjects(final Context context) {
        if (sharedWorld != null) {
            return sharedWorld;
        }
        myContext = context;
        sharedWorld = new World(context);
        // gpsTracker = new CustomGPSTracker(myContext);  // Add location listener to update world position

        // The user can set the default bitmap. This is useful if you are
        // loading images form Internet and the connection get lost
        sharedWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);


        // User position (you can change it using the GPS listeners form Android API)
        // if(!gpsTracker.canGetLocation())
        // {
        //     gpsTracker.showSettingsAlert();
        // }

        // USCMapJSONWeb uscMap = new USCMapJSONWeb();
        // uscMap.execute(); // execute HTTP request and get JSON file

        longitude = -118.2829284668d;
        latitude = 34.021774292d;
        sharedWorld.setGeoPosition(34.021774292, -118.2829284668); // Initial: USC Leavey Library

        USCMapJSONLocal uscMapLocal = new USCMapJSONLocal();
        String jsonStr = uscMapLocal.readFromFile(myContext);
        uscMapLocal.jsonConverter(jsonStr);


        for(int i=0; i<uscMapLocal.buildingArray.size(); i++) {

            CustomGeoObject geoObject = new CustomGeoObject(i);
            geoObject = uscMapLocal.buildingArray.get(i);

            // Log.e("distance1", Double.toString(Distance.calculateDistanceMeters(longitude, latitude, geoObject.getLongitude(), geoObject.getLatitude())));

            double distance = Distance.calculateDistanceMeters(longitude, latitude, geoObject.getLongitude(), geoObject.getLatitude());


            String image = "viewimage_" + geoObject.getmId();
            int imageResource = context.getResources().getIdentifier(image, "drawable", context.getPackageName());

            geoObject.setGeoPosition(geoObject.getLatitude(), geoObject.getLongitude());
            geoObject.setImageResource(imageResource); // geoObject.setImageResource(R.drawable.creature_1);
            geoObject.setName(geoObject.getmName()); // Beyondar Object Id
            if(distance < CustomCameraActivity.MAX_DISTANCE) {
                geoObject.setVisible(true);

            }else{
                geoObject.setVisible(false);
            }
            sharedWorld.addBeyondarObject(geoObject);

        }

        return sharedWorld;
    }


    public static void Pause() { // when camera view is paused

        Toast.makeText(myContext, "Pause", Toast.LENGTH_LONG).show();
        // gpsTracker.stopUsingGPS(); // removeUpdates

    }

    public static void Resume() { // when camera view is resumed




        Toast.makeText(myContext, "Resume", Toast.LENGTH_LONG).show();
        // gpsTracker.getLocation(); // requestLocationUpdates

        // if(!gpsTracker.canGetLocation())
        //{
        //    gpsTracker.showSettingsAlert();
        //}
    }
}
