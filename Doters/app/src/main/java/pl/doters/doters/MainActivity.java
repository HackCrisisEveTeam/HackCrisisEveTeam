package pl.doters.doters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
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


    private static final String CHANNEL_ID = "range_notifications";
    private static final String CHANNEL_NAME = "Range Notifications";
    private static final String CHANNEL_DESC = "Closing to Border Notifications";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.test_layout);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String id = settings.getString("id", null);

        if (id != null) {
            permissions = checkPermissions();
            marshmallowGPSPremissionCheck();
            if (permissions) {
                Log.i(TAG, "You Got The Permissions!");
                getLocation();
                if (googleServicesAvailable()) {
                    setContentView(R.layout.activity_main);
                    tvLat = findViewById(R.id.latitude);
                    tvLon = findViewById(R.id.longitude);
                    initMap();
                } else {
                    Log.e(TAG, "Nie Dostępne Usługi Google");
                }

            } else {
                Log.e(TAG, "Brak Pozwoleń na Lokalizacje !");
                onCreate(savedInstanceState);
            }
        } else {
            //Blok Odpowiedzialny za zbieranie info
            Log.i(TAG, "onCreate: Trzeba Zebrać Dane");
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
    @SuppressLint("MissingPermission")
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
        drawCircles();
    }
    private void drawCircles() {
        mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(cLatitude, cLongitude))
                .radius(25)
                .strokeColor(Color.RED));
                //.fillColor(R.color.transparentRed));
        mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(cLatitude, cLongitude))
                .radius(10)
                .strokeColor(Color.YELLOW));
                //.fillColor(R.color.transparentYellow));
    }
    private void setUpMap() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mGoogleMap.setMyLocationEnabled(true);
    }
    private void goToLocationZoom() {
        LatLng ll = new LatLng(uLatitude, uLongitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) 19);
        mGoogleMap.moveCamera(update);
    }
    @SuppressLint("SetTextI18n")
    private void UpdateLocation() {
        tvLon.setText("LongiTude : " + uLongitude);
        tvLat.setText("Latitude : " + uLatitude);
        //noinspection deprecation
        mGoogleMap.setOnMyLocationChangeListener(location -> {
            uLatitude = location.getLatitude();
            uLongitude = location.getLongitude();

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(uLatitude, uLongitude));
            tvLon.setText("LongiTude : " + uLongitude);
            tvLat.setText("Latitude : " + uLatitude);

            double resetDist = 25.000000000000;
            double warnDist = 10.000000000000;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(CHANNEL_DESC);
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            String mTitle, mContent;

            if(distFromHome(uLatitude, uLongitude) >= resetDist) {
                Log.i(TAG, "Aktywowano resetowanie punktów");
                mTitle = "Oddaliłeś się z Domu.";
                mContent = "Twoje zdobyte dzisiaj punkty Zostały Zresetowane.";
                displayNotification(mTitle, mContent);
            } else if (distFromHome(uLatitude, uLongitude) >= warnDist) {
                Log.i(TAG, "Aktywowano Ostrzeżenie");
                mTitle = "Oddalasz się od Domu";
                mContent = "Twoje Punkty z Dzisiaj zostaną zresetowane jeżeli oddalisz się za bardzo.";
                displayNotification(mTitle, mContent);
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
    private void displayNotification(String mTitle, String mContent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_info)
                        .setContentTitle(mTitle)
                        .setContentText(mContent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, mBuilder.build());

    }
}
