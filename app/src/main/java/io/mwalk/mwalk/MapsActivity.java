package io.mwalk.mwalk;
//ashiglagdah nemelt sanguud
import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    Bitmap mDotMarkerBitmap;
    private double alldistance = 0;
    private LatLng previousloc = null;
    Button start;
    TextView distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //tsonh uuseh ued ajillah heseg
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
        mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = getResources().getDrawable(R.drawable.circle);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distance = (TextView) findViewById(R.id.distance);
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alldistance = 0;
                previousloc = null;
                distance.setText("0km");
            }
        });
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            } else  {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
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
            Toast.makeText(getApplicationContext(), "loc="+latitude+":"+longitude, Toast.LENGTH_LONG).show();
            LatLng ub = new LatLng(loc.getLatitude(), loc.getLongitude());

            /*check later if location not fake*/
            if(previousloc.equals(null)){
                previousloc = ub;
            } else {
                double lastdistance = calcdistance(ub.latitude, ub.longitude, previousloc.latitude, previousloc.longitude);
                alldistance += lastdistance;
                distance.setText(String.valueOf(alldistance)+"km");
                previousloc = ub;
                Toast.makeText(getApplicationContext(), "Dist="+String.valueOf(lastdistance), Toast.LENGTH_LONG).show();
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(ub));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
            mMap.animateCamera(zoom);

            MarkerOptions marker = new MarkerOptions().position(ub).title(latitude+":"+longitude);
            marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
            mMap.addMarker(marker);
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

//        mMap.addCircle(new CircleOptions()
//                .center(ub)
//                .radius(10)
//                .strokeColor(Color.RED)
//                .fillColor(Color.BLUE));

        getLocation();
    }
    private double calcdistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
