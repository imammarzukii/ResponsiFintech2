package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.bayu1993.androidbasic.R;
import io.github.bayu1993.androidbasic.adapter.PariwisataAdapter;
import io.github.bayu1993.androidbasic.data.Constanta;
import io.github.bayu1993.androidbasic.data.model.PariwisataModel;
import io.github.bayu1993.androidbasic.data.network.NetworkService;
import io.github.bayu1993.androidbasic.data.network.RestService;
import io.github.bayu1993.androidbasic.data.network.response.PariwisataResponse;
import io.github.bayu1993.androidbasic.view.OnPariwisataClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private RestService restService;
    private PariwisataAdapter adapter;
    private List<PariwisataModel> pariwisataModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cekUser();
        initRest();
        loadData();
        initView();
    }

    private void cekUser() {
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void initRest() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://erporate.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkService networkService = retrofit.create(NetworkService.class);
        restService = new RestService(networkService);
    }

    private void loadData() {
        restService.getPariwisata(new RestService.MyCallback() {
            @Override
            public void onSuccess(PariwisataResponse response) {
                pariwisataModelList.clear();
                pariwisataModelList.addAll(response.getData());
                adapter.updatePariwisata(pariwisataModelList);
                Log.e(TAG, "onSuccess <<-------");
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        RecyclerView rvPariwisata = findViewById(R.id.rc_pariwisata);
        adapter = new PariwisataAdapter(this, onPariwisataClick);
        rvPariwisata.setLayoutManager(new GridLayoutManager(this, 2));
        rvPariwisata.setAdapter(adapter);
    }

    private OnPariwisataClick onPariwisataClick = new OnPariwisataClick() {
        @Override
        public void onCLick(int position) {
            PariwisataModel pariwisataModel = pariwisataModelList.get(position);
            Intent intent = new Intent(MainActivity.this, DetailPariwisataActivity.class);
            intent = intent.putExtra(Constanta.KEY_PARIWISATA, pariwisataModel);
            startActivity(intent);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_menu) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }
}
