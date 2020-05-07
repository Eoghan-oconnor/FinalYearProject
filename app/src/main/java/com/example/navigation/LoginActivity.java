package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Boolean signUpMode = true;

    TextView changeSignUp;
    Button signUpButton;
    TextView email;
    TextView makeModel;
    RadioButton petrol;
    RadioButton diesel;



    public void onClick(View view){
        if(view.getId() == R.id.ChangeSignUpMode){
            if(signUpMode){

                signUpMode = false;
                signUpButton.setText("Login");
                changeSignUp.setText("or, Sign up");
                email.setVisibility(View.INVISIBLE);
                makeModel.setVisibility(View.INVISIBLE);
                diesel.setVisibility(View.INVISIBLE);
                petrol.setVisibility(View.INVISIBLE);
            } else {
                signUpMode = true;
                signUpButton.setText("Sign Up");
                changeSignUp.setText("or, Login");
                email.setVisibility(View.VISIBLE);
                makeModel.setVisibility(View.VISIBLE);
                diesel.setVisibility(View.VISIBLE);
                petrol.setVisibility(View.VISIBLE);
            }

        }
    }

    public void RadioButtonOnClick(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()){

            case R.id.petrol:
                if(checked){
                    diesel.setChecked(false);
                }
                break;
            case R.id.diesel:
                if(checked){
                    petrol.setChecked(false);
                }
                break;
        }
    }
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
                user.setEmail(email.getText().toString());
                user.put("makeModel", makeModel.getText().toString());

                if(petrol.isChecked()){
                    diesel.setChecked(false);
                    user.put("fuel", "Petrol");
                } else {
                    petrol.setChecked(false);
                    user.put("fuel","Diesel");
                }

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successful");
                            login();

                        } else {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user !=null){
                            Log.i("Appinfo","Successful");
                            login();
                        } else {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeSignUp = (TextView) findViewById(R.id.ChangeSignUpMode);
        signUpButton = (Button) findViewById(R.id.SignUp);
        email = (TextView) findViewById(R.id.emailEditText2);
        makeModel = (TextView) findViewById(R.id.makeModel);
        petrol = (RadioButton) findViewById(R.id.petrol);
        diesel = (RadioButton) findViewById(R.id.diesel);
            changeSignUp.setOnClickListener(this);
    }

    public void login(){
        // opens App to user
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}