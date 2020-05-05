package com.example.navigation.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.navigation.GetNearbyPlacesData;
import com.example.navigation.InfoDisplay;
import com.example.navigation.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DashboardFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    GoogleMap mMap;
    MapView mMapView;
    View mView;
    GoogleApiClient mGoogleApiClient;
    double currentLat;
    double currentLong;
    Location myLocation;

    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(DashboardFragment.this);

    private Context mContext;
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }






    LocationManager locationManager;
    LocationListener locationListener;

    private DashboardViewModel dashboardViewModel;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Bundle bundle = this.getArguments();
        if(bundle == null){

            Log.i("bundle", "null");
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }


        setUPGClient();

    }


    private void setUPGClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {



        MapsInitializer.initialize(getContext());

        mMap = googleMap;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e("Location: ", user.toString());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(user).title("Your Location"));
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user, 10.0f));
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String s = marker.getSnippet();
                        String placeId = s.substring(0,26);
                        Log.i("DB", "Marker: " + placeId);

                        Intent intent = new Intent(getActivity(), InfoDisplay.class);
                        intent.putExtra("placeId", placeId);
                        startActivity(intent);

                        return false;
                    }
                });

                getNearByGasStations();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 ,1000 ,locationListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void getNearByGasStations(){

        StringBuilder stringBuilder =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location="+String.valueOf(currentLat)+","+String.valueOf(currentLong));
                stringBuilder.append("&radius=10000");
                stringBuilder.append("&type=gas_station");

        stringBuilder.append("&key="+mContext.getResources().getString(R.string.googlemapskey));



        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);

    }

}