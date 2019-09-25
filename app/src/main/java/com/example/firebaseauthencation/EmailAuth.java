package com.example.firebaseauthencation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailAuth extends AppCompatActivity {

    private FirebaseAuth eaAuth;

    private EditText eaLoginEmail,eaLoginPassword,eaSignEmail,eaSignPassword;
    private TextView eaLoginT,eaSignT;

    private Button eaLoginB,eaSignB;
    private View eaLoginView,eaSignView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        eaAuth = FirebaseAuth.getInstance();

        eaLoginView = (View)findViewById(R.id.login_layout);
        eaSignView = (View)findViewById(R.id.sign_layout);


        /*
        This Button is used for the Login Purpose
         */


        eaLoginEmail = (EditText) eaLoginView.findViewById(R.id.l_user_name);
        eaLoginPassword = (EditText) eaLoginView.findViewById(R.id.l_password);

        eaLoginT = (TextView)eaLoginView.findViewById(R.id.l_goto);

        eaSignB = eaLoginView.findViewById(R.id.l_button);
        eaSignB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = eaLoginEmail.getText().toString();
                String password = eaLoginPassword.getText().toString();

                eaAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                        EmailAuth.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(EmailAuth.this, "Email Authentication Successfully !", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EmailAuth.this,Welcome.class));
                                }else{
                                    Toast.makeText(EmailAuth.this, "Email Authentication Failed !"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

            }
        });

        eaLoginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eaLoginView.setVisibility(View.GONE);
                eaSignView.setVisibility(View.VISIBLE);
            }
        });


        /*
        This is used for the SignUp Purpose
         */

        eaSignEmail = (EditText)eaSignView.findViewById(R.id.s_user_name);
        eaSignPassword = (EditText)eaSignView.findViewById(R.id.s_password);

        eaSignT = (TextView)eaSignView.findViewById(R.id.s_goto);

        eaSignB = eaSignView.findViewById(R.id.s_button);
        eaSignB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = eaSignEmail.getText().toString();
                String password = eaSignPassword.getText().toString();

                eaAuth.createUserWithEmailAndPassword(username,password)
                        .addOnCompleteListener(EmailAuth.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(EmailAuth.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EmailAuth.this,MainActivity.class));

                                }else{

                                    Toast.makeText(EmailAuth.this, "User Creation Failed !"+task.getException(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });

        eaSignT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eaSignView.setVisibility(View.GONE);
                eaLoginView.setVisibility(View.VISIBLE);
            }
        });
    }
}
