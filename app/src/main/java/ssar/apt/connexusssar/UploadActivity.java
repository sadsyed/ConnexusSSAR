package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import ssar.apt.connexusssar.util.ConnexusFileService;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class UploadActivity extends Activity {
    private static final String TAG = UploadActivity.class.getSimpleName();
    public final static String EXTRA_MESSAGE = "ssar.apt.connexusssar.MESSAGE";
    private ConnexusUploadRequestReceiver uploadRequestReceiver;
    String streamname = "";
    IntentFilter filter;
    String imagePath;
    String comments;
    double[] location;
    Uri selectedimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        streamname = intent.getStringExtra("Streamname");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);
        TextView currentStreamTextView = (TextView) findViewById(R.id.currentStream);
        currentStreamTextView.setText("View A Stream: " + streamname);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(uploadRequestReceiver != null) {
            this.registerReceiver(uploadRequestReceiver, filter);
        }
        super.onResume();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_CANCELED)
        {
            // action cancelled
        }
        if(resultCode==RESULT_OK) {
            selectedimg = data.getData();
            imagePath = ConnexusFileService.getRealPathFromURI(selectedimg, this);
            Log.i(TAG, "Selected img path is: " + imagePath);
            location = ConnexusLocationService.getGPS(this);
            Button uploadButton = (Button) findViewById(R.id.uploadButton);
            uploadButton.setEnabled(true);
        }
    }

    public void chooseImageFile(View view) {
        //Select file
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentChooser, "Choose Picture"), 1);
    }

    /** Called when the user clicks the Use Camera button */
    public void onUseCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Camera Activity Test");
        startActivity(intent);
    }

    public void uploadFile(View view) {

        filter = new IntentFilter(ConnexusUploadRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        uploadRequestReceiver = new ConnexusUploadRequestReceiver(ConnexusSSARConstants.UPLOAD_FILE);
        registerReceiver(uploadRequestReceiver, filter);

        Intent msgIntent = new Intent(UploadActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.UPLOAD_FILE);
        msgIntent.putExtra("Streamname", streamname);
        msgIntent.putExtra("ImagePath", imagePath);
        msgIntent.putExtra("latitude", String.valueOf(location[0]));
        msgIntent.putExtra("longitude", String.valueOf(location[1]));
        startService(msgIntent);
    }

    public class ConnexusUploadRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public ConnexusUploadRequestReceiver (String serviceUrl) {
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
            try {
                String imageFileUrl = (String)json.get("file");
                Log.i(TAG,"Files successfully uploaded");
                intent = new Intent(context, ViewAStreamActivity.class);
                intent.putExtra(EXTRA_MESSAGE,streamname);
                context.startActivity(intent);
            } catch (JSONException exception) {
                Log.i(TAG,"File upload failed.");
            }
        }
    }
}
