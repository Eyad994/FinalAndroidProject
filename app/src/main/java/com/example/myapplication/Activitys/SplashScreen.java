package com.example.myapplication.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Progressbar.ProgressBar;
import com.example.myapplication.R;
import com.example.myapplication.UserActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private SharedPreferences pref;
    JSONObject Jobject;
    SharedPreferences.Editor editor;
    private JsonPlaceHolder jsonPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                editor.putString("firebaseToken", FirebaseInstanceId.getInstance().getToken());
                editor.apply();

                Log.d(TAG, "run: " + pref.getString("firebaseToken", null));
                if (pref.getString("accessToken", null) != null) {

                    updateDeviceToken();
                    getUserDetails();

                    String is_provider = pref.getString("is_provider", null);

                    assert is_provider != null;
                    if (is_provider.equals("1")) {
                        startActivity(new Intent(SplashScreen.this, ProviderActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, MapsActivity.class));
                        finish();
                    }

                } else {
                    finish();
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
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
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                try {
                    Jobject = new JSONObject(response.body().string());
                    Log.d(TAG, "onResponse: "+ Jobject);

                    editor.putString("email", Jobject.getString("email"));
                    editor.putString("id", Jobject.getString("id"));
                    editor.putString("name", Jobject.getString("name"));
                    editor.putString("is_provider", Jobject.getString("is_provider"));
                    editor.putString("password", Jobject.getString("password"));
                    editor.commit();
                    /*if (Jobject.getInt("is_provider") == 1) {
                        Toast.makeText(MainActivity.this, "Provider", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(MainActivity.this, "User", Toast.LENGTH_SHORT).show();*/

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void init() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
              //.baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
    }

}
