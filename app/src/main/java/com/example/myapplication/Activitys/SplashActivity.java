package com.example.myapplication.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Interface.JsonPlaceHolder;
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

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView logoSplash, chmaraTech, logoWhite;
    private Animation anim1, anim2, anim3;
    private SharedPreferences pref;
    JSONObject Jobject;
    SharedPreferences.Editor editor;
    private JsonPlaceHolder jsonPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_scr);
        init();

        editor.putString("firebaseToken", FirebaseInstanceId.getInstance().getToken());
        editor.apply();

        logoSplash.startAnimation(anim1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoSplash.startAnimation(anim2);
                logoSplash.setVisibility(View.GONE);

                logoWhite.startAnimation(anim3);
                chmaraTech.startAnimation(anim3);
                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoWhite.setVisibility(View.VISIBLE);
                        chmaraTech.setVisibility(View.VISIBLE);

                        finish();
                        if (pref.getString("accessToken", null) != null) {
                            getUserDetails();
                            String is_provider = pref.getString("is_provider", null);

                            assert is_provider != null;
                            if (is_provider.equals("1")) {
                                startActivity(new Intent(SplashActivity.this, ProviderActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                            }

                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
                    Toast.makeText(SplashActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Jobject = new JSONObject(response.body().string());
                    editor.putString("email", Jobject.getString("email"));
                    editor.putString("id", Jobject.getString("id"));
                    editor.putString("name", Jobject.getString("name"));
                    editor.putString("is_provider", Jobject.getString("is_provider"));
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
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        logoSplash = findViewById(R.id.ivLogoSplash);
        logoWhite = findViewById(R.id.ivLogoWhite);
        chmaraTech = findViewById(R.id.ivCHTtext);
        anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout);
        anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
    }


}

