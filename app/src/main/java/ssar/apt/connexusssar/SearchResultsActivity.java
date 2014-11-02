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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.types.StreamAdapater;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;


public class SearchResultsActivity extends Activity {
    public static final String CLASSNAME = SearchResultsActivity.class.getName();
    public static final String SEARCH_QUERY = "searchQuery";

    private StreamParser streamParser = new StreamParser();
    private SearchRequestReceiver searchRequestReceiver;

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        String searchQuery = intent.getStringExtra(SEARCH_QUERY);

        EditText searchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);
        searchQueryEditText.setText(searchQuery);

        searchStreams(searchQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
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

    public void loadSearchResults(View view) {
        EditText searchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);
        String searchQuery = searchQueryEditText.getText().toString();

        searchStreams(searchQuery);
    }

    private void searchStreams(String searchQuery) {
        IntentFilter filter = new IntentFilter(SearchRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory((Intent.CATEGORY_DEFAULT));
        searchRequestReceiver = new SearchRequestReceiver(ConnexusSSARConstants.SEARCH_STREAM);
        registerReceiver(searchRequestReceiver, filter);

        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("streamname", searchQuery);
        } catch (Exception e) {
            Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Exception while creating a search request JSON object.");
        }

        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Starting SearchStreams request");
        Intent msgIntent = new Intent(SearchResultsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.SEARCH_STREAM);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);
    }

    public class SearchRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public SearchRequestReceiver (String serviceUrl) {
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
                if(streamCounter < 8) {
                    Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": " + String.valueOf(streamCounter) + ": " + streamItem.toString());
                    streams.add(streamItem);
                }
                streamCounter++;
            }

            setContentView(R.layout.activity_search_results);
            gridView = (GridView) findViewById(R.id.searchStreamsGridView);
            //gridView.setAdapter(new CustomAdapter(context, listOfStreamNames, listOfImages));
            gridView.setAdapter(new StreamAdapater(context, streams));
        }
    }
}
