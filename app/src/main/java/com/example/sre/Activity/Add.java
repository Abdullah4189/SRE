package com.example.sre.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.Domain.AdModel;
import com.example.sre.MapsActivity;
import com.example.sre.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Add extends AppCompatActivity {

    AdModel adModel = new AdModel();
    String[] categories = {"Food", "Item"};
    String[] types = {"Sell", "Donate"};
    double lat;
    double longt;
    private static final int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    Button add_img_btn;
    Button add_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText title_et = findViewById(R.id.title);
        EditText phone_et = findViewById(R.id.phoneNo);
        EditText description_et = findViewById(R.id.description);
        EditText price_et = findViewById(R.id.price);
        Spinner category_spinner = findViewById(R.id.category_spinner);
        Spinner type_spinner = findViewById(R.id.type_spinner);
        Button save_btn = findViewById(R.id.save_button);
        TextView priceTv = findViewById(R.id.PricetextView);
        add_img_btn = findViewById(R.id.add_img_button);
        add_location = findViewById(R.id.add_loc);


        FirebaseFirestore db = FirebaseFirestore.getInstance();



        ArrayAdapter<String> category_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        category_spinner.setAdapter(category_adapter);

        ArrayAdapter<String> type_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        type_spinner.setAdapter(type_adapter);

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                type_spinner.setSelection(position);
                adModel.type = type_spinner.getSelectedItem().toString();

                if(type_spinner.getSelectedItem().toString().equals("Donate")){

                    priceTv.setVisibility(View.GONE);
                    price_et.setVisibility(View.GONE);
                } else{
                    priceTv.setVisibility(View.VISIBLE);
                    price_et.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                category_spinner.setSelection(position);
                adModel.category = category_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adModel.id = UUID.randomUUID().toString();

                final String[] imageUrl = new String[1];

                final ByteArrayOutputStream[] baos = {new ByteArrayOutputStream()};
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos[0]);
                byte[] imageData = baos[0].toByteArray();

//                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
//                        .child("images/" + UUID.randomUUID().toString() + ".jpg");
//
//                UploadTask uploadTask = storageRef.putBytes(imageData);
//
//                uploadTask.addOnSuccessListener(taskSnapshot -> {
//                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        imageUrl[0] = uri.toString();
//                        adModel.img_url = imageUrl[0];
//
//                    });
//                }).addOnFailureListener(e -> {
////                    Toast.makeText(Add.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });


                adModel.description = description_et.getText().toString();
                adModel.title = title_et.getText().toString();
                loadLocationFromPrefs();
                adModel.latitude = lat;
                adModel.longitude = longt;
                adModel.phoneNo = phone_et.getText().toString();
                adModel.posted_by = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                if(adModel.type.equals("Sell")){
                    adModel.price = price_et.getText().toString();
                }
                else{
                    adModel.price = "";
                }

                try{
                    // Add to Firestore collection "ads"
                    db.collection("ads")
                            .add(adModel)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(Add.this, "Ad posted successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Add.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add.this, MapsActivity.class);
                startActivity(intent);
            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Optional: Convert to Bitmap or upload to Firebase
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                add_img_btn.setText("Image Added");
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadLocationFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);

        lat = Double.longBitsToDouble(prefs.getLong("latitude", Double.doubleToRawLongBits(0.0)));
        longt = Double.longBitsToDouble(prefs.getLong("longitude", Double.doubleToRawLongBits(0.0)));

        Log.d("latitude", String.valueOf(lat));


    }


}

