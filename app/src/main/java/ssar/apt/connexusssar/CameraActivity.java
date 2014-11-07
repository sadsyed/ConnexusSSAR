package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import ssar.apt.connexusssar.util.ConnexusFileService;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {
    private static final String CLASSNAME = CameraActivity.class.getName();
    private ConnexusCameraUploadRequestReceiver uploadRequestReceiver;
    private SurfaceView mSurfaceView;
    double[] location;
    Intent intent;
    IntentFilter filter;
    String imagefile;
    String streamname;
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        streamname = intent.getStringExtra("Streamname");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        mCamera = Camera.open();
    }

    public void onTakeAPicture(View view) {
        mCamera.takePicture(this, null, null, this);
    }

    public Bitmap onUseThisPicture(View view) {
        Bitmap bitmap = null;
        try {
            if(uploadRequestReceiver != null) {
                try {
                    this.unregisterReceiver(uploadRequestReceiver);
                } catch (IllegalArgumentException e){
                    Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
                }
            }
            FileInputStream fileInputStream = openFileInput("picture.jpg");
            //get the image
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "name for bitmap is: " + today.toString().substring(0, 14));
            Uri tmpPath = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, today.toString().substring(0,14) , "Connexussssar Pic"));
            imagefile = ConnexusFileService.getRealPathFromURI(tmpPath, this);
            Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "The image file uri is: " + imagefile);
            fileInputStream.close();
            uploadFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void uploadFile() {

        filter = new IntentFilter(ConnexusCameraUploadRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        uploadRequestReceiver = new ConnexusCameraUploadRequestReceiver(ConnexusSSARConstants.UPLOAD_FILE);
        registerReceiver(uploadRequestReceiver, filter);

        Intent msgIntent = new Intent(CameraActivity.this, ConnexusIntentService.class);
        msgIntent.putExtra(ConnexusIntentService.REQUEST_URL, ConnexusSSARConstants.UPLOAD_FILE);
        msgIntent.putExtra("Streamname", streamname);
        msgIntent.putExtra("ImagePath", imagefile);
        location = ConnexusLocationService.getGPS(this);
        msgIntent.putExtra("latitude", String.valueOf(location[0]));
        msgIntent.putExtra("longitude", String.valueOf(location[1]));
        startService(msgIntent);
    }

    public void onStreams(View view) {
        Intent intent = new Intent(this, ViewStreamsActivity.class);
        startActivity(intent);
    }

    //Camera Callback Methods
    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("picture.jpg", Activity.MODE_PRIVATE);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
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
    public void onPause () {
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onPause();
        mCamera.stopPreview();
    }

    public void onResume() {
        if(uploadRequestReceiver != null) {
            this.registerReceiver(uploadRequestReceiver, filter);
        }
        super.onResume();

    }

    @Override
    public void onDestroy() {
        if(uploadRequestReceiver != null) {
            try {
                this.unregisterReceiver(uploadRequestReceiver);
            } catch (IllegalArgumentException e){
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
        super.onDestroy();
        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();

        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width, selected.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceDestroyed");
    }

    public class ConnexusCameraUploadRequestReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "ssar.apt.intent.action";
        private String serviceUrl;

        public ConnexusCameraUploadRequestReceiver (String serviceUrl) {
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
                Log.i(CLASSNAME, "Exception creating json object: " + e.getMessage());
            }
            try {
                String imageFileUrl = (String)json.get("file");
                Log.i(CLASSNAME,"Files successfully uploaded: " + imageFileUrl);
            } catch (JSONException exception) {
                Log.i(CLASSNAME,"File upload failed.");
            }
        }
    }
}
