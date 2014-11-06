package ssar.apt.connexusssar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * Created by Amy on 10/26/2014.
 */
public class ConnexusLocationService {

    LocationManager locationManager;
    LocationListener locationListener;
    public static double[] lastLocation = new double[2];
    Context mContext;
    private static final String TAG = ConnexusLocationService.class.getSimpleName();

    public ConnexusLocationService(Context mContext) {
        this.mContext = mContext;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                saveAppLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        Log.i(TAG,"Created location listener.");

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 100, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 100, locationListener);
        Log.i(TAG,"Registered listeners for location.");
    }

    public static double[] getGPS(Context mContext) {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            Log.i(TAG, "Looping over providers.");
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            Log.i(TAG, "l is not equal null.");
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        } else {
            Log.i(TAG, "l is equal to null.");
        }
        return gps;
    }

    public void saveAppLocation(Location location) {
        double latitude = 0.0;
        double longitude = 0.0;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(TAG,"Saving location: " + Double.toString(latitude) + ", " + Double.toString(longitude));
    }
}
