package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {
    private static final String CLASSNAME = CameraActivity.class.getName();

    private SurfaceView mSurfaceView;
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            FileInputStream fileInputStream = openFileInput("picture.jpg");
            //get the image
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
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
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
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
}
