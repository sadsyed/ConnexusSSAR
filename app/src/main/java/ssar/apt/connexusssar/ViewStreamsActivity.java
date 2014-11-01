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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.StreamAdapater;
import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;


public class ViewStreamsActivity extends Activity {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private ConnexusRequestReceiver requestReceiver;
    private ConnexusRequestReceiver subscribeRequestReceiver;
    private IntentFilter filter;

    GridView gridView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the message from the Intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Display the message
/*           TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
*/
        //Set the text view as the activity layout
        //setContentView(textView);
        setContentView(R.layout.activity_view_streams);

        filter = new IntentFilter(ConnexusRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        requestReceiver = new ConnexusRequestReceiver(ConnexusSSARConstants.VIEW_ALL_STREAMS);
        registerReceiver(requestReceiver, filter);

        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting ViewAllStreams request");
        Intent msgIntent = new Intent(ViewStreamsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.VIEW_ALL_STREAMS);
        startService(msgIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_streams, menu);
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

    /** Called when the user clicks the My Subscribed Streams button */
    public void loadSubscribedStreams(View view) {
        unregisterReceiver(requestReceiver);
        IntentFilter filter = new IntentFilter(ConnexusRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        subscribeRequestReceiver = new ConnexusRequestReceiver(ConnexusSSARConstants.MANAGE_STREAM);
        registerReceiver(subscribeRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            //TODO: Remove the hardcoded userid
            requestJSON.put("userid", "sh.sadaf@gmail.com");
        } catch (Exception e) {
            Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Exception while creating an request JSON.");
        }

        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting ManageStreams request");
        Intent msgIntent = new Intent(ViewStreamsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.MANAGE_STREAM);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);
    }

    public void searchStreams(View view) {
        //get the find stream name
        EditText findStreamsEditText = (EditText) findViewById(R.id.findStreamsEditText);
        String streamQuery = findStreamsEditText.getText().toString();

        //launch searchResultsActivity
        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Launching SearchResults Activity");
        Intent searchResultActivityIntent = new Intent(this, SearchResultsActivity.class);
        searchResultActivityIntent.putExtra("streamQuery", streamQuery);
        this.startActivity(searchResultActivityIntent);
    }

    public class ConnexusRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public ConnexusRequestReceiver (String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseJSON = intent.getStringExtra(ConnexusIntentService.RESPONSE_JSON);
            //Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Service response JSON: " + responseJSON);

            TextView jsonObjectTextView = (TextView) findViewById(R.id.viewStreamTitle);
            jsonObjectTextView.setText(responseJSON);

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

            setContentView(R.layout.activity_view_streams);
            gridView = (GridView) findViewById(R.id.viewStreamGridView);
            //gridView.setAdapter(new CustomAdapter(context, listOfStreamNames, listOfImages));
            gridView.setAdapter(new StreamAdapater(context, streams));
        }
    }
}
