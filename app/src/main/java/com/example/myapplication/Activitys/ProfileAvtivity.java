package com.example.myapplication.Activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Models.Reserves;
import com.example.myapplication.Progressbar.ProgressBar;
import com.example.myapplication.R;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileAvtivity extends AppCompatActivity {

    TextView username;
    EditText profileName, email, password, phoneNumber;
    Button update;
    JsonPlaceHolder jsonPlaceHolder;
    private SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog loading = null;
    ProgressBar progressBar;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            //.addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    private static final String TAG = "ProfileAvtivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_avtivity);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        username = findViewById(R.id.profile_username);
        profileName = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        password = findViewById(R.id.profile_password);
        phoneNumber = findViewById(R.id.profile_phone);
        update = findViewById(R.id.profile_saveBtn);
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        editor = pref.edit();

        username.setText(pref.getString("name", null));
        profileName.setText(pref.getString("name", null));
        email.setText(pref.getString("email", null));
        password.setText(pref.getString("password", null));


        update.setOnClickListener(v -> new AlertDialog.Builder(ProfileAvtivity.this)
                .setMessage("Are you sure you want to save?")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                    progressBar = new ProgressBar();
                    progressBar.showProgress(v.getContext());

                    /*loading = new ProgressDialog(v.getContext());
                    loading.setCancelable(true);
                    loading.setMessage("Loading...");
                    loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    loading.show();*/

                    //loading.dismiss();
                    new Handler().postDelayed(this::update, 3000);

                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.dialog_holo_dark_frame)
                .show());

    }

    public void update() {

        Call<ResponseBody> call = jsonPlaceHolder.updateProfile(pref.getString("id", null),
                profileName.getText().toString(), email.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    progressBar.dismissProgress();
                    return;
                }

                editor.putString("name", profileName.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.apply();
                Log.d(TAG, "onResponse: " + profileName.getText().toString());
                progressBar.dismissProgress();
                
                finish();
                startActivity(new Intent(ProfileAvtivity.this, ProfileAvtivity.class));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(ProfileAvtivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                progressBar.dismissProgress();
            }
        });

    }
}
