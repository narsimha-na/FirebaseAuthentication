package com.example.firebaseauthencation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    private EditText paCountryCode,paNumber,paOtp;
    private String countryCode,number,otp,mVerificationId;
    private LinearLayout  paLinearLayout;
    private TextView paOtpAgain;

    private FirebaseAuth paFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        paFirebaseAuth = FirebaseAuth.getInstance();

        paCountryCode = (EditText) findViewById(R.id.pa_country_code);
        paLinearLayout = (LinearLayout)findViewById(R.id.pa_phone_layout);
        paNumber = (EditText) findViewById(R.id.pa_phone);
        paOtp = (EditText) findViewById(R.id.pa_otp);
        paOtpAgain = (TextView)findViewById(R.id.pa_otp_again);

        findViewById(R.id.pa_submit_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(paCountryCode.getText().toString(),paNumber.getText().toString());
                paLinearLayout.setVisibility(View.GONE);
                findViewById(R.id.pa_submit_number).setVisibility(View.GONE);
                paOtp.setVisibility(View.VISIBLE);
                findViewById(R.id.pa_submit_otp).setVisibility(View.VISIBLE);
                paOtpAgain.setVisibility(View.VISIBLE);

            }
        });

        paOtpAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(paCountryCode.getText().toString(),paNumber.getText().toString());
                paLinearLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.pa_submit_number).setVisibility(View.VISIBLE);
                paOtp.setVisibility(View.GONE);
                findViewById(R.id.pa_submit_otp).setVisibility(View.GONE);
                paOtpAgain.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.pa_submit_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtpCode(paOtp.getText().toString());
            }
        });

    }

    private void sendVerificationCode(String countryCode, String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode+number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //Sometimes code might not be detected
            if(code != null){
                paOtp.setText(code);
                //Verifying the code
                verifyOtpCode(code);
            }
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneAuth.this, "Something went wrong ! :"+e, Toast.LENGTH_SHORT).show();
            Log.d("errorPhone",e.getMessage().trim());

        }
    };

    private void verifyOtpCode(String otp) {
        //Creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,otp);
        signInUserWithPhoneAuthCredential(credential);
    }

    private void signInUserWithPhoneAuthCredential(PhoneAuthCredential credential) {
        paFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent na = new Intent(PhoneAuth.this,Welcome.class);
                            startActivity(na);
                        }else{
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(PhoneAuth.this, "Invalid Code !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


}