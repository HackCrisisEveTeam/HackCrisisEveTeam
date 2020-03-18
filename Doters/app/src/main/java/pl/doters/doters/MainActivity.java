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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sqrt;

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

    private GoogleMap mGoogleMap;
    private TextView tvLon;
    private TextView tvLat;

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

            tvLat = findViewById(R.id.latitude);
            tvLon = findViewById(R.id.longitude);

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    private boolean googleServicesAvailable() {
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
    private void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (permissions) {
            if (lm != null) {
                @SuppressLint("MissingPermission") Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationGPS != null) {
                    uLatitude = locationGPS.getLatitude();
                    uLongitude = locationGPS.getLongitude();
                    cLatitude = locationGPS.getLatitude();
                    cLongitude = locationGPS.getLongitude();
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
        mGoogleMap = googleMap;
        setUpMap();
        goToLocationZoom();
        UpdateLocation();
    }
    private void setUpMap() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mGoogleMap.setMyLocationEnabled(true);
    }
    private void goToLocationZoom() {
        LatLng ll = new LatLng(uLatitude, uLongitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) 15);
        mGoogleMap.moveCamera(update);
    }
    @SuppressLint("SetTextI18n")
    private void UpdateLocation() {
        //noinspection deprecation
        mGoogleMap.setOnMyLocationChangeListener(location -> {
            uLatitude = location.getLatitude();
            uLongitude = location.getLongitude();

            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(uLatitude, uLongitude), 11);
            tvLon.setText("LongiTude : " + uLongitude);
            tvLat.setText("Latitude : " + uLatitude);

            double resetDist = 50.00000000;
            double warnDist = 25.00000000;

            if(distFromHome(uLatitude, uLongitude) >= resetDist) {
                Log.i(TAG, "Aktywowano resetowanie punktów");
            } else if (distFromHome(uLatitude, uLongitude) >= warnDist) {
                Log.i(TAG, "Aktywowano Ostrzeżenie");
            }

            mGoogleMap.moveCamera(center);
        });
    }
    private double distFromHome(double uLatitude, double uLongitude) {
        double x1 = cLatitude;
        double y1 = cLongitude;
        double tmp1 = 40075.704 / 360;
        double dist = sqrt( (uLatitude - x1) * (uLatitude - x1) + (cos((x1 * PI) / 180) * (uLongitude - y1) * (cos((x1 * PI) / 180) * (uLongitude - y1) )) );
        return dist * tmp1;
    }

}
