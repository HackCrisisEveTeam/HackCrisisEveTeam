package pl.doters.doters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    /**
     * TAG for Logs
     */
    private static final String TAG = "MyActivity";
    /**
     * uLatitude - Current user Latitude
     * uLongitude - Current user Longitude
     * cLatitude - center of GeoFence Latitude
     * cLongitude - center of GeoFence Longitude
     */
    double uLatitude, uLongitude, cLatitude, cLongitude;
    boolean permissions = false;
    int MY_PERMISSION_LOCATION = 10;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions = checkPermissions();
        marshmallowGPSPremissionCheck();
        if (permissions) {
            Log.i(TAG, "You Got The Permissions!");
            getLocation();

            TextView tvLat = findViewById(R.id.latitude);
            TextView tvLon = findViewById(R.id.longitude);

            tvLat.setText("Latitude : " + uLatitude);
            tvLon.setText("Longitude : " + uLongitude);
        } else {
            Log.e(TAG, "Brak Pozwoleń na Lokalizacje !");
            onCreate(savedInstanceState);
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (permissions) {
            if (lm != null) {
                @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    uLatitude = location.getLatitude();
                    uLongitude = location.getLongitude();
                    cLatitude = location.getLatitude();
                    cLongitude = location.getLongitude();
                }
            }
        }
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission( android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions( new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSION_LOCATION );
            Log.i(TAG, "Checking Permissions!");
            marshmallowGPSPremissionCheck();
        } else {
            permissions = true;
            Log.i(TAG, "Permissions Granted!");
        }
    }

}
