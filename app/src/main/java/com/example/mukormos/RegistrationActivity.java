package com.example.mukormos;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    EditText firstNameET;
    EditText surnameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;
    EditText usernameET;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference myUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);
        createNotificationChannel(); // 🔔 Create notification channel

        firstNameET = findViewById(R.id.firstNameEditText);
        surnameET = findViewById(R.id.surnameEditText);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordAgainET = findViewById(R.id.passwordAgainEditText);
        usernameET = findViewById(R.id.usernameEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        myUsers = firebaseFirestore.collection("Users");
    }

    public void register(View view) {
        String name = firstNameET.getText().toString() + " " + surnameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();

        if (!password.equals(passwordAgain)) {
            Log.e("Password error", "Password and password again are not same!");
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("User", "User creation successful!");
                    Toast.makeText(RegistrationActivity.this, name + " registration was successful", Toast.LENGTH_LONG).show();
                    sendRegistrationSuccessNotification(name); // 🔔 Show notification
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("User", "User creation failed!");
                    Toast.makeText(RegistrationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        createUserInFirestore(email);
    }

    public void createUserInFirestore(String email) {
        MyUser user = new MyUser(
                "", // temporarily empty, Firestore will generate the ID
                email,
                usernameET.getText().toString(),
                new MyUser.Name(
                        firstNameET.getText().toString(),
                        surnameET.getText().toString()
                )
        );

        myUsers
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    documentReference.update("id", generatedId);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error saving user", e);
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "registration_channel",
                    "Registration Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for successful registration");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendRegistrationSuccessNotification(String name) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "registration_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // ✅ Use your app icon here
                .setContentTitle("Welcome to the App!")
                .setContentText(name + ", your registration was successful!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
