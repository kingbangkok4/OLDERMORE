package com.app.oldermore;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends FragmentActivity implements LocationListener {
    GPSTracker gps = new GPSTracker(this);
    private static Location TODO;
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    // Google Map
    private GoogleMap googleMap;

    // Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle extras = getIntent().getExtras();
        // เช็คว่ามีค่าที่ส่งมาจากหน้าอื่นหรือไม่ถ้ามีจะไม่เท่ากับ null
        if (extras != null) {
            tmpMyArrList = (ArrayList<HashMap<String, String>>) extras
                    .getSerializable("MyArrList");
            if (tmpMyArrList != null) {
                MyArrList = tmpMyArrList;
            }
        }

        //*** Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //*** Display Google Map
        googleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        Criteria criteria = new Criteria();
        String mprovider;

        mprovider = locationManager.getBestProvider(criteria, false);
        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);
            Location location = locationManager.getLastKnownLocation(mprovider);
            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "ไม่พบสถานที่ของคุณ กรุณาเปิด GPS!", Toast.LENGTH_SHORT).show();
        }


       // Location location=new Location("");
        //*** For test Location
      /*  location.setLatitude(13.756331);
        location.setLongitude(100.501765);*/
        //*** Data Using
        // location.setLatitude(location.getLatitude());
        // location.setLongitude(location.getLongitude());
       // location = gps.getLocation();
        // onLocationChanged(location);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            return;
        }

        String msg="ละติจูด: " + location.getLatitude()
                + "ลองจิจูด: " + location.getLongitude();

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        //** Get Latitude & Longitude
        Latitude=location.getLatitude();
        Longitude=location.getLongitude();

        googleMap.clear();

        //*** Focus & Zoom
        LatLng coordinate=new LatLng(Latitude, Longitude);
        googleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));

        //*** Marker
        MarkerOptions marker=new MarkerOptions()
                .position(new LatLng(Latitude, Longitude)).title("คุณอยู่ตรงนี้");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        googleMap.addMarker(marker);
    }


    @Override
    public void onProviderDisabled(String provider) {
        // Log.d("Latitude","disable");
        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Log.d("Latitude","enable");
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Log.d("Latitude","status");
        // TODO Auto-generated method stub
    }

}