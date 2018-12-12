package com.kinocode.firebasestudying;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Button btnEdit, btnSignOut;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        imgUser = (ImageView) findViewById(R.id.img_user);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid());

        loadfromdatabase();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateUsernameDialog();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * menampilkan username dari firebase realtime database
     * menampilkan email dari firebase realtime database
     * menampilkan photo profil dari gmail atau google sign in*/

    private void loadfromdatabase(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                String username = user.getUsername();
                String email = user.getEmail();
                tvUsername.setText(username);
                tvEmail.setText(email);

                //load photo profil gmail
                Glide.with(getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(imgUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "loadUserIdentity:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * menampilkan dialog update suername
     * lalu menyimpanya kedalam firebase database*/
    private void showUpdateUsernameDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_username,null);
        dialogBuilder.setView(dialogView);

        final EditText etUsername = (EditText) dialogView.findViewById(R.id.et_edit_username);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String username = user.getUsername();
                etUsername.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "loadUserIdentity:onCancelled", databaseError.toException());
            }
        });

        dialogBuilder.setTitle("Edit Username")
        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = etUsername.getText().toString().trim();
                if (!TextUtils.isEmpty(newUsername)){
                    updateUsername(newUsername);
                }else {

                }
            }
        });
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }

    //fungsi untuk update username
    private void updateUsername(String newUsername){
        mDatabase.child("username").setValue(newUsername);

        tvUsername.setText(newUsername);
    }

    private void signOut(){
        mAuth.signOut();

         mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {

                 startActivity(new Intent(MainActivity.this, LoginActivity.class));


             }
         });
    }

}
