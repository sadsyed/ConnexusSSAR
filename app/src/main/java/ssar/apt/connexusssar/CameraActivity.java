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

import java.util.List;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private static final String CLASSNAME = CameraActivity.class.getName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private LayoutInflater mInflater;
    static Camera mCamera = null;
    byte[] tempData;
    boolean mPreviewRunning;
    private ImageView imgPreview;
    private Button takePictureButton;
    private Uri fileUri;

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imgPreview = (ImageView) findViewById(R.id.imageView);
        takePictureButton = (Button) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support camera", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        previewCapturedImage();
    }

    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imgPreview.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    //Surface Callback Methods
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();

        //get the device's supported sizes and pick the first which is the largest
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width, selected.height);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mInflater = LayoutInflater.from(this);
        View overView = mInflater.inflate(R.layout.camera_overlay_layout, null);
        this.addContentView(overView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
            }
        });
    }

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {}
    };

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {}
    };

    Camera.PictureCallback mjpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {
                tempData = data;
                done();
            }
        }
    };

    void done() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(tempData, 0 , tempData.length);
        String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
        bitmap.recycle();;
        Bundle bundle = new Bundle();
        if (url != null) {
            bundle.putString("url", url);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Picture cannot be saved", Toast.LENGTH_SHORT).show();
        }
        finish();
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
        mCamera.stopPreview();
        mCamera.release();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mCamera.stopPreview();
        mCamera.release();
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceChanged");
        try {
            if (mPreviewRunning) {
                mCamera.stopPreview();
                mPreviewRunning = false;
            }

            Camera.Parameters p = mCamera.getParameters();
            p.setPreviewSize(w, h);

            mCamera.setParameters(p);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mPreviewRunning = true;
        } catch (Exception e) {
            Log.d("", e.toString());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceCreated");
        mCamera = Camera.open();
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": surfaceDestroyed");
        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();
        mCamera = null;
    }
}
