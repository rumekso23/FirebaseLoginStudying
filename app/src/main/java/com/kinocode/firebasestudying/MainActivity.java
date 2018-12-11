package com.kinocode.firebasestudying;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvEmail, tvUsername;
    private ImageView imgUser;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        imgUser = (ImageView) findViewById(R.id.img_user);

        mDatabase =FirebaseDatabase.getInstance().getReference().child("User");

        initFirebase();
        loadfromdatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    String UID = firebaseUser.getUid();
                }else {
                    MainActivity.this.finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    Log.d("TAG_MAIN", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void loadfromdatabase(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    String username = user.getUsername();
                    String email = user.getEmail();
                    tvUsername.setText(username);
                    tvEmail.setText(email);
                    Glide.with(getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(imgUser);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "loadUserIdentity:onCancelled", databaseError.toException());
            }
        });
    }

}
