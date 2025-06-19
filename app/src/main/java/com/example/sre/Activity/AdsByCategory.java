package com.example.sre.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.sre.Adapter.AdAdapter;
import com.example.sre.Domain.AdModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.databinding.ActivityAdsByCategoryBinding;

import com.example.sre.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdsByCategory extends AppCompatActivity {

    ArrayList<AdModel> ads = new ArrayList<>();
    TextView categoryTitle;
    RecyclerView recyclerView1;
    AdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_by_category);

        recyclerView1 = findViewById(R.id.recyclerView1);
        categoryTitle = findViewById(R.id.category_title);

        Intent get = getIntent();
        String selected_cat = get.getStringExtra("category");
//        Toast.makeText(this, selected_cat, Toast.LENGTH_SHORT).show();

        categoryTitle.setText(selected_cat);

        adapter = new AdAdapter(ads, AdsByCategory.this);  // attach to global ads list
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter);

//        fetchAllAds(selected_cat);  // only need category now

        fetchAllAds(selected_cat, new OnAdsFetchedCallback() {
            @Override
            public void onAdsFetched(ArrayList<AdModel> fetchedAds) {
                ads.clear();
                ads.addAll(fetchedAds);
                adapter.notifyDataSetChanged();
            }
        });

    }

//    public void fetchAllAds(String ad_category) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        if (ad_category.equals("All")) {
//            db.collection("ads")
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        ads.clear();  // clear global list only
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            AdModel ad = doc.toObject(AdModel.class);
//                            ads.add(ad);  // add directly to adapter's list
//                            Log.d("FirestoreAd", "Fetched: " + ad.title);
//                        }
//                        adapter.notifyDataSetChanged();  // refresh adapter
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Firestore", "Failed to fetch ads: " + e.getMessage());
//                    });
//        } else {
//            db.collection("ads").whereEqualTo("category", ad_category)
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        ads.clear();
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            AdModel ad = doc.toObject(AdModel.class);
//                            ads.add(ad);
//                            Log.d("FirestoreAd", "Fetched: " + ad.title);
//                        }
//                        adapter.notifyDataSetChanged();
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Firestore", "Failed to fetch ads: " + e.getMessage());
//                    });
//        }
//    }



    public void fetchAllAds(String ad_category, OnAdsFetchedCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<AdModel> fetchedAds = new ArrayList<>();

        if (ad_category.equals("All")) {
            db.collection("ads")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            AdModel ad = doc.toObject(AdModel.class);
                            fetchedAds.add(ad);
                            Log.d("FirestoreAd", "Fetched: " + ad.title);
                        }
                        callback.onAdsFetched(fetchedAds); // pass data back
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Failed to fetch ads: " + e.getMessage());
                    });
        } else {
            db.collection("ads").whereEqualTo("category", ad_category)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            AdModel ad = doc.toObject(AdModel.class);
                            fetchedAds.add(ad);
                            Log.d("FirestoreAd", "Fetched: " + ad.title);
                        }
                        callback.onAdsFetched(fetchedAds); // pass data back
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Failed to fetch ads: " + e.getMessage());
                    });
        }
    }



    public interface OnAdsFetchedCallback {
        void onAdsFetched(ArrayList<AdModel> ads);
    }
}


