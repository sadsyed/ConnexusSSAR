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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.util.StreamParser;


public class ViewStreamsActivity extends Activity {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private ConnexusRequestReceiver requestReceiver;

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

        IntentFilter filter = new IntentFilter(ConnexusRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        requestReceiver = new ConnexusRequestReceiver();
        registerReceiver(requestReceiver, filter);

        Log.i(TAG, "Starting ViewAllStreams request");
        Intent msgIntent = new Intent(ViewStreamsActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, "http://sonic-fiber-734.appspot.com/ViewAllStreamsService");
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
        this.unregisterReceiver(requestReceiver);
        super.onDestroy();
    }

    public class ConnexusRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseJSON = intent.getStringExtra(ConnexusIntentService.RESPONSE_JSON);
            Log.i(TAG, responseJSON);

            TextView jsonObjectTextView = (TextView) findViewById(R.id.jsonObjectTextView);
            jsonObjectTextView.setText(responseJSON);

            List<Stream> streams = streamParser.jsonToStream(responseJSON);

            for (Stream stream : streams){
                Log.i(TAG, stream.toString());
            }
        }
    }
}
