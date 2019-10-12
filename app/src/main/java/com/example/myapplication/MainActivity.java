package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Interface.JsonPlaceHolder;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
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

        email = findViewById(R.id.atvEmailLog);
        password = findViewById(R.id.atvPasswordLog);
        login = findViewById(R.id.btnSignIn);
        signup = findViewById(R.id.btnSignUp);
        incorrect = findViewById(R.id.tvIncorrectPass);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String xEmail = email.getText().toString();
                String xPass = password.getText().toString();
                Log.d(TAG, "onClick: " + xEmail + xPass);
                makeLogin(xEmail, xPass);
            }
        });

    }


    private void makeLogin(String email, String password) {


        //Login login = new Login("userEmail@gmail.com", "password");
        Call<ResponseBody> call = jsonPlaceHolder.loginPost(email, password);

        Log.d(TAG, "makeLogin: " + email + password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    incorrect.setVisibility(View.VISIBLE);
                    return;
                }

                try {
                    Jobject = new JSONObject(response.body().string());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                /*try {
                    String accessToken = Jobject.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


                if (response.code() == 200) {
                    try {
                        editor.putString("accessToken", Jobject.getString("access_token"));
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    getUserDetails();

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                //Toast.makeText(MainActivity.this,  " response code: "+ response.code(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getMessage());
            }
        });
    }

    private void getUserDetails() {

        Call<ResponseBody> call = jsonPlaceHolder.getUser("Bearer " + pref.getString("accessToken", null));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    incorrect.setVisibility(View.VISIBLE);
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
}
