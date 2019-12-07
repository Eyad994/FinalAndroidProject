package com.example.myapplication.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.DialogMap;
import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Models.Provider;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient.RetrofitClient;
import com.example.myapplication.UserActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DialogMap.ExampleDialogListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    JsonPlaceHolder jsonPlaceHolderApi;
    SharedPreferences pref;
    String providerId;
    JSONObject Jobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://cuturhair.azurewebsites.net/api/")
                //.baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(RetrofitClient.getClient())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolder.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllProviders();
            }
        }, 6000);

        FilterMenuLayout layout = findViewById(R.id.filter_menu);
        FilterMenu menu = new FilterMenu.Builder(this)
                //.addItem(R.drawable.facebook)
                .inflate(R.menu.menu_filter)//inflate  menu resource
                .attach(layout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                Toast.makeText(MapsActivity.this, "1", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(MapsActivity.this, "2", Toast.LENGTH_SHORT).show();
                                mMap.clear();
                                break;
                            case 2:
                                startActivity(new Intent(MapsActivity.this, UserActivity.class));
                                break;

                            default:
                                Toast.makeText(MapsActivity.this, "XXX", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }

                    @Override
                    public void onMenuCollapse() {
                    }

                    @Override
                    public void onMenuExpand() {
                    }
                })
                .build();
    }


    public void getAllProviders() {
        Call<List<Provider>> call = jsonPlaceHolderApi.getLatLng();

        call.enqueue(new Callback<List<Provider>>() {
            @Override
            public void onResponse(Call<List<Provider>> call, Response<List<Provider>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                List<Provider> providers = response.body();
                LatLng latLng;
                assert providers != null;
                for (Provider provider : providers) {
                    latLng = new LatLng(provider.getLatitude(), provider.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(provider.getName()).snippet(provider.getId().toString()));
                }
            }

            @Override
            public void onFailure(Call<List<Provider>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.scissor);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallIcon = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        LatLng AmmanCenter = new LatLng(31.953838, 35.910577);
        // Bitmap testMethod = resizeImage(getApplicationContext(), R.drawable.scissor,100,100);
        /*LatLng sydney = new LatLng(31.902765, 35.889524);
        LatLng amman = new LatLng(31.904623, 35.887657);
        // mMap.clear();
        mMap.addMarker(new MarkerOptions().position(AmmanCenter).title("Amman Center")).setAlpha(0.0f);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"))
                //  .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                .setIcon(BitmapDescriptorFactory.fromBitmap(smallIcon));
        mMap.addMarker(new MarkerOptions().position(amman).title("Marker in Amman"));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AmmanCenter, 12));

        mMap.setOnMarkerClickListener(marker -> {

            setProviderId(marker.getSnippet());
            openDateDialog(marker.getTitle());
            return false;
        });
    }

    public void openDateDialog(String title) {
        DialogMap exampleDialog2 = new DialogMap();
        exampleDialog2.setApiTitle(title);
        exampleDialog2.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyInputs(int year, int month, int day, int hour, int minute) {

        Log.d(TAG, "applyInputs: " + year + " " + month + "" + hour);
        String time = hour + ":" + minute;
        String date = day + "/" + month + "/" + year;
        Call<ResponseBody> call = jsonPlaceHolderApi.reserve(time, date, getProviderId(), pref.getString("id", null));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    return;
                }

                try {
                    Jobject = new JSONObject(response.body().string());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(MapsActivity.this, UserActivity.class));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
