package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Boolean signUpMode = true;

    TextView changeSignUp;

    public void onClick(View view){

        Button signUpButton = (Button) findViewById(R.id.SignUp);

        if(view.getId() == R.id.ChangeSignUpMode){
            if(signUpMode){

                signUpMode = false;
                signUpButton.setText("Login");
                changeSignUp.setText("or, Sign up");

            } else {

                signUpMode = true;
                signUpButton.setText("Sign Up");
                changeSignUp.setText("or, Login");

            }
        }
    }

    // Variables
    private Button button;

    public void signUp(View view){

        EditText username  = (EditText) findViewById(R.id.usernameEditText);
        EditText password  = (EditText) findViewById(R.id.passwordEditText);

        if (username.getText().toString().matches("")|| password.getText().toString().matches("")){
            Toast.makeText(this, "Please Enter a username and a password", Toast.LENGTH_SHORT).show();
        } else {

            if (signUpMode) {


                ParseUser user = new ParseUser();


                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successful");
                            openMaps();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user !=null){
                            Log.i("Appinfo","Successful");
                            openMaps();

                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    }
                });
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         changeSignUp = (TextView) findViewById(R.id.ChangeSignUpMode);
        changeSignUp.setOnClickListener(this);

        //username = (TextView) findViewById("userName");


//        // Adding button, Place holder until login is added
//        button = (Button) findViewById(R.id.button);
//        // Listener for button
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Calls open maps method
//                openMaps();
//            }
//        });

    }

    public void openMaps(){
        // Opens Maps.
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

    }


}
