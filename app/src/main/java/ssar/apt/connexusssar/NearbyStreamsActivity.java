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
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;


public class NearbyStreamsActivity extends Activity {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private NearbyStreamsRequestReceiver nbsRequestReceiver;
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

        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting Nearby Streams request");
        Intent msgIntent = new Intent(NearbyStreamsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.NEARBY_STREAMS);
        Log.i(TAG, "JSON sent is: " + requestJSON.toString());
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);
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
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Service response JSON: " + responseJSON);

            List<Stream> allStreams = streamParser.jsonToStream(serviceUrl, responseJSON);

            //truncate streams to 16 streams
            List<Stream> streams = new ArrayList<Stream>();
            int streamCounter = 0;
            for (Stream streamItem : allStreams) {
                if(streamCounter < 16) {
                    Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, String.valueOf(streamCounter) + ": " + streamItem.toString());
                    streams.add(streamItem);
                }
                streamCounter++;
            }

            setContentView(R.layout.activity_nearby_streams);
            gridView = (GridView) findViewById(R.id.nearbyStreamsGridView);
            //gridView.setAdapter(new CustomAdapter(context, listOfStreamNames, listOfImages));
            gridView.setAdapter(new StreamAdapater(context, streams));
        }
    }
}
