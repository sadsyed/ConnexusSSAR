package ssar.apt.connexusssar.util;

/**
 * Created by Amy on 11/7/2014.
 */

import android.util.Log;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;

/**
 * Created by Amy on 11/7/2014.
 */
public class UserStore {
    private static String TAG = ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG;
    private static UserStore singleton = null;
    private static boolean firstThread = true;

    //data to store
    private String username;
    private String password;

    public void setUserPassword(String user, String pass) {
        username = user;
        password = pass;
    }

    public String getUser() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    protected UserStore() {
        // Exists only to defeat instantiation.
    }
    public static UserStore getInstance() {
        if(singleton == null) {
            simulateRandomActivity();
            singleton = new UserStore();
            Log.i(TAG, "created singleton: " + singleton);
        }
        return singleton;
    }
    private static void simulateRandomActivity() {
        try {
            if(firstThread) {
                firstThread = false;
                Log.i(TAG, "sleeping...");
                // This nap should give the second thread enough time
                // to get by the first thread.
                Thread.sleep(50);
            }
        }
        catch(InterruptedException ex) {
            Log.w(TAG, "Sleep interrupted");
        }
    }

}

