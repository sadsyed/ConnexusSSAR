package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.types.StreamAdapater;
import ssar.apt.connexusssar.types.StreamImage;
import ssar.apt.connexusssar.types.StreamImageAdapter;
import ssar.apt.connexusssar.types.StreamImageAdapterClickable;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;


public class NearbyStreamsActivity extends Activity {
    private static final String TAG = NearbyStreamsActivity.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private NearbyStreamsRequestReceiver nbsRequestReceiver;
    List<StreamImage> myImages = null;
    private int displayPicStart = 0;
    private int displayPicEnd = 16;
    private IntentFilter filter;

    GridView gridView;
    Context context;
    double latitude = 0.0;
    double longitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_streams);
        double[] lat_long = ConnexusLocationService.getGPS(this);
        latitude = lat_long[0];
        longitude = lat_long[1];
        Log.i(TAG, "Latitude is: " + latitude + " Longitude is: " + longitude);
        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("latitude", latitude);
            requestJSON.put("longitude", longitude);
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating an request JSON.");
        }
        filter = new IntentFilter(NearbyStreamsRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        nbsRequestReceiver = new NearbyStreamsRequestReceiver(ConnexusSSARConstants.NEARBY_STREAMS);
        registerReceiver(nbsRequestReceiver, filter);

        Log.i(TAG, "Starting Nearby Streams request");
        Intent msgIntent = new Intent(NearbyStreamsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.NEARBY_STREAMS);
        Log.i(TAG, "JSON sent is: " + requestJSON.toString());
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);
    }

    @Override
    public void onDestroy() {
        if(nbsRequestReceiver != null) {
            try {
                this.unregisterReceiver(nbsRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(nbsRequestReceiver != null) {
            try {
                this.unregisterReceiver(nbsRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(nbsRequestReceiver != null) {
            this.registerReceiver(nbsRequestReceiver, filter);
        }
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nearby_streams, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewAllStreams(View view) {
        Intent intent = new Intent(this, ViewStreamsActivity.class);
        startActivity(intent);
    }

    public class NearbyStreamsRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public NearbyStreamsRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseJSON = intent.getStringExtra(ConnexusIntentService.RESPONSE_JSON);
            Log.i(TAG, "Service response JSON: " + responseJSON);
            myImages = streamParser.jsonToStreamImages(serviceUrl, responseJSON);
            List<StreamImage> shortMyImages = new ArrayList<StreamImage>();
            if (myImages.size() > 0) {
                int counter = 0;
                for (StreamImage streamImageItem : myImages) {
                    if (counter >= displayPicStart && counter < displayPicEnd) {
                        Log.i(TAG, String.valueOf(counter) + ": " + streamImageItem.toString());
                        shortMyImages.add(streamImageItem);
                    }
                    counter++;
                }
            }
            setContentView(R.layout.activity_nearby_streams);
            gridView = (GridView) findViewById(R.id.nearbyStreamsGridView);
            gridView.setAdapter(new StreamImageAdapterClickable(context, shortMyImages));
        }
    }
}
