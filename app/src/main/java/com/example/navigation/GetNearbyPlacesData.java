package com.example.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> {

    String googlePlaceData;
    GoogleMap map;
    String url;




    @Override
    protected String doInBackground(Object... objects) {

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
                        .snippet("Open now: "+open)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(latLng);
                map.addMarker(markerOptions);

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        

                        return false;
                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
