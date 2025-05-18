package com.example.mukormos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference myTimes;

    private RecyclerView recyclerView;
    private timeitemAdapter adapter;
    private ArrayList<myTime> myTimesList = new ArrayList<>();

    EditText ujidopontEDT;
    Button ujidopontButton;
    String idopont;
    Date jelenido = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        myTimes = firebaseFirestore.collection("Times");

        ujidopontEDT = findViewById(R.id.idopontEDT);
        ujidopontButton = findViewById(R.id.idopontletrehozButton);
        recyclerView = findViewById(R.id.idopontlistaRVV);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new timeitemAdapter(this, myTimesList);
        recyclerView.setAdapter(adapter);

        ujidopontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ujidopontEDT.getText().toString().isEmpty()) {
                    idopont = ujidopontEDT.getText().toString();
                } else {
                    ujidopontEDT.setError("Kérlek adj meg egy időpontot!");
                    return;
                }

                myTime newTime = new myTime(idopont, firebaseUser.getEmail(), jelenido);
                myTimes.add(newTime);
                Toast.makeText(MainActivity.this, "Időpont hozzáadva!", Toast.LENGTH_SHORT).show();
                loadTimesFromFirestore();  // refresh list
            }
        });

        loadTimesFromFirestore();
    }

    private void loadTimesFromFirestore() {
        myTimes.get().addOnSuccessListener(querySnapshot -> {
            myTimesList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                myTime item = doc.toObject(myTime.class);
                item.setId(doc.getId());
                myTimesList.add(item);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
