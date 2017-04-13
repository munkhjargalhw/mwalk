package io.mwalk.mwalk;
//ashiglagdah nemelt sanguud
import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //huvisagchid
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String latitude = null;
    private String longitude = null;
    private static final int GPS_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //tsonh uuseh ued ajillah heseg
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void getLocation(){
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        //utasnii location avag erhiig shalgah
        if (res == PackageManager.PERMISSION_GRANTED){
            //bairshil uurchlugduh agshniig bainga medeh
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //bairshliig 5000ms secund tutamd shalgah heseg
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            } else  {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
            }
        } else {
            //bairshliin erh ugugduugui bol erh avah huselt yavuulah
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "LOCATION permission denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            //bairshil uurchlugduh ues gazriin zurgiig shinechleh
            latitude = ""+loc.getLatitude();
            longitude = ""+loc.getLongitude();
            LatLng ub = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ub));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
            mMap.animateCamera(zoom);
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    //map iin tohirgoo heseg
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //anhnii bairshlig zaaj ter gazart ochih
        LatLng ub = new LatLng(47.918913, 106.917422);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ub));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(0);
        mMap.animateCamera(zoom);
        getLocation();
    }
}
