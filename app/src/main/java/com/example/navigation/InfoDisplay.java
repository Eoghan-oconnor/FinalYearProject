package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigation.ui.dashboard.DashboardFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class InfoDisplay extends AppCompatActivity {

    TextView placeName;
    EditText petrol;
    EditText diesel;
    TextView name;
    String placeId;
    String ShopName;
    Boolean petrolChanged = false;
    Boolean dieselChanged = false;


    ParseObject stationInfo = new ParseObject("FuelPrice");
    ParseQuery query = new ParseQuery("FuelPrice");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);
        placeId = getIntent().getStringExtra("placeId");
        ShopName = getIntent().getStringExtra("name");

        placeName = (TextView) findViewById(R.id.PlaceName);
        petrol = (EditText) findViewById(R.id.petrol);
        name = (TextView) findViewById(R.id.name);
        diesel = (EditText) findViewById(R.id.diesel);

        placeName.setText(placeId);
        placeName.setVisibility(View.INVISIBLE);
        name.setText(ShopName);

        petrol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                dieselChanged = true;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        petrol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                petrolChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        String petrolPrice = Double.toString(stationInfo.getDouble("petrolPrice"));
//        Log.i("Petrol","Price" +petrolPrice);
//        petrol.setText(petrolPrice);


        getPlaceinfo(placeId);
        ParseInfo(placeId);

    }

    public void getPlaceinfo(String placeId) {

        GetPlaceInfo getPlaceInfo = new GetPlaceInfo(placeId);

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("placeid=" + placeId);
        sb.append("&key=" + getResources().getString(R.string.googlemapskey));

        String url = sb.toString();

        Log.i("URL", "" + url);

        Object dataTransfer[] = new Object[1];
        dataTransfer[0] = url;

        getPlaceInfo.execute(dataTransfer);
    }

    public void ParseInfo(String placeId) {

        query.whereEqualTo("PlaceId", placeId);
        Log.i("PlaceId", "After query: " + placeId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                //Log.i("Object", "Check" + object.toString());

                if (e == null) {
                    Log.i("petrol", "****** " + stationInfo.getString("PetrolPrice"));

                    petrol.setText(object.getString("PetrolPrice"));
                    diesel.setText(object.getString("DieselPrice"));

                } else {
                    //Log.i("Exeception Response:", ":" + e.getMessage());
                }
            }
        });
    }

    public void voidUpdateOnClick(View view) {
        if (diesel.getText().toString().matches("") || diesel.getText().toString().matches("")) {
            Toast.makeText(this, "Please Do not enter a Blank String", Toast.LENGTH_SHORT).show();

        } else {
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {

                    if (dieselChanged || petrolChanged) {
                        object.put("DieselPrice", diesel.getText().toString());
                        object.put("PetrolPrice", petrol.getText().toString());
                        petrolChanged = false;
                        dieselChanged = false;
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    Log.i("Update Fuel Prices", "Save Successful");
                                } else {
                                    Log.i("Error***", ":" + e.getMessage());
                                }

                            }
                        });
                    }
                }
            });

            onBackPressed();
        }
    }
}
