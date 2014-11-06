package ssar.apt.connexusssar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "ssar.apt.connexusssar.MESSAGE";
    private static String TAG = MainActivity.class.getSimpleName();
    ConnexusLocationService locationservice;
    Context mContext = MainActivity.this;
    AccountManager mAccountManager;
    String token;
    int serverCode;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationservice = new ConnexusLocationService(this);
        syncGoogleAccount();
    }

    public void syncGoogleAccount() {
        if (isNetworkAvailable() == true) {
            String[] accountarrs = getAccountNames();
            if (accountarrs.length > 0) {
                //you can set here account for login
                Log.i(TAG, "First account is: " + accountarrs[0]);
                Log.i(TAG, "The account array size is: " + Integer.toString(accountarrs.length));
                try {
                   // new GetTokenTask().execute("aimers1975@gmail.com", mContext);

                } catch (Exception e) {
                    Log.i(TAG, "The exception is: " + e.getMessage());
                }
            } else {
                Toast.makeText(MainActivity.this, "No Google Account Sync!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Network Service!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getAccountNames() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
                .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;
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

    public void login(View view) {
        Log.i(TAG, "Login clicked");
    }

    /** Called when the user clicks the View Streams button */
    public void viewStreams(View view) {
        Intent intent = new Intent(this, ViewStreamsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.viewStreamsButton);
        intent.putExtra(EXTRA_MESSAGE, "View Streams Activity Test");
        startActivity(intent);
    }
    /*private class GetTokenTask extends AsyncTask<String, void, void> {
        protected Long doInBackground(String email) {
            try {
                GoogleAuthUtil.getToken(mContext,"aimers1975@gmail.com",SCOPE);
            } catch (Exception e) {
                Log.i(TAG, "Exception getting token: " + e.getMessage());
            }
        }

        protected void onProgressUpdate() {

        }

        protected void onPostExecute() {

        }
    }*/

    /** Called whent the user clicks the Use Canera button */
    public void onUseCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Camera Activity Test");
        startActivity(intent);
    }
}

