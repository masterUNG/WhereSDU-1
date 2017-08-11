package sdu.alice.wheresdu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ServiceActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latADouble = 0, lngADouble = 0;
    private LocationManager locationManager;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_service_layout);

        //Setup MapFragmetn
        setupMapFragmetn();

        //Show Title
        showTitle();

        //Off Controller
        offController();

        //Setup For Get Location
        setupForGetLocation();

    }   // Main Method

    @Override
    protected void onResume() {
        super.onResume();

        myGetLocation();

    }

    private void myGetLocation() {

        locationManager.removeUpdates(locationListener);

        //For NetWork
        Location netWorkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (netWorkLocation != null) {
            latADouble = netWorkLocation.getLatitude();
            lngADouble = netWorkLocation.getLongitude();
        }


        //For GPS
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latADouble = gpsLocation.getLatitude();
            lngADouble = gpsLocation.getLongitude();
        }

        Log.d("11AugV1", "Lat ==> " + latADouble);
        Log.d("11AugV1", "Lng ==> " + lngADouble);

    }

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(locationListener);

    }

    public Location myFindLocation(String strProvider) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        }   //if

        return location;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latADouble = location.getLatitude();
            lngADouble = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void setupForGetLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
    }

    private void offController() {
        ImageView imageView = findViewById(R.id.imvOff);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showTitle() {
        TextView textView = findViewById(R.id.txtTitle);
        String strName = getIntent().getStringExtra("Name");
        textView.setText(strName);
    }

    private void setupMapFragmetn() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Check lat,lng != 0
        createMapAnEditLatLng();


    }   // onMapReady

    private void createMapAnEditLatLng() {

        //Create Map
        while (latADouble == 0) {
            myGetLocation();
        }   // while

        LatLng latLng = new LatLng(latADouble, lngADouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        //Edit LatLng

        String strID = getIntent().getStringExtra("ID");
        MyConstant myConstant = new MyConstant();
        String strURL = myConstant.getUrlEditLatLng();

        try {

            EditLatLng editLatLng = new EditLatLng(ServiceActivity.this);
            editLatLng.execute(strID,
                    Double.toString(latADouble),
                    Double.toString(lngADouble),
                    strURL);
            String result = editLatLng.get();
            Log.d("11AugV1", "result ==> " + result);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }   // createMap

}   // Main Class
