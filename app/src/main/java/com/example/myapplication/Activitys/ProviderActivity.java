package com.example.myapplication.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.Adapters.ProviderAdapter;
import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Models.ProviderReserves;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProviderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProviderAdapter mReserveAdapter;
    private ArrayList<ProviderReserves> mReserveList;
    private SharedPreferences pref;
    JsonPlaceHolder jsonPlaceHolder;
    private TextView tvProviderName,reservesNumber;
    private static final String TAG = "ProviderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        mRecyclerView = findViewById(R.id.recyclerViewProvider);
        tvProviderName = findViewById(R.id.providerName);
        reservesNumber = findViewById(R.id.numberOfReserves);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReserveList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);

        tvProviderName.setText(pref.getString("name", null));

        getReservation();
    }

    public void getReservation(){

        Call<List<ProviderReserves>> call = jsonPlaceHolder.getUsersReserves(pref.getString("id", null));
        call.enqueue(new Callback<List<ProviderReserves>>() {
            @Override
            public void onResponse(Call<List<ProviderReserves>> call, Response<List<ProviderReserves>> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: "+ response.code());
                    return;
                }

                List<ProviderReserves> reserves = response.body();
                reservesNumber.setText(reserves.size()+ " reserves");

                for (ProviderReserves reserve : reserves) {
                    String providerName = reserve.getName();
                    String email = reserve.getEmail();
                    String time = reserve.getTime();
                    String date = reserve.getDate();
                    Integer id = reserve.getId();
                    Integer approved = reserve.getApproved();
                    mReserveList.add(new ProviderReserves(providerName, email, time, date, id, approved));
                }

                mReserveAdapter = new ProviderAdapter(ProviderActivity.this, mReserveList);
                mRecyclerView.setAdapter(mReserveAdapter);

            }

            @Override
            public void onFailure(Call<List<ProviderReserves>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getMessage());
            }
        });

    }

    public void sendNotification()
    {
        Call<ResponseBody> call = jsonPlaceHolder.send();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.code());
                    return;
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
