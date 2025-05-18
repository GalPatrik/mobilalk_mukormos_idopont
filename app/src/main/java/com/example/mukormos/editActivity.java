package com.example.mukormos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.os.Handler;

public class editActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference myTimes;
    EditText edittimeEDT;
    Button edittimeButton;
    String documentid;

    private Handler handler = new Handler(); // 🔁 For animation looping
    private Runnable repeatAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_edit);
        edittimeButton = findViewById(R.id.edittimeButton);
        edittimeEDT = findViewById(R.id.edittimeEDT);
        TextView title = findViewById(R.id.editTitle);

        repeatAnimation = new Runnable() {
            @Override
            public void run() {
                Animation pop = AnimationUtils.loadAnimation(editActivity.this, R.anim.pop);
                title.startAnimation(pop);
                handler.postDelayed(this, 2000); // Run again in 2 seconds
            }
        };
        handler.post(repeatAnimation);

        firebaseFirestore = FirebaseFirestore.getInstance();
        myTimes = firebaseFirestore.collection("Times");

        querytime();
        edittimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newIdopont = edittimeEDT.getText().toString().trim();

                myTimes.document(documentid)
                        .update("idopont", newIdopont)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(editActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editActivity.this, MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(editActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Update failed", e);
                        });
            }
        });
    }
    private void querytime() {
        documentid = getIntent().getStringExtra("id");

        myTimes.document(documentid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myTime time = documentSnapshot.toObject(myTime.class);
                        if (time != null) {
                            edittimeEDT.setText(time.getIdopont());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching document", e);
                });
    }


//    private void querytime() {
//        myTimes.whereEqualTo("id", getIntent().getStringExtra("id"))
//                .limit(1)
//                .get()
//                .addOnSuccessListener((queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        myTime time = document.toObject(myTime.class);
//                        edittimeEDT.setText(time.getIdopont());
//                        documentid = document.getId();
//                    }
//                }));
//
//    }


}