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

import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.StreamAdapater;
import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;
import ssar.apt.connexusssar.util.UserStore;


public class ViewStreamsActivity extends Activity {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private ConnexusRequestReceiver requestReceiver;
    private ConnexusRequestReceiver subscribeRequestReceiver;
    private IntentFilter filter;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

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

        Log.i(TAG, "Starting ViewAllStreams request");
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
        if(requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(subscribeRequestReceiver != null) {
            try {
                this.unregisterReceiver(subscribeRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(subscribeRequestReceiver != null) {
            try {
                this.unregisterReceiver(subscribeRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        this.registerReceiver(requestReceiver, filter);
        if(requestReceiver != null) {
            this.registerReceiver(requestReceiver, filter);
        }
        if(subscribeRequestReceiver != null) {
            this.registerReceiver(subscribeRequestReceiver, filter);
        }
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
            UserStore userStore = UserStore.getInstance();
            String tmpUser = userStore.getUser();
            Log.i(TAG, "The user is: " + tmpUser);
            if(tmpUser.equals("")) {
                Toast.makeText(ViewStreamsActivity.this, "You must be logged into google on this device to retrieve subscribed streams.",
                        Toast.LENGTH_SHORT).show();
            }
            requestJSON.put("userid", tmpUser);
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating an request JSON.");
        }

        Log.i(TAG, "Starting ManageStreams request");
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
        Log.i(TAG, "Launching SearchResults Activity");
        Intent searchResultActivityIntent = new Intent(this, SearchResultsActivity.class);
        searchResultActivityIntent.putExtra(SearchResultsActivity.SEARCH_QUERY, streamQuery);
        this.startActivity(searchResultActivityIntent);
    }

    public void nearbyStreams(View view) {
        Intent intent = new Intent(this, NearbyStreamsActivity.class);
        this.startActivity(intent);
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
            //Log.i(TAG, "Service response JSON: " + responseJSON);

            TextView jsonObjectTextView = (TextView) findViewById(R.id.viewStreamTitle);
            jsonObjectTextView.setText(responseJSON);

            List<Stream> allStreams = streamParser.jsonToStream(serviceUrl, responseJSON);

            //truncate streams to 16 streams
            List<Stream> streams = new ArrayList<Stream>();
            int streamCounter = 0;
            for (Stream streamItem : allStreams) {
                if(streamCounter < 16) {
                    Log.i(TAG, String.valueOf(streamCounter) + ": " + streamItem.toString());
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
