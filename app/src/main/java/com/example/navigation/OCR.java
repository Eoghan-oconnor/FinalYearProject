package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class OCR extends AppCompatActivity {

    private Context mContext;

    EditText mResultEt;
    EditText mResultEt2;
    ImageView mPreviewIv;
    Button priceReview;
    TextView price1Confirm;
    ImageView pic;
    EditText popupPrice1;
    EditText popupPrice2;
    Button save;
    Switch switchPrice1;
    Switch switchPrice2;

    ParseQuery query = new ParseQuery("FuelPrice");







    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE= 1000;
    private static final int IMAGE_PICK_CAMERA_CODE= 1001;


    String cameraPermission[];
    String storagePermission[];

    Uri imageUri;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click + button to add    ");




        //Log.i("placeIdOCR",placeId);

        mResultEt = findViewById(R.id.resultEt);
        mResultEt2 = findViewById(R.id.resultEt2);
        mPreviewIv = findViewById(R.id.imageIv);
        priceReview = (Button) findViewById(R.id.submit);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addImage){
            showImageImportDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void PopupOnClick(View view){
        confirmFuelPrices();
    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Selecting Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }else {
                        pickCamera();
                    }
                }
                if(which== 1){

                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }else {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){

            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0) {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccept && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mPreviewIv.setImageURI(resultUri);


                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("-");
                    }

                    String sb = stringBuilder.toString();

                    String[] prices= sb.split("-");
                    String price1 = prices[0];
                    String price2 = prices[1];

                    mResultEt.setText(price1);
                    mResultEt2.setText(price2);


                    //popupPrice1.setText(price1);
                    //popupPrice2.setText(price2);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void confirmFuelPrices(){
        builder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.popup, null);


        popupPrice1 = (EditText) popup.findViewById(R.id.price1);
        popupPrice2 = (EditText) popup.findViewById(R.id.price2);
        switchPrice2 = (Switch) popup.findViewById(R.id.switch1);
        switchPrice1 = (Switch) popup.findViewById(R.id.switch2);

        popupPrice1.setText(mResultEt.getText().toString());
        popupPrice2.setText(mResultEt2.getText().toString());

        builder.setView(popup);
        dialog = builder.create();
        dialog.show();

    }

    public void SubmitOnClick(View view){

        String placeId = getIntent().getStringExtra("placeId");



        query.whereEqualTo("PlaceId",placeId);
        Log.i("Submit Clicked","***");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if(switchPrice1.isChecked()){
                    Log.i("switchPrice1","isChecked");
                    object.put("PetrolPrice",popupPrice1.getText().toString());
                    object.put("DieselPrice",popupPrice2.getText().toString());
                } else if(!switchPrice1.isChecked()){
                    object.put("DieselPrice",popupPrice1.getText().toString());
                    object.put("PetrolPrice",popupPrice2.getText().toString());
                }

                if (switchPrice2.isChecked()){
                    object.put("DieselPrice",popupPrice1.getText().toString());
                    object.put("PetrolPrice",popupPrice2.getText().toString());
                } else if (!switchPrice2.isChecked()){
                    object.put("DieselPrice",popupPrice2.getText().toString());
                    object.put("PetrolPrice",popupPrice1.getText().toString());
                }

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Log.i("Update Fuel Prices", "Save Successful OCR");
                        } else {
                            Log.i("Error", ":" + e.getMessage());
                        }
                    }
                });

            }

        });

        onBackPressed();
    }
}
