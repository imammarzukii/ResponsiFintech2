package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.github.bayu1993.androidbasic.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignUp;
    private FirebaseAuth auth;
    private ConstraintLayout parentLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initView();
        signUpProcess();
    }

    private void initView() {
        edtEmail = findViewById(R.id.edt_email_sign_up);
        edtPassword = findViewById(R.id.edt_password_sign_up);
        btnSignUp = findViewById(R.id.btn_sign_up);
        auth = FirebaseAuth.getInstance();
        parentLayout = findViewById(R.id.paren_layout_sign_up);
    }

    private void signUpProcess() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    edtEmail.setError("your email is blank");
                } else if (password.isEmpty()) {
                    edtPassword.setError("your password is blank");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError("Email not valid");
                } else if (password.length() < 6) {
                    edtPassword.setError("password must contain at least 6 characters");
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        setSnackBar(parentLayout, "Sorry, Sign up failed " + Objects.requireNonNull(task.getException()).getMessage());
                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }

    public static void setSnackBar(View coordinatorLayout, String snackTitle) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_LONG);
        snackbar.show();
        View view = snackbar.getView();
        TextView tvSnackbar = view.findViewById(android.support.design.R.id.snackbar_text);
        tvSnackbar.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}