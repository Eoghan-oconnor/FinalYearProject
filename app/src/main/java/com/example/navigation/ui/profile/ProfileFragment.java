package com.example.navigation.ui.profile;

import android.icu.text.DateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.navigation.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    EditText usernameProfile;
    EditText fuelTypeProfile;
    EditText mandmProfile;
    EditText emailProfile;
    Button updateButton;
    TextView update;
    TextView passwordLabel;
    EditText password;

    Boolean updateMode = true;
    Boolean usernameChanged = false;
    Boolean emailChanged = false;
    Boolean mandmChanged = false;
    Boolean fuelChanged = false;
    Boolean passwordChanged = false;


    ParseUser currentUser = ParseUser.getCurrentUser();

    private ProfileViewModel profileViewModel;

    View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameProfile = (EditText) mView.findViewById(R.id.usernameProfile);
        mandmProfile = (EditText) mView.findViewById(R.id.mandmProfile);
        emailProfile = (EditText) mView.findViewById(R.id.emailProfile);
        fuelTypeProfile = (EditText) mView.findViewById(R.id.fuelTypeProfile);
        updateButton = (Button) mView.findViewById(R.id.updateButton);
        update = (TextView) mView.findViewById(R.id.update);
        passwordLabel = (TextView) mView.findViewById(R.id.passwordLabel);
        password = (EditText) mView.findViewById(R.id.passwordEdit);
        update.setOnClickListener(this);

        updateButton.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        passwordLabel.setVisibility(View.INVISIBLE);

        usernameProfile.setText(currentUser.getUsername());
        emailProfile.setText(currentUser.getEmail());
        mandmProfile.setText(currentUser.getString("makeModel"));
        fuelTypeProfile.setText(currentUser.getString("fuel"));

        usernameProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                usernameChanged = true;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mandmProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mandmChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mandmProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mandmChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameChanged){
                    currentUser.setUsername(usernameProfile.getText().toString());
                    currentUser.saveInBackground();
                }
                if(emailChanged){
                    currentUser.setEmail(emailProfile.getText().toString());
                    currentUser.saveInBackground();
                }
                if(mandmChanged){
                    currentUser.put("fuel", mandmProfile.getText().toString());
                    currentUser.saveInBackground();
                }
                if(passwordChanged){
                    currentUser.setPassword(password.getText().toString());
                    currentUser.saveInBackground();
                }

            }
        });

        return mView;
    }




    public void onClick(View v) {
        //Log.i("view", "Enter");
        if(v.getId() == R.id.update){
            if(updateMode){
                updateMode = false;
                updateButton.setVisibility(View.VISIBLE);
                passwordLabel.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                update.setVisibility(View.INVISIBLE);
            }
        }
    }




}
