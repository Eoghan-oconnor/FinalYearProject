package com.example.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.navigation.ui.dashboard.DashboardFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> implements Serializable {


    String googlePlaceData;
    GoogleMap map;
    String url;
    String placeID;
    private HashMap<Integer, String> markerIdMapping = new HashMap<>();


    private DashboardFragment context;
    public GetNearbyPlacesData(DashboardFragment context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Object... objects) {

        Log.i("doInBACK", "*****");

        map = (GoogleMap) objects[0];
        url =(String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try{
            googlePlaceData = downloadUrl.readUrl(url);
        } catch (Exception e){
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultArray = parentObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {

                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject locationObject = jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude = locationObject.getString("lat");
                String longitude = locationObject.getString("lng");


                JSONObject nameObject = resultArray.getJSONObject(i);
                String name = nameObject.getString("name");

                JSONObject openNow = resultArray.getJSONObject(i);
                JSONObject openObject = openNow.getJSONObject("opening_hours");

                String open = openObject.getString("open_now");

                JSONObject placeIdObject = resultArray.getJSONObject(i);
                String placeId = placeIdObject.getString("place_id");
                Log.i("Place id: ", "pi"+ placeId);

                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                MarkerOptions markerOptions = new MarkerOptions()
                        .title(name)
                        .snippet(placeId)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(latLng);
             //   map.addMarker(markerOptions);
                Marker marker = map.addMarker(markerOptions);
                markerIdMapping.put(i, placeId);
                Log.i("MarkerMap", "PlaceId" + markerIdMapping.toString());
            }

            Bundle args = new Bundle();
            args.putSerializable("PlaceId", placeID);
            DashboardFragment dashboardFragment = new DashboardFragment();
            dashboardFragment.setArguments(args);
            Log.i("placeId=", args.toString());

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.i("GN", "Marker");
                    return false;
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }







}
