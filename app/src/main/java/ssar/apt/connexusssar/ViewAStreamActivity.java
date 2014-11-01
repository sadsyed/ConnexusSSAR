package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.GridView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.StreamImage;
import ssar.apt.connexusssar.types.StreamImageAdapter;
import ssar.apt.connexusssar.util.ConnexusFileService;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;
import ssar.apt.connexusssar.util.StreamParser;
import ssar.apt.connexusssar.types.Stream;

public class ViewAStreamActivity extends Activity {
    private static final String TAG = ViewAStreamActivity.class.getSimpleName();
    private StreamParser streamParser = new StreamParser();
    private ConnexusViewAStreamRequestReceiver requestReceiver;
    private ConnexusViewAStreamRequestReceiver redrawRequestReceiver;
    private ConnexusViewAStreamRequestReceiver uploadRequestReceiver;
    private String streamname = "";
    IntentFilter filter;

    GridView gridView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_astream);
        Log.i(TAG, "Starting View a stream");
        Intent intent = getIntent();
        streamname = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.i(TAG, "Stream is: " + streamname);
        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("streamname", streamname);
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating an request JSON.");
        }

        setContentView(R.layout.activity_view_astream);
        double[] myLocation = ConnexusLocationService.getGPS(this);
        for(double val : myLocation) {
            Log.i(TAG, "Location is: " + Double.toString(val));
        }

        filter = new IntentFilter(ConnexusViewAStreamRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        requestReceiver = new ConnexusViewAStreamRequestReceiver(ConnexusSSARConstants.VIEW_ASTREAM);
        registerReceiver(requestReceiver, filter);

        Intent msgIntent = new Intent(ViewAStreamActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.VIEW_ASTREAM);
        Log.i(TAG, "JSON sent is: " + requestJSON.toString());
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
        if(requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(redrawRequestReceiver != null) {
            try {
                this.unregisterReceiver(redrawRequestReceiver);
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
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        if(redrawRequestReceiver != null) {
            try {
                this.unregisterReceiver(redrawRequestReceiver);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Exception unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(requestReceiver != null) {
            this.registerReceiver(requestReceiver, filter);
        }
        if(uploadRequestReceiver != null) {
            this.registerReceiver(uploadRequestReceiver, filter);
        }
        if(redrawRequestReceiver != null) {
            this.registerReceiver(redrawRequestReceiver, filter);
        }
        super.onResume();
    }

    /** Called when the user clicks the View Streams button */
    public void UploadImageFile(View view) {
        //Select file
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentChooser, "Choose Picture"), 1);
    }

    protected void redrawStreams() {
        Intent intent = getIntent();
        Log.i(TAG, "Redrawing streams for stream: " + streamname);
        //set the JSON request object
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("streamname", streamname);
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating an request JSON.");
        }

        filter = new IntentFilter(ConnexusViewAStreamRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        if (redrawRequestReceiver != null) {
            try {
                this.unregisterReceiver(redrawRequestReceiver);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Exception unregistering receiver: " + e.getMessage());
            }
        }
        redrawRequestReceiver = new ConnexusViewAStreamRequestReceiver(ConnexusSSARConstants.VIEW_ASTREAM);
        registerReceiver(redrawRequestReceiver, filter);

        Intent msgIntent = new Intent(ViewAStreamActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.VIEW_ASTREAM);
        Log.i(TAG, "JSON sent is: " + requestJSON.toString());
        msgIntent.putExtra(ConnexusIntentService.REQUEST_JSON, requestJSON.toString());
        startService(msgIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_CANCELED)
        {
            // action cancelled
        }
        if(resultCode==RESULT_OK)
        {
            Uri selectedimg = data.getData();
            String imagePath = ConnexusFileService.getRealPathFromURI(selectedimg, this);
            Log.i(TAG, "Selected img path is: " + imagePath);

            Intent intent = new Intent(this, ViewAStreamActivity.class);
            filter = new IntentFilter(ConnexusViewAStreamRequestReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            uploadRequestReceiver = new ConnexusViewAStreamRequestReceiver(ConnexusSSARConstants.UPLOAD_FILE);
            registerReceiver(uploadRequestReceiver, filter);

            Intent msgIntent = new Intent(ViewAStreamActivity.this, ConnexusIntentService.class);
            msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.UPLOAD_FILE);
            msgIntent.putExtra("Streamname", streamname);
            msgIntent.putExtra("ImagePath", imagePath);
            startService(msgIntent);
        }
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
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(responseJSON);
            } catch (JSONException e)
            {
                Log.i(TAG, "Exception creating json object: " + e.getMessage());
            }
            Log.i(TAG, "Service response JSON: " + responseJSON);
            try {
                Stream stream = streamParser.jsonToSingleStream(serviceUrl, responseJSON);
                Log.i(TAG, "Stream received: " + stream.toString());
                List<StreamImage> myImages = stream.getStreamImageList();
                List<StreamImage> shortMyImages = new ArrayList<StreamImage>();
                if (myImages.size() > 0) {
                    int counter = 0;
                    for (StreamImage streamImageItem : myImages) {
                        if (counter < 16) {
                            Log.i(TAG, String.valueOf(counter) + ": " + streamImageItem.toString());
                            shortMyImages.add(streamImageItem);
                        }
                        counter++;
                    }
                }
                setContentView(R.layout.activity_view_astream);
                gridView = (GridView) findViewById(R.id.viewAStreamGridView);
                gridView.setAdapter(new StreamImageAdapter(context, shortMyImages));
            } catch (Exception e) {
                Log.i(TAG,"This was a upload image request. Trying to redraw streams.");
                try {
                    String imageFileUrl = (String)json.get("file");
                    Log.i(TAG,"Files list was greater than 0, redrawing streams.");
                    if (imageFileUrl != null) {
                        redrawStreams();
                    }
                } catch (JSONException exception) {
                    Log.i(TAG,"Could not get any files from response.");
                }
            }
        }
    }
}
