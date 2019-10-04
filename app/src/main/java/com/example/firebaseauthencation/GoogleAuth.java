package com.example.firebaseauthencation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleAuth extends AppCompatActivity {

    // Google Authentication
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "Google Activity";
    private static final int RC_SIGN_IN = 9001;

    private TextView statusView,emailView,nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        statusView = (TextView)findViewById(R.id.ga_status);
        emailView = (TextView)findViewById(R.id.ga_email);
        nameView = (TextView)findViewById(R.id.ga_name);

        /*
        For the Google authentication purpose
         */

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.ga_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(GoogleAuth.this,MainActivity.class));
            }
        });


    }

    private void updateUI(String value) {
        if(value.equals("yes")){
            emailView.setText(firebaseAuth.getCurrentUser().getEmail());
            nameView.setText(firebaseAuth.getCurrentUser().getDisplayName());
        }else{
            statusView.setText("Failed to SignIN with Google ! Try Again ");
            emailView.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);

        }
    }


    void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(GoogleAuth.this, "Google Authentication Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleAuth.this, "Google Authentication Failure", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        signIn();
        if(firebaseAuth.getCurrentUser() == null){
            signIn();
        }else{
            updateUI("yes");
        }
    }
}
