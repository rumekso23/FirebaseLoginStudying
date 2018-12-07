package com.kinocode.firebasestudying;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout etInput;
    private TextView tvUsername;
    private Button btnInput;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUsername = (TextView) findViewById(R.id.tv_username);
        etInput = (TextInputLayout) findViewById(R.id.et_input);
        btnInput = (Button) findViewById(R.id.btn_input);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvUsername.setText(user.getDisplayName());

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitData();
            }
        });
    }

    private void SubmitData(){
        Data data = new Data();
        final String inputData = etInput.getEditText().getText().toString();
        if (inputData.isEmpty()){
            etInput.setError("Fill this !!!");
            return;
        }

        final String userid = user.getUid();
        data.inputData = etInput.getEditText().getText().toString();
        mDatabase.child("Data").child(userid).setValue(data).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                etInput.getEditText().setText("");
                Snackbar.make(findViewById(R.id.btn_input), "input berhasil", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
