package com.example.sre.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.Adapter.AdAdapter;
import com.example.sre.Adapter.RequestAdapter;
import com.example.sre.Adapter.RequestModel;
import com.example.sre.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {
    RecyclerView recyclerView;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListenerRegistration requestListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



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
                                    Toast.makeText(Requests.this, "New Request from: " + request.getSenderEmail(), Toast.LENGTH_SHORT).show();
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
        recyclerView.setAdapter(adapter);
    }



}