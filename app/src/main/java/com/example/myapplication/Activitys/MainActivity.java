package com.example.myapplication.Activitys;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel name";
    private static final String CHANNEL_DESC = "channel description notification";

    JSONObject Jobject;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView incorrect;
    EditText email, password;
    Button login, signup;
    private JsonPlaceHolder jsonPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        email = findViewById(R.id.atvEmailLog);
        password = findViewById(R.id.atvPasswordLog);
        login = findViewById(R.id.btnSignIn);
        signup = findViewById(R.id.btnSignUp);
        incorrect = findViewById(R.id.tvIncorrectPass);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete: " + token);
                        // Log and toast
                        /*String msg = getString(, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/
                    }
                });

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken",newToken);
            SharedPrefManager.getInstance(this).saveDeviceToken(newToken);
        });*/

        //FirebaseInstanceId.getInstance().getToken();

        /*pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        editor = pref.edit();
        editor.putString("firebaseToken", SharedPrefManager.getInstance(this).getDeviceToken());
        editor.apply();*/

        /*Log.d(TAG, "onCreate: "+pref.getString("firebaseToken", ""));*/
        // Toast.makeText(this, ""+ pref.getString("firebaseToken", ""), Toast.LENGTH_SHORT).show();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cuturhair.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);

        login.setOnClickListener(v -> {

            String xEmail = email.getText().toString();
            String xPass = password.getText().toString();
            makeLogin(xEmail, xPass);
        });

        signup.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });



    }


    private void makeLogin(String email, String password) {


        //Login login = new Login("userEmail@gmail.com", "password");
        Call<ResponseBody> call = jsonPlaceHolder.loginPost(email, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.code());
                    //Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    incorrect.setVisibility(View.VISIBLE);
                    return;
                }

                try {
                    assert response.body() != null;
                    Jobject = new JSONObject(response.body().string());
                    if (response.code() == 200) {
                        try {
                            editor.putString("accessToken", Jobject.getString("access_token"));
                            editor.commit();
                        } catch (JSONException e) {
                            e.getMessage();
                        }

                        getUserDetails();

                    }

                } catch (JSONException | IOException e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void updateDeviceToken() {

        Call<ResponseBody> call = jsonPlaceHolder.updateDeviceToken(pref.getString("id", null), pref.getString("firebaseToken", null));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                Log.d(TAG, "onResponse: "+ response.body());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getUserDetails() {

        Call<ResponseBody> call = jsonPlaceHolder.getUser("Bearer " + pref.getString("accessToken", null));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    incorrect.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                try {

                    Jobject = new JSONObject(response.body().string());


                    editor.putString("email", Jobject.getString("email"));
                    editor.putString("id", Jobject.getString("id"));
                    editor.putString("name", Jobject.getString("name"));
                    editor.putString("is_provider", Jobject.getString("is_provider"));
                    editor.commit();

                    updateDeviceToken();

                    Log.d(TAG, "onResponse: " + pref.getString("is_provider", null));

                    if (Jobject.getInt("is_provider") == 1) {
                        finish();
                        startActivity(new Intent(MainActivity.this, ProviderActivity.class));
                    }
                    if (Jobject.getInt("is_provider") == 0) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                    }


                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
