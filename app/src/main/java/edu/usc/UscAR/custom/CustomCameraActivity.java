package edu.usc.UscAR.custom;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.ImageUtils;
import com.beyondar.android.util.math.Distance;
import com.beyondar.android.view.CameraView;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import edu.usc.UscAR.R;
import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.usc.UscAR.CustomWorldHelper;
import edu.usc.UscAR.pref.PreferenceManager;


public class CustomCameraActivity extends FragmentActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener,
        OnClickBeyondarObjectListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CAMERA = 0;

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    public TextView songName, mDuration;
    private Handler durationHandler = new Handler();

    public static final int INITIAL_DISTANCE = 40;
    public static final int MAX_DISTANCE = 100;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static int count = 0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    static double longitude, latitude;
    String mLastUpdateTime;
    boolean firstTime = true;

    private static final String TMP_IMAGE_PREFIX = "viewimage_";

    private static BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private Button mShowMap;
    private Button mNavigation;

    private Button mPlay, mStop, mPause;
    private SeekBar mAudioSeekBar;
    // video
    private VideoView mVideo;
    private Button mCloseVideo;
    // Rader View
    private RadarView mRadarView;
    private static RadarWorldPlugin mRadarPlugin;
    private SeekBar mSeekBarMaxDistance;
    private TextView mTextviewMaxDistance;

    // Render View Distance
    // private SeekBar mSeekBarMaxDistanceToRender;
    private TextView mArViewDistanceText;

    // Image Increase or Decrease
    private SeekBar mSeekBarPushAwayDistance;
    private TextView mMinFarText;

    // Info
    private static TextView mTextValues, mGPStextValues;

    // Detail information
    private CustomSlidingPaneLayout slidingPaneLayout;
    private TextView detailCode, detailName, detailDescription, detailHomepage, detailDistance;
    private ImageView detailImg;
    private VideoView detailVideo;
    private Button detailClose;

    private String result = "en_";

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        // mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)|| (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)|| (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)){
            // Camera permission has not been granted.

            Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CAMERA);
        }

        // The first thing that we do is to remove all the generated temporal
        // images. Remember that the application needs external storage write
        // permission.
        //cleanTempFolder();

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Custom XML, Beyondar XML(Camera view), Radar, Render, Text Info
        loadViewFromXML();

        // Create Radar Plugin, Create RadarView, Set RadarView max distance
        createRadarPlugin();

        // How far can we detect objects
        setRenderXMLInfo();


        // We create the world and fill it
        mWorld = CustomHelperClass.generateObjects(this);
        PreferenceManager.getPreferenceManager(this.getApplicationContext()).putValue(PreferenceManager.KEY_USC_AR_DEFAULT_INIT, 1);

        mBeyondarFragment.setWorld(mWorld);

        // add the plugin(World + RadarView)
        mWorld.addPlugin(mRadarPlugin);

        // We also can see the Frames per seconds
        // mBeyondarFragment.showFPS(false);

        // set radar info
        mSeekBarMaxDistance.setOnSeekBarChangeListener(this);
        mSeekBarMaxDistance.setMax(MAX_DISTANCE); // 100 m
        mSeekBarMaxDistance.setProgress(INITIAL_DISTANCE); // 40 m

        // object click listener
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        // This method will replace all GeoObjects the images with a simple
        // static view
        //replaceImagesByStaticViews(mWorld);
    }

    //////////////////////////////// Static View ///////////////////////////////////////////////////
    private void replaceImagesByStaticViews(World world) {
        String path = getTmpPath();

        for (BeyondarObjectList beyondarList : world.getBeyondarObjectLists()) {
            for (BeyondarObject beyondarObject : beyondarList) {
                // First let's get the view, inflate it and change some stuff
                View view = getLayoutInflater().inflate(R.layout.static_beyondar_object_view, null);
                TextView textView = (TextView) view.findViewById(R.id.geoObjectName);
                textView.setText(beyondarObject.getName());
                int height_in_pixels = textView.getLineCount() * textView.getLineHeight(); //approx height text
                textView.setHeight(height_in_pixels);
                try {
                    // Now that we have it we need to store this view in the
                    // storage in order to allow the framework to load it when
                    // it will be need it
                    for (int i = 0; i < USCMapJSONLocal.buildingArray.size(); i++) {
                        if (beyondarObject.getId() == USCMapJSONLocal.buildingArray.get(i).getId()) {

                            String imageName = TMP_IMAGE_PREFIX + USCMapJSONLocal.buildingArray.get(i).getmId() + ".png";

                            // String imageName = TMP_IMAGE_PREFIX + "c + ".png";
                            ImageUtils.storeView(view, path, imageName);
                            beyondarObject.setImageUri(path + imageName);
                        }
                    }
                    // If there are no errors we can tell the object to use the
                    // view that we just stored

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the path to store temporally the images. Remember that you need to
     * set WRITE_EXTERNAL_STORAGE permission in your manifest in order to
     * write/read the storage
     */
    private String getTmpPath() {
        return getExternalFilesDir(null).getAbsoluteFile() + "/tmp/";
    }

    /** Clean all the generated files */
    private void cleanTempFolder() {
        File tmpFolder = new File(getTmpPath());
        if (tmpFolder.isDirectory()) {
            String[] children = tmpFolder.list();
            for (int i = 0; i < children.length; i++) {
                if (children[i].startsWith(TMP_IMAGE_PREFIX)) {
                    new File(tmpFolder, children[i]).delete();
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void loadViewFromXML() {

        // Load information from Custom XML
        setContentView(R.layout.activity_custom_main);

        // Use Beyondar XML (Camera View)
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondarFragment);

        // Get ID from Custom XML (Map Button)
        mShowMap = (Button) findViewById(R.id.showMapButton);
        mShowMap.setOnClickListener(this);

        // Get ID from Custom XML (Search Button)
        mNavigation = (Button) findViewById(R.id.navigationButton);
        mNavigation.setOnClickListener(this);
        mNavigation.setVisibility(View.INVISIBLE);

        // Get ID from Custom XML (Close description panel)
        detailClose = (Button) findViewById(R.id.detail_close);
        detailClose.setOnClickListener(this);

        // Get ID from Custom XML (Radar)
        mTextviewMaxDistance = (TextView) findViewById(R.id.textMaxDistance);
        mSeekBarMaxDistance = (SeekBar) findViewById(R.id.seekBarMaxDistance);
        mRadarView = (RadarView) findViewById(R.id.radarView);

        // Get ID from Custom XML (Text information)
        mTextValues = (TextView) findViewById(R.id.textValues);
        mGPStextValues = (TextView) findViewById(R.id.GPStextValues);

        // Get ID from Custom XML (Render)
        mArViewDistanceText = (TextView) findViewById(R.id.textBarArViewDistance);
        // mSeekBarMaxDistanceToRender = (SeekBar) findViewById(R.id.seekBarArViewDistance);

        // Get ID from Custom XML (Image Increase Decrease)
        mMinFarText = (TextView) findViewById(R.id.textBarMin);
        mSeekBarPushAwayDistance = (SeekBar) findViewById(R.id.seekBarMin);

        // Get ID from Custom XML (Audio Controller)
        mPlay = (Button) findViewById(R.id.play_button);
        mPlay.setOnClickListener(this);
        mStop = (Button) findViewById(R.id.stop_button);
        mStop.setOnClickListener(this);
        mPause = (Button) findViewById(R.id.pause_button);
        mPause.setOnClickListener(this);
        mAudioSeekBar = (SeekBar) findViewById(R.id.audio_seekbar);
        mDuration = (TextView) findViewById(R.id.duration);
        mPlay.setVisibility(View.INVISIBLE);
        mStop.setVisibility(View.INVISIBLE);
        mPause.setVisibility(View.INVISIBLE);
        mAudioSeekBar.setVisibility(View.INVISIBLE);
        mDuration.setVisibility(View.INVISIBLE);

        mVideo = (VideoView) findViewById(R.id.camera_video);
        mVideo.setZOrderMediaOverlay(true);
        mVideo.setZOrderOnTop(true);


        mediaController = new MediaController(CustomCameraActivity.this);
        mediaController.setAnchorView(mVideo);
        mVideo.setMediaController(mediaController);
        mCloseVideo = (Button)findViewById(R.id.closeVideo);
        mCloseVideo.setOnClickListener(this);
        mCloseVideo.setVisibility(View.INVISIBLE);
    }

    private void setRenderXMLInfo() {

        // Render Distance
        // mArViewDistanceText.setText("Max dst render:");
        // mSeekBarMaxDistanceToRender.setMax(100); // 100 m
        // mSeekBarMaxDistanceToRender.setOnSeekBarChangeListener(this);

        // Image Increase Decrease
        mMinFarText.setText("Push away:");
        mSeekBarPushAwayDistance.setMax(MAX_DISTANCE); // push away 100m (image size decrease)
        mSeekBarPushAwayDistance.setOnSeekBarChangeListener(this);

        updateTextValues(); // max distance render

        // mSeekBarMaxDistanceToRender.setProgress(100); // initial value 100m
        mSeekBarPushAwayDistance.setProgress(0); // initial value 0m
    }

    private void createRadarPlugin() {
        // Create the Radar plugin
        mRadarPlugin = new RadarWorldPlugin(this);
        // set the radar view in to our radar plugin
        mRadarPlugin.setRadarView(mRadarView);
        // Set how far (in meters) we want to display in the view
        mRadarPlugin.setMaxDistance(MAX_DISTANCE);

        // We can customize the color of the items
        mRadarPlugin.setListColor(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, Color.RED);
        // and also the size
        mRadarPlugin.setListDotRadius(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, 3);
    }

    public static void updateTextValues() {
        mTextValues.setText(" push away from me=" + mBeyondarFragment.getPushAwayDistance() + "m" + "\r\n" +
                " Max Render distance=" + mBeyondarFragment.getMaxDistanceToRender() + "m" + "\r\n" +
                " Radar Distance=" + mRadarPlugin.getMaxDistance() + "m");
        mGPStextValues.setText("latitude=" + latitude + "\r\n" + "longitude=" + longitude);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mRadarPlugin == null)
            return;
        if (seekBar == mSeekBarMaxDistance) { // Radar Progress Bar Change
            // float value = ((float) progress/(float) 10000);
            mTextviewMaxDistance.setText("Distance: " + progress + "m");
            mRadarPlugin.setMaxDistance(progress);
            mBeyondarFragment.setMaxDistanceToRender(progress);

        } // else if (seekBar == mSeekBarMaxDistanceToRender) { ... } // Render Progress Bar Change
        else if (seekBar == mSeekBarPushAwayDistance) {
            mMinFarText.setText("Push away from me: " + progress + "m");
            mBeyondarFragment.setPushAwayDistance(progress);
        }
        else if(seekBar == mAudioSeekBar) {

        }

        updateTextValues();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        if (v == mShowMap) {

            mPlay.setVisibility(View.INVISIBLE);
            mStop.setVisibility(View.INVISIBLE);
            mPause.setVisibility(View.INVISIBLE);
            mAudioSeekBar.setVisibility(View.INVISIBLE);
            mDuration.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(this, CustomMapActivity.class);
            startActivity(intent);
        }
        if (v == mNavigation) {
            Intent intent = new Intent(this, CustomSearchActivity.class);
            startActivity(intent);
        }
        if (v == detailClose) {
            slidingPaneLayout.closePane();


            if (detailVideo != null) {
                detailVideo.stopPlayback();
                detailVideo.setVisibility(View.GONE);

            }

        }
        if(v == mPlay) {
            durationHandler.postDelayed(updateSeekBarTime, 100);
            mediaPlayer.start();
        }
        if(v == mStop) {
            durationHandler.removeCallbacks(updateSeekBarTime);
            mAudioSeekBar.setProgress(0);
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        if(v == mPause) {
            durationHandler.removeCallbacks(updateSeekBarTime);
            mediaPlayer.pause();
        }
        if(v== mCloseVideo){

            mVideo.stopPlayback();
            mVideo.setVisibility(View.GONE);
            mCloseVideo.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public void onClickBeyondarObject(final ArrayList<BeyondarObject> beyondarObjects) { // when users click objects

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Show Information or Listen Narration");
        final CustomGeoObject customGeoObject = (CustomGeoObject) beyondarObjects.get(0);


        builder.setPositiveButton("Info", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {


                slidingPaneLayout = (CustomSlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
                slidingPaneLayout.openPane();

                detailImg = (ImageView) findViewById((R.id.detail_image));
                detailCode = (TextView) findViewById(R.id.detail_code);
                detailName = (TextView) findViewById(R.id.detail_name);
                detailDescription = (TextView) findViewById(R.id.detail_description);
                detailHomepage = (TextView) findViewById(R.id.detail_homepage);
                detailDistance = (TextView) findViewById(R.id.detail_distance);
                detailVideo = (VideoView) findViewById(R.id.detail_video);


                if (beyondarObjects.size() > 0) {


                    for (int i = 0; i < USCMapJSONLocal.buildingArray.size(); i++) {
                        if (beyondarObjects.get(0).getId() == USCMapJSONLocal.buildingArray.get(i).getId()) {

                            class DownloadImageTask extends AsyncTask<String, Void, Bitmap> { // Async function for yahoo image
                                ImageView bmImage;

                                public DownloadImageTask(ImageView bmImage) {
                                    this.bmImage = bmImage;
                                }

                                protected Bitmap doInBackground(String... urls) {
                                    String urldisplay = urls[0];
                                    Bitmap mIcon11 = null;
                                    try {
                                        InputStream in = new java.net.URL(urldisplay).openStream();
                                        mIcon11 = BitmapFactory.decodeStream(in);
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage());
                                        e.printStackTrace();
                                    }
                                    return mIcon11;
                                }

                                protected void onPostExecute(Bitmap result) {
                                    bmImage.setImageBitmap(result);
                                }
                            }

                            new DownloadImageTask(detailImg) // download image from url
                                    .execute("http://web-app.usc.edu/venues/" + USCMapJSONLocal.buildingArray.get(i).getPhoto());
                            //.execute("http://web-app.usc.edu/venues/ACC.jpg");

                            detailCode.setText("Code: " + USCMapJSONLocal.buildingArray.get(i).getCode());
                            detailName.setText("Name: " + USCMapJSONLocal.buildingArray.get(i).getmName());
                            detailDescription.setText("Description: " + USCMapJSONLocal.buildingArray.get(i).getDescription());
                            detailHomepage.setText("Homepage: " + USCMapJSONLocal.buildingArray.get(i).getUrl());
                            detailDistance.setText("Distance: " + beyondarObjects.get(0).getDistanceFromUser() + "m");

                            detailVideo.setVideoPath("http://www.ebookfrenzy.com/android_book/movie.mp4");
                            // detailVideo.setVideoPath("http://www-scf.usc.edu/~shin630/Youngmin/musics/guitar/Shine_of_Silver_Thaw.mp3");
                            mediaController = new MediaController(CustomCameraActivity.this);
                            mediaController.setAnchorView(detailVideo);
                            detailVideo.setMediaController(mediaController);
                            detailVideo.setVisibility(View.VISIBLE); // visible Videoview

                            // detailVideo.start();

                            break;
                        }
                    }

                }
            }

        });

        builder.setNegativeButton("Narration", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    durationHandler.removeCallbacks(updateSeekBarTime);
                }
                mPlay.setVisibility(View.VISIBLE);
                mStop.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.VISIBLE);
                mAudioSeekBar.setVisibility(View.VISIBLE);
                mDuration.setVisibility(View.VISIBLE);

                selectCountry(customGeoObject.getmId());
                /*
//                int resID = getResources().getIdentifier("shine", "raw", getPackageName());
                int resID = getResources().getIdentifier((result + customGeoObject.getmId()), "raw", getPackageName());
                mediaPlayer = new MediaPlayer();
                // mediaController = new MediaController(CustomCameraActivity.this);
                // mediaController.show();
                mediaPlayer = MediaPlayer.create(CustomCameraActivity.this, resID);
                finalTime = mediaPlayer.getDuration();
                mAudioSeekBar.setMax((int) finalTime);
                mAudioSeekBar.setClickable(false);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you

                timeElapsed = mediaPlayer.getCurrentPosition();
                mAudioSeekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);
                */

                /*
                String url = "http://www-scf.usc.edu/~shin630/Youngmin/musics/guitar/Shine_of_Silver_Thaw.mp3";

                try {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                */

            }

        });
        builder.setNeutralButton("Video", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    durationHandler.removeCallbacks(updateSeekBarTime);
                }
                FetchVideoTask fetchVideoTask = new FetchVideoTask();
                fetchVideoTask.execute(customGeoObject.getLatitude(),customGeoObject.getLongitude());

            }

        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            mAudioSeekBar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            mDuration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) { // every 1 seconds gps update

        count++;

        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        Log.d(TAG, "lat!!!!" + String.valueOf(location.getLatitude()));

        // Toast.makeText(this, String.valueOf(location.getLatitude()),
        //         Toast.LENGTH_LONG).show();

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v(TAG, Double.toString(latitude));

        updateTextValues();

        // CustomHelperClass.sharedWorld.setGeoPosition(34.0217, -118.284);
        mWorld.setGeoPosition(location.getLatitude(),location.getLongitude());


        if (count >= 30 || firstTime) // Every 30 seconds array update
        {
            count = 0;
            firstTime=false;
            Toast.makeText(this, "redraw", Toast.LENGTH_LONG).show();


            int a = mWorld.getBeyondarObjectLists().get(0).size();
            Log.v(TAG, Integer.toString(a));
            //String jsonStr = uscMapLocal.readFromFile(this);
            //uscMapLocal.jsonConverter(jsonStr);

            //CustomHelperClass.sharedWorld.clearWorld();
            //CustomHelperClass.sharedWorld.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

            for (int i = 0; i < mWorld.getBeyondarObjectLists().get(0).size(); i++) {

                GeoObject geoObject =(GeoObject) mWorld.getBeyondarObjectLists().get(0).get(i);
                // Log.v(TAG,geoObject.getName());

                // Log.e("distance2", Double.toString(Distance.calculateDistanceMeters(CustomHelperClass.longitude, CustomHelperClass.latitude, geoObject.getLongitude(), geoObject.getLatitude())));
                // Log.e("distance2", Double.toString(Distance.calculateDistanceMeters(-118.284, 34.0217, geoObject.getLongitude(), geoObject.getLatitude())));

                // double distance = Distance.calculateDistanceMeters(-118.284, 34.0217, geoObject.getLongitude(), geoObject.getLatitude());
                double distance = Distance.calculateDistanceMeters(location.getLongitude(),location.getLatitude(), geoObject.getLongitude(), geoObject.getLatitude());

                if (distance < MAX_DISTANCE) {
                    //String image = "viewimage_" + geoObject.getmId();
                    geoObject.setVisible(true);

                    // int imageResource = this.getResources().getIdentifier(image, "drawable", this.getPackageName());

                    //geoObject.setGeoPosition(geoObject.getLatitude(), geoObject.getLongitude());
                    //geoObject.setImageResource(imageResource); // geoObject.setImageResource(R.drawable.creature_1);
                    //geoObject.setName(geoObject.getmName()); // Beyondar Object Id
                    //CustomHelperClass.sharedWorld.addBeyondarObject(geoObject);
                } else {
                    geoObject.setVisible(false);
                }
            }
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            durationHandler.removeCallbacks(updateSeekBarTime);
            mediaPlayer = null;
        }

        stopLocationUpdates();


        this.mBeyondarFragment.onPause();
        CustomHelperClass.Pause();
        //System.gc();
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getCameraDisplayOrientation() {
        int rotation = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlay.setVisibility(View.INVISIBLE);
        mStop.setVisibility(View.INVISIBLE);
        mPause.setVisibility(View.INVISIBLE);
        mAudioSeekBar.setVisibility(View.INVISIBLE);
        mDuration.setVisibility(View.INVISIBLE);

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

        this.mBeyondarFragment.onResume();
        CustomHelperClass.Resume();

        CameraView cv = mBeyondarFragment.getCameraView();

        int orientation = 0;

        if (Build.VERSION.SDK_INT < 9) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                cv.getCamera().getParameters().set("orientation", "portrait");
                orientation = 90;
            } else {
                cv.getCamera().getParameters().set("orientation", "landscape");
                orientation = 0;
            }
        } else {

            orientation = getCameraDisplayOrientation();
            cv.getCamera().getParameters().setRotation(orientation);
        }
        cv.getCamera().setDisplayOrientation(orientation);



    }

    private class FetchVideoTask extends AsyncTask<Double, Void,  List<Feature>> {
        @Override
        protected  List<Feature> doInBackground(Double... params) {

            if(params.length ==0)
                return null;
            Log.v("fetch",params[0] + ","+ params[1]);

            System.out.println(Double.toString(params[0]-Distance.fastConversionMetersToGeoPoints(4.00)));
            System.out.println(Double.toString(params[1]-Distance.fastConversionMetersToGeoPoints(4.00)));
            System.out.println(Double.toString(Distance.fastConversionMetersToGeoPoints(1.00)));
            try {
                StringBuilder urlAPI = new StringBuilder();
                // Azure
                urlAPI.append("http://mediaq1.cloudapp.net/MediaQ_MVC_V3/api/geoq/rectangle_query?swlat=");
                urlAPI.append(Double.toString(params[0]-Distance.fastConversionMetersToGeoPoints(4.00)));
                urlAPI.append("&swlng="+Double.toString(params[1]-Distance.fastConversionMetersToGeoPoints(4.00)));
                urlAPI.append("&nelat="+Double.toString(params[0]+Distance.fastConversionMetersToGeoPoints(4.00)));
                urlAPI.append("&nelng="+Double.toString(params[1]+Distance.fastConversionMetersToGeoPoints(4.00)));
                urlAPI.append("&X-API-KEY=8b51UFM2SlBltx3s6864eUO1zSoefeK5");

                Log.v("url", urlAPI.toString());
                URL url = new URL(urlAPI.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                InputStream stream = conn.getInputStream();

                try {
                    GeoJSONObject geoJSON = GeoJSON.parse(stream);


                   FeatureCollection featureCollection = new FeatureCollection(geoJSON.toJSON());
                    List<Feature> featureList = featureCollection.getFeatures();



                    for(int i=0; i< featureList.size();i++){
                        System.out.println(featureList.get(i).getProperties().getString("href"));
                        System.out.print("/////");
                    }
                    return featureList;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

               /* BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }*/

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute( List<Feature> features) {
            if ( features.size()==0){
                Context context = getApplicationContext();
                CharSequence text = "No video for this location";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {

                try {
                    for(int i=0; i< features.size();i++){
                        String mimeType = features.get(i).getProperties().getString("videoid");
                        if (mimeType.contains(".mp4")) {
                            System.out.println(features.get(i).getProperties().getString("href")); // now always choosing the number index 1
                            Uri uri =Uri.parse(features.get(i).getProperties().getString("href"));
                            System.out.println(uri.toString());
                            mVideo.setVideoURI(uri);
                            break;
                        }
                     }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

          //  }

                    }


            mCloseVideo.setVisibility(View.VISIBLE);

            mVideo.setVisibility(View.VISIBLE); // visible Videoview

            mVideo.start();




        }

        }

        private void selectCountry(final String id) {
            final CharSequence[] items = {
                    "English", "Chinese", "Korean"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select your language");
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            result = "en_";
                            break;
                        case 1:
                            result = "ch_";
                            break;
                        case 2:
                            result = "ko_";
                            break;
                    }

                    int resID = getResources().getIdentifier((result + id), "raw", getPackageName());
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer = MediaPlayer.create(CustomCameraActivity.this, resID);
                    } catch (Resources.NotFoundException e) {
                        Toast.makeText(CustomCameraActivity.this, R.string.no_narration, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    finalTime = mediaPlayer.getDuration();
                    mAudioSeekBar.setMax((int) finalTime);
                    mAudioSeekBar.setClickable(false);
                    mediaPlayer.start(); // no need to call prepare(); create() does that for you

                    timeElapsed = mediaPlayer.getCurrentPosition();
                    mAudioSeekBar.setProgress((int) timeElapsed);
                    durationHandler.postDelayed(updateSeekBarTime, 100);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mPlay.setVisibility(View.GONE);
                            mStop.setVisibility(View.GONE);
                            mPause.setVisibility(View.GONE);
                            mAudioSeekBar.setVisibility(View.GONE);
                            mDuration.setVisibility(View.GONE);
                        }
                    });

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

