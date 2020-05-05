package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class InfoDisplay extends AppCompatActivity {

    TextView place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);
        String placeId = getIntent().getStringExtra("placeId");
        place = (TextView) findViewById(R.id.placeId);
        place.setText(placeId);

    }
}
