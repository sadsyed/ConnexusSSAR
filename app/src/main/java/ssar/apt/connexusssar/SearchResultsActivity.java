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
    private SearchRequestReceiver initRequestReceiver;
    private SearchRequestReceiver loadRequestReceiver;
    private SearchRequestReceiver redrawRequestReceiver;
    private IntentFilter filter;

    private String searchQuery;
    private List<Stream> allStreams = new ArrayList<Stream>();
    private int displayPicStart = 0;
    private int displayPicEnd = 8;

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        searchQuery = intent.getStringExtra(SEARCH_QUERY);

        EditText searchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);
        searchQueryEditText.setText(searchQuery);

        initRequestReceiver = new SearchRequestReceiver(ConnexusSSARConstants.SEARCH_STREAM);
        performSearchRequest(initRequestReceiver);
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

    @Override
    public void onDestroy() {
        unregisterSearchRequestReceiver();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        unregisterSearchRequestReceiver();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (initRequestReceiver != null) {
            this.registerReceiver(initRequestReceiver, filter);
        }
        if (loadRequestReceiver != null) {
            this.registerReceiver(loadRequestReceiver, filter);
        }
        if (redrawRequestReceiver != null) {
            this.registerReceiver(redrawRequestReceiver, filter);
        }
        super.onResume();
    }

    private void unregisterSearchRequestReceiver () {
        if(initRequestReceiver != null) {
            try {
                this.unregisterReceiver(initRequestReceiver);
            } catch (IllegalArgumentException e) {
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(loadRequestReceiver != null) {
            try {
                this.unregisterReceiver(loadRequestReceiver);
            } catch (IllegalArgumentException e) {
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(redrawRequestReceiver != null) {
            try {
                this.unregisterReceiver(redrawRequestReceiver);
            } catch (IllegalArgumentException e) {
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
    }

    public void loadSearchResults(View view) {
        EditText searchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);
        searchQuery = searchQueryEditText.getText().toString();

        loadRequestReceiver = new SearchRequestReceiver(ConnexusSSARConstants.SEARCH_STREAM);
        performSearchRequest(loadRequestReceiver);
    }

    public void moreSearchResults(View view) {
        if(displayPicEnd < allStreams.size()){
            displayPicStart = displayPicEnd;
            displayPicEnd = displayPicStart + 8;
        } else {
            displayPicStart = 0;
            displayPicEnd = 8;
        }

        redrawStreams();
    }

    protected void redrawStreams() {
        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": Redrawing streams for stream: " + searchQuery);
        redrawRequestReceiver = new SearchRequestReceiver(ConnexusSSARConstants.SEARCH_STREAM);
        performSearchRequest(redrawRequestReceiver);
    }

    private void performSearchRequest(SearchRequestReceiver searchRequestReceiver) {
        filter = new IntentFilter(SearchRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory((Intent.CATEGORY_DEFAULT));

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
            List<Stream> streams = new ArrayList<Stream>();

            allStreams = streamParser.jsonToStream(serviceUrl, responseJSON);

            //truncate streams to 8 streams
            if(allStreams.size() > 0) {
                int streamCounter = 0;
                for (Stream streamItem : allStreams) {
                    if (streamCounter >= displayPicStart && streamCounter < displayPicEnd) {
                        Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": " + String.valueOf(streamCounter) + ": " + streamItem.toString());
                        streams.add(streamItem);
                    }
                    streamCounter++;
                }
            }

            setContentView(R.layout.activity_search_results);

            EditText searchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);
            searchQueryEditText.setText(searchQuery);

            EditText resultsInfoEditText = (EditText) findViewById(R.id.resultsInfoEditText);
            resultsInfoEditText.setText(allStreams.size() + " results for " + searchQuery +". Click on an image to view stream");

            gridView = (GridView) findViewById(R.id.searchStreamsGridView);
            gridView.setAdapter(new StreamAdapater(context, streams));
        }
    }
}
