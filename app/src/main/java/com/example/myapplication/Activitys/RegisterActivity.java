package com.example.myapplication.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.R;
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

public class RegisterActivity extends AppCompatActivity {

    TextView login, incorrect;
    EditText userName, email, password;
    Button register;
    private JsonPlaceHolder jsonPlaceHolder;
    private static final String TAG = "RegisterActivity";
    private SharedPreferences pref;
    JSONObject Jobject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userName = findViewById(R.id.atvUsernameReg);
        email = findViewById(R.id.atvEmailReg);
        password = findViewById(R.id.atvPasswordReg);
        register = findViewById(R.id.signUp);
        login = findViewById(R.id.tvSignIn);
        incorrect = findViewById(R.id.tvIncorrect);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);

        Log.d(TAG, "makeRegister: "+pref.getString("firebaseToken", null));
        login.setOnClickListener(v ->{
            finish();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        });

        register.setOnClickListener(v -> {
            String xEmail = email.getText().toString();
            String xPass = password.getText().toString();
            String xUserName = userName.getText().toString();

            makeRegister(xEmail, xPass, xUserName);
        });

    }

    private void makeRegister(String email, String password, String userName)
    {
        String deviceToken = pref.getString("firebaseToken", null);

        Call<ResponseBody> call = jsonPlaceHolder.register(email, password, password, userName, deviceToken);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: "+ response.body());
                    incorrect.setVisibility(View.VISIBLE);
                    return;
                }

                try {
                    assert response.body() != null;
                    Jobject = new JSONObject(response.body().string());
                    if (response.code() == 201) {
                        finish();
                        Toast.makeText(RegisterActivity.this, "Registration successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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
}
