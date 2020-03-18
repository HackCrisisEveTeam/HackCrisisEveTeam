package pl.doters.doters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {

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
        setContentView(R.layout.test_layout);
        permissions = checkPermissions();
        marshmallowGPSPremissionCheck();
        if (permissions) {
            Log.i(TAG, "You Got The Permissions!");
            getLocation();

            TextView tvLat = findViewById(R.id.latitude);
            TextView tvLon = findViewById(R.id.longitude);

            tvLat.setText("Latitude : " + uLatitude);
            tvLon.setText("Longitude : " + uLongitude);

            if (googleServicesAvailable()) {
                setContentView(R.layout.activity_main);
                initMap();
            } else {
                Log.e(TAG, "Nie Dostępne Usługi Google");
            }

        } else {
            Log.e(TAG, "Brak Pozwoleń na Lokalizacje !");
            onCreate(savedInstanceState);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Log.e(TAG, "Can't connect to Google Play Services");
        }
        return false;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
