package com.example.sre.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.Domain.AdModel;
import com.example.sre.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdDetails extends AppCompatActivity {

    ImageView imageView;
    TextView titleTV;
    TextView typeTV;
    TextView categoryTV;
    TextView descriptionTV;
    TextView priceTV;
    TextView phoneTV;
    TextView priceValueTV;
    Button buy_btn;
    ProgressBar progressBar;

    AdModel adModel = new AdModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ad_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.img);
        titleTV = findViewById(R.id.title_tv);
        typeTV = findViewById(R.id.type_tv);
        priceTV = findViewById(R.id.price_tv);
        phoneTV = findViewById(R.id.phone_tv);
        priceValueTV = findViewById(R.id.price);
        categoryTV = findViewById(R.id.category_tv);
        descriptionTV = findViewById(R.id.description_tv);
        buy_btn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress);


        progressBar.setVisibility(View.VISIBLE);
        priceTV.setVisibility(View.GONE);
        priceValueTV.setVisibility(View.GONE);


        Intent get = getIntent();
        String id = get.getStringExtra("id");


            fetchAdDetailsById(id);


        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BuyButton", "Buy button clicked for Ad ID: " + id);

                FirebaseFirestore.getInstance()
                        .collection("ads")
                        .whereEqualTo("id", id) // 🔍 match field, not doc ID
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // first match
                                String postedByEmail = documentSnapshot.getString("posted_by");

                                // Now you can call your method
                                sendRequestToUser(postedByEmail, id);
                            } else {
                                Log.e("FirestoreError", "❌ No ad found with field id: " + id);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "❌ Failed to query ad by field id: " + e.getMessage());
                        });

            }
        });


    }



    private void fetchAdDetailsById(String adId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ads").whereEqualTo("id", adId).get().addOnSuccessListener(queryDocumentSnapshots -> {

            progressBar.setVisibility(View.GONE);

            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                String title = documentSnapshot.getString("title");
                String description = documentSnapshot.getString("description");
                String imageUrl = documentSnapshot.getString("image_url");
                String price = documentSnapshot.getString("price");
                String category = documentSnapshot.getString("category");
                String type = documentSnapshot.getString("type");

                adModel.title = title;
                adModel.type = type;
                adModel.description = description;
                adModel.category = category;
                adModel.price = price;


                if(adModel.type.equals("Sell")){
                    priceTV.setVisibility(View.VISIBLE);
                    priceValueTV.setVisibility(View.VISIBLE);
                    String p = "Rs." + adModel.price;
                    priceTV.setText(p);

                }

                titleTV.setText(adModel.title);
                descriptionTV.setText(adModel.description);
                categoryTV.setText(adModel.category);
                typeTV.setText(adModel.type);
                String p = "Phone: " + adModel.phoneNo;
                phoneTV.setText(p);

                if(type.equals("Donate"))
                {
                    buy_btn.setText("Send Request");
                }


                if(adModel.title.toLowerCase().contains("burger"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.burger, null));
                } else if(adModel.title.toLowerCase().contains("daal"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.daal, null));
                } else if(adModel.title.toLowerCase().contains("pizza"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.pizza, null));
                } else if(adModel.title.toLowerCase().contains("biryani"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.biryani, null));
                } else if(adModel.title.toLowerCase().contains("sofa"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.sofa, null));
                }
                else if(adModel.title.toLowerCase().contains("table"))
                {
                    imageView.setImageDrawable(ResourcesCompat.getDrawable(AdDetails.this.getResources(), R.drawable.table, null));
                }




            } else {
                Log.e("AdDetails", "Ad not found with ID: " + adId);
            }
        }).addOnFailureListener(e -> {
            Log.e("AdDetails", "Error fetching ad: ", e);
        });
    }

    private void sendRequestToUser(String receiverEmail, String adId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("adId", adId);
            requestData.put("senderEmail", currentUser.getEmail());
            requestData.put("receiverEmail", receiverEmail);
            requestData.put("timestamp", FieldValue.serverTimestamp());
            requestData.put("status", "pending");

            FirebaseFirestore.getInstance().collection("requests")
                    .add(requestData)
                    .addOnSuccessListener(doc -> Toast.makeText(this, "Request sent successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.e("RequestError", "Failed to send request: " + e.getMessage()));
        }
    }


}