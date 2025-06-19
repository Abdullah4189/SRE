package com.example.sre.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.Adapter.AdAdapter;
import com.example.sre.Adapter.RequestAdapter;
import com.example.sre.Adapter.RequestModel;
import com.example.sre.Domain.AdModel;
import com.example.sre.R;
import com.example.sre.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String edittext;
    RecyclerView recyclerView1;
    TextView nameText;
    ArrayList<AdModel> ads = new ArrayList<>();
    AdModel adModel = new AdModel();
    ImageView food_image;
    ImageView item_image;
    ImageView all_image;
    ImageView request_image;


    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration requestListener;
    TextView search;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("SRE_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        recyclerView1 = findViewById(R.id.recyclerView1);
        nameText = findViewById(R.id.nameText);
        food_image = findViewById(R.id.food_cat_img);
        item_image = findViewById(R.id.item_cat_img);
        all_image = findViewById(R.id.cat_img);
        ImageView profile = findViewById(R.id.imageView5);
        search = findViewById(R.id.editText);
        request_image = findViewById(R.id.request_img);



        AdAdapter adapter = new AdAdapter(ads, MainActivity.this);

        fetchAllAds(ads, adapter);

        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, User_detail.class);
                startActivity(intent);
            }
        });


        Intent get = getIntent();

        String email = get.getStringExtra("user_email");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString(email, "User");

        nameText.setText(username);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageTintList(ContextCompat.getColorStateList(this, R.color.red));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Add.class);
                startActivity(intent);
            }
        });


        food_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdsByCategory.class);
                intent.putExtra("category", "Food");
                startActivity(intent);
            }
        });

        item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdsByCategory.class);
                intent.putExtra("category", "Item");
                startActivity(intent);
            }
        });

        all_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdsByCategory.class);
                intent.putExtra("category", "All");
                startActivity(intent);
            }
        });

        request_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Requests.class);
                startActivity(intent);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchNearby.class);
                startActivity(intent);
            }
        });


    }

    public void fetchAllAds(ArrayList<AdModel> adList, RecyclerView.Adapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ads")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    adList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AdModel ad = doc.toObject(AdModel.class);
                        adList.add(ad);
                    }
                    adapter.notifyDataSetChanged(); // notify your adapter (optional)
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to fetch ads: " + e.getMessage());
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllAds(ads, recyclerView1.getAdapter());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchAllAds(ads, recyclerView1.getAdapter());

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            requestListener = db.collection("requests")
                    .whereEqualTo("receiverEmail", currentUser.getEmail())
                    .whereEqualTo("status", "pending")
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            List<RequestModel> requests = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                RequestModel request = doc.toObject(RequestModel.class);
                                requests.add(request);
                            }
                            showRequests(requests);
                        }

                        if (querySnapshot.getDocumentChanges().size() > 0) {
                            for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    RequestModel request = dc.getDocument().toObject(RequestModel.class);
                                    Toast.makeText(MainActivity.this, "New Request from: " + request.getSenderEmail(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestListener != null) {
            requestListener.remove();
        }
    }

    private void showRequests(List<RequestModel> requests) {
        RequestAdapter adapter = new RequestAdapter(requests);
//        recyclerView.setAdapter(adapter);
    }

}