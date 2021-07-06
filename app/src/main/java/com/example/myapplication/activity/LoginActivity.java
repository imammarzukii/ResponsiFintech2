package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout parentLayout;
    private EditText edtEmail, edtPassword;
    private TextView tvSignUp;
    private Button btnLogin;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        initView();
        signIn();
        signUp();
    }

    private void initView() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        btnLogin = findViewById(R.id.btn_login);
        parentLayout = findViewById(R.id.parent_layout);
        auth = FirebaseAuth.getInstance();

    }

    public void signIn() {
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        setSnackBar(parentLayout, "Login Failed " +
                                                Objects.requireNonNull(task.getException()).getMessage());
                                    } else {
                                        setSnackBar(parentLayout, "Login Successfull");
                                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void signUp() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
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
}
