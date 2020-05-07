package com.example.navigation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.navigation.DownloadUrl;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetPlaceInfo extends AsyncTask<Object, String, String> {

    String placeId;

    String googlePlaceData;
    String url;

    ParseObject stationInfo = new ParseObject("FuelPrice");
    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FuelPrice");
    //Object PlaceIdInDb = stationInfo.get("PlaceId");


    public GetPlaceInfo(String s) {
        this.placeId = s;
    }

    public GetPlaceInfo() {

    }


    @Override
    protected String doInBackground(Object[] objects) {
        Log.i("doInBACKPlaces", "*****");

        url = (String) objects[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(final String s) {

        super.onPostExecute(s);
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONObject result = parentObject.getJSONObject("result");
            JSONArray nameObject = result.getJSONArray("address_components");
            JSONObject opening = result.getJSONObject("opening_hours");
            final Boolean open_now = opening.getBoolean("open_now");
            //JSONObject name = result.getJSONObject("name");
            String ShopName = result.getString("name");


            stationInfo.put("openNow", open_now);
            stationInfo.put("ShopName", ShopName);



            for (int i = 0; i < nameObject.length(); i++) {

                JSONObject object = nameObject.getJSONObject(i);


               // name[i] = object.getString("long_name");

                Log.i("Open", "Now: " + open_now);
                //Log.i("LONGNAME", "::::" + name[0]);
            }
            query.whereEqualTo("PlaceId", placeId);
            Log.i("PlaceId","After query: "+ placeId);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {

                    if(e != null)//object doesnt exist
                    {
                        Log.i("Error", ":" + e.getMessage());
                        stationInfo.put("PlaceId", placeId);

                        stationInfo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.i("Save", "Save Successful");
                                } else {
                                    Log.i("Error***",":" + e.getMessage());
                                }
                            }
                        });
                    } else if (object.getString("openNow") != open_now.toString()){
                        object.put("openNow", open_now);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Log.i("Open Save", "Save Successful");
                                } else {
                                    Log.i("Error*** Open",":" + e.getMessage());
                                }
                            }
                        });


                    }

                }
            });




//            query.getInBackground(placeId,new GetCallback<ParseObject>() {
//                @Override
//                public void done(ParseObject object, ParseException e) {
//                    if(e==null){
//                        stationInfo.put("openNow", open);
//                        stationInfo.saveInBackground();
//                    } else {
//                        if(e != null){
//                            Log.i("Error",":" +e.getMessage() + e.getCode());
//                            stationInfo.put("openNow", open);
//                            stationInfo.put("PlaceId", placeId);
//                            stationInfo.saveInBackground();
//
//                        }
//
//                    }
//                }
//            });




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
