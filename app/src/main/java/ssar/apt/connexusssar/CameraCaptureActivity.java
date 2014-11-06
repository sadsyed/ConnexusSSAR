package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class CameraCaptureActivity extends Activity {
    private static final String CLASSNAME = CameraCaptureActivity.class.getName();
    private static final int REQUEST_IMAGE = 100;

    Button takeAPictureButton;
    ImageView imageView;
    File destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        takeAPictureButton = (Button) findViewById(R.id.takeAPictureButton);
        takeAPictureButton.setOnClickListener(captureListener);

        imageView = (ImageView) findViewById(R.id.image);

        destination = new File(Environment.getExternalStorageDirectory(), "image.jpg");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_capture, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode  == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                FileInputStream fileInputStream = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;

                Bitmap userImage = BitmapFactory.decodeStream(fileInputStream, null, options);
                imageView.setImageBitmap(userImage);
                //Bitmap userImage = (Bitmap) data.getExtras().get("data");
                //imageView.setImageBitmap(userImage);

            } catch (Exception e) {
                Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": " + e.getMessage());
            }
        }
    }

    private View.OnClickListener captureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Add extra to save full-image somewhere
                intent.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, Uri.fromFile(destination));
                startActivityForResult(intent, REQUEST_IMAGE);
            } catch (ActivityNotFoundException e) {
                Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": " + e.getMessage());
            }
        }
    };
}
