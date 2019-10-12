package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Models.Reserves;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ReserveAdapter mReserveAdapter;
    private ArrayList<Reserves> mReserveList;
    private SharedPreferences pref;
    JsonPlaceHolder jsonPlaceHolder;

    private static final String TAG = "UserActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        mRecyclerView = findViewById(R.id.recyclerViewUser);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReserveList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);

        getReservation();
    }

    private void getReservation(){
        Call<List<Reserves>> call = jsonPlaceHolder.getReservation(pref.getString("id", null));
        call.enqueue(new Callback<List<Reserves>>() {
            @Override
            public void onResponse(Call<List<Reserves>> call, Response<List<Reserves>> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: "+ response.code());
                    return;
                }

                List<Reserves> reserves = response.body();

                for (Reserves reserve : reserves) {
                    String providerName = reserve.getName();
                    String email = reserve.getEmail();
                    String time = reserve.getTime();
                    String date = reserve.getDate();
                    Integer provider_id = reserve.getProviderId();
                    double latitude = reserve.getLatitude();
                    double longitude = reserve.getLongitude();

                    mReserveList.add(new Reserves(providerName, email, time, date, provider_id, latitude, longitude));
                }

                mReserveAdapter = new ReserveAdapter(UserActivity.this, mReserveList);
                mRecyclerView.setAdapter(mReserveAdapter);

            }

            @Override
            public void onFailure(Call<List<Reserves>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getMessage());
            }
        });

    }
}
