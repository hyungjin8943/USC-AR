package edu.usc.UscAR.custom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;
import com.beyondar.example.R;

import java.io.InputStream;
import java.util.ArrayList;

import edu.usc.UscAR.CustomWorldHelper;


public class CustomCameraActivity extends FragmentActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener,
        OnClickBeyondarObjectListener {

    private static BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private Button mShowMap;
    private Button mNavigation;
    // Rader View
    private RadarView mRadarView;
    private static RadarWorldPlugin mRadarPlugin;
    private SeekBar mSeekBarMaxDistance;
    private TextView mTextviewMaxDistance;

    // Render View Distance
    private SeekBar mSeekBarMaxDistanceToRender;
    private TextView mArViewDistanceText;

    // Image Increase or Decrease
    private SeekBar mSeekBarPushAwayDistance;
    private TextView mMinFarText;

    // Info
    private static TextView mTextValues, mGPStextValues;

    // Detail information
    private CustomSlidingPaneLayout slidingPaneLayout;
    private TextView detailName;
    private Button detailClose;
    private ImageView imageName;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mBeyondarFragment.setWorld(mWorld);

        // add the plugin(World + RadarView)
        mWorld.addPlugin(mRadarPlugin);

        // We also can see the Frames per seconds
        // mBeyondarFragment.showFPS(false);

        // set radar info
        mSeekBarMaxDistance.setOnSeekBarChangeListener(this);
        mSeekBarMaxDistance.setMax(500); // 500 m
        mSeekBarMaxDistance.setProgress(200); // 200 m

        // object click listener
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);
    }

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
        mSeekBarMaxDistanceToRender = (SeekBar) findViewById(R.id.seekBarArViewDistance);

        // Get ID from Custom XML (Image Increase Decrease)
        mMinFarText = (TextView) findViewById(R.id.textBarMin);
        mSeekBarPushAwayDistance = (SeekBar) findViewById(R.id.seekBarMin);
    }

    private void setRenderXMLInfo() {

        // Render Distance
        mArViewDistanceText.setText("Max dst render:");
        mSeekBarMaxDistanceToRender.setMax(1000); // 1 km
        mSeekBarMaxDistanceToRender.setOnSeekBarChangeListener(this);

        // Image Increase Decrease
        mMinFarText.setText("Push away:");
        mSeekBarPushAwayDistance.setMax(100); // push away 100m (image size decrease)
        mSeekBarPushAwayDistance.setOnSeekBarChangeListener(this);

        updateTextValues(); // max distance render

        mSeekBarMaxDistanceToRender.setProgress(500); // initial value 500m
        mSeekBarPushAwayDistance.setProgress(1); // initial value 1m
    }

    private void createRadarPlugin() {
        // Create the Radar plugin
        mRadarPlugin = new RadarWorldPlugin(this);
        // set the radar view in to our radar plugin
        mRadarPlugin.setRadarView(mRadarView);
        // Set how far (in meters) we want to display in the view
        mRadarPlugin.setMaxDistance(100);

        // We can customize the color of the items
        mRadarPlugin.setListColor(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, Color.RED);
        // and also the size
        mRadarPlugin.setListDotRadius(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, 3);
    }

    public static void updateTextValues() {
        mTextValues.setText(" push away from me=" + mBeyondarFragment.getPushAwayDistance() + "m" + "\r\n" +
                " Max Render distance=" + mBeyondarFragment.getMaxDistanceToRender() + "m" + "\r\n" +
                " Radar Distance=" + mRadarPlugin.getMaxDistance() +"m");
        mGPStextValues.setText("latitude=" + CustomHelperClass.latitude + "\r\n" + "longitude=" + CustomHelperClass.longitude);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mRadarPlugin == null)
            return;
        if (seekBar == mSeekBarMaxDistance) { // Radar Progress Bar Change
            // float value = ((float) progress/(float) 10000);
            mTextviewMaxDistance.setText("Radar Distance: " + progress +"m");
            mRadarPlugin.setMaxDistance(progress);
        }
        else if (seekBar == mSeekBarMaxDistanceToRender) { // Render Progress Bar Change
            mArViewDistanceText.setText("Max Render Distance : " + progress + "m");
            mBeyondarFragment.setMaxDistanceToRender(progress);
        }
        else if (seekBar == mSeekBarPushAwayDistance) {
            mMinFarText.setText("Push away from me: " + progress + "m");
            mBeyondarFragment.setPushAwayDistance(progress);
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
            Intent intent = new Intent(this, CustomMapActivity.class);
            startActivity(intent);
        }
        if (v == mNavigation) {
            Intent intent = new Intent(this, CustomSearchActivity.class);
            startActivity(intent);
        }
        if (v == detailClose) {
            slidingPaneLayout.closePane();
        }
    }
//
    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) { // when users click objects
         String name ;

        slidingPaneLayout = (CustomSlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
        slidingPaneLayout.openPane();
        imageName = (ImageView)findViewById(R.id.image_name);
        detailName = (TextView) findViewById(R.id.detail_name);

        if (beyondarObjects.size() > 0) {
            Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(),
                    Toast.LENGTH_LONG).show();

            for (int i=0;i<USCMapJSONLocal.buildingArray.size();i++) {
                if(USCMapJSONLocal.buildingArray.get(i).getmId()== beyondarObjects.get(0).getName()){
                   name = USCMapJSONLocal.buildingArray.get(i).getmName();
                    detailName.setText(name);

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

                    ImageView img = (ImageView) findViewById((R.id.image_name));
                    new DownloadImageTask(img) // download image from url
                            .execute("http://web-app.usc.edu/venues/ACC.jpg");
                }
            }
            //detailName.setText(beyondarObjects.get(0).getName());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mBeyondarFragment.onPause();
        CustomHelperClass.Pause();
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mBeyondarFragment.onResume();
        CustomHelperClass.Resume();
    }
}
