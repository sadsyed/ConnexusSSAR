package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.StreamImage;
import ssar.apt.connexusssar.types.StreamImageAdapter;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;
import ssar.apt.connexusssar.types.StreamAdapater;
import ssar.apt.connexusssar.types.Stream;




public class ViewAStreamActivity extends Activity {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private ConnexusViewAStreamRequestReceiver requestReceiver;
    private ConnexusViewAStreamRequestReceiver subscribeRequestReceiver;
    IntentFilter filter;

    GridView gridView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_astream);
        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting View a stream");
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Stream is: " + message);
        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            //TODO: Remove the hardcoded userid
            requestJSON.put("streamname", message);
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Created json");
        } catch (Exception e) {
            Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Exception while creating an request JSON.");
        }

        setContentView(R.layout.activity_view_astream);

        filter = new IntentFilter(ConnexusViewAStreamRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        requestReceiver = new ConnexusViewAStreamRequestReceiver(ConnexusSSARConstants.VIEW_ASTREAM);
        registerReceiver(requestReceiver, filter);

        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting ViewAStreams request");
        Intent msgIntent = new Intent(ViewAStreamActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.VIEW_ASTREAM);
        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "JSON sent is: " + requestJSON.toString());
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_astream, menu);
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

    @Override
    public void onDestroy() {
        //this.unregisterReceiver(requestReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        this.unregisterReceiver(requestReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        this.registerReceiver(requestReceiver, filter);
        super.onResume();
    }
    public class ConnexusViewAStreamRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public ConnexusViewAStreamRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseJSON = intent.getStringExtra(ConnexusIntentService.RESPONSE_JSON);
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Service response JSON: " + responseJSON);

            //TextView jsonObjectTextView = (TextView) findViewById(R.id.viewStreamTitle);
            //jsonObjectTextView.setText(responseJSON);

            //List<Stream> allStreams = streamParser.jsonToStream(serviceUrl, responseJSON);
            Stream stream = streamParser.jsonToSingleStream(serviceUrl, responseJSON);
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Stream received: " + stream.toString());
            List<StreamImage> myImages = stream.getStreamImageList();
            List<StreamImage> shortMyImages = new ArrayList<StreamImage>();
            if (myImages.size() > 0) {
                int counter = 0;
                for(StreamImage streamImageItem : myImages) {
                    if (counter < 16) {
                        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, String.valueOf(counter) + ": " + streamImageItem.toString());
                        shortMyImages.add(streamImageItem);
                    }
                    counter ++;
                }
            }


            setContentView(R.layout.activity_view_astream);
            gridView = (GridView) findViewById(R.id.viewAStreamGridView);
            //gridView.setAdapter(new CustomAdapter(context, listOfStreamNames, listOfImages));
            gridView.setAdapter(new StreamImageAdapter(context, shortMyImages));
        }
    }
}
