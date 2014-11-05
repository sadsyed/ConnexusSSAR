package ssar.apt.connexusssar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "ssar.apt.connexusssar.MESSAGE";
    ConnexusLocationService locationservice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationservice = new ConnexusLocationService(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /** Called when the user clicks the View Streams button */
    public void viewStreams(View view) {
        Intent intent = new Intent(this, ViewStreamsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.viewStreamsButton);
        intent.putExtra(EXTRA_MESSAGE, "View Streams Activity Test");
        startActivity(intent);
    }

    /** Called whent the user clicks the Use Canera button */
    public void onUseCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Camera Activity Test");
        startActivity(intent);
    }
}

