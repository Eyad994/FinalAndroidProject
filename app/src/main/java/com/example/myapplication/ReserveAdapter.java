package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Interface.JsonPlaceHolder;
import com.example.myapplication.Models.Reserves;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.ReserveViewHolder> {

    private Context mContext;
    private ArrayList<Reserves> mReserveList;
    private static final String TAG = "ReserveAdapter";
    String providerId;

    public ReserveAdapter(Context context, ArrayList<Reserves> reservesList){
        mContext =context;
        mReserveList = reservesList;
    }

    @NonNull
    @Override
    public ReserveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_user, parent, false);
        return new ReserveViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ReserveViewHolder holder, int position) {
        Reserves currentItem = mReserveList.get(position);
        String providerName = currentItem.getName();
        String time = currentItem.getTime();
        String date = currentItem.getDate();
        holder.mTextViewProvider.setText(providerName);
        holder.mTextViewTime.setText(time);
        holder.mTextViewDate.setText(date);

        if (currentItem.getApproved() == 2) {
            holder.mTextViewApproved.setText("Approved");
            holder.mTextViewApproved.setTextColor(Color.GREEN);
        }
        if (currentItem.getApproved() == 1) {
            holder.mTextViewApproved.setText("Disapproved");
            holder.mTextViewApproved.setTextColor(Color.RED);
        }
        if (currentItem.getApproved() == 0){
            holder.mTextViewApproved.setText("Pending");
            holder.mTextViewApproved.setTextColor(Color.BLUE);
        }

        holder.directions.setOnTouchListener((v, event) -> {
            v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
            return false;
        });

        holder.directions.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
            //Toast.makeText(mContext, "providerId: "+currentItem.getId(), Toast.LENGTH_SHORT).show();
            // Display a label at the location of Google's Sydney office
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+currentItem.getLatitude()+","+currentItem.getLongitude()));
            mapIntent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(mapIntent);
        });

        holder.delete.setOnClickListener(v -> {
            Toast.makeText(mContext, ""+currentItem.getId(), Toast.LENGTH_SHORT).show();

           /* setProviderId(String.valueOf(currentItem.getId()));
            openDateDialog(currentItem.getName());*/
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProviderId(String.valueOf(currentItem.getId()));
                openDateDialog(currentItem.getName());

            }
        });

    }

    public void openDateDialog(String title) {
        DialogMap exampleDialog2 = new DialogMap();
        exampleDialog2.setApiTitle(title);
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        exampleDialog2.show(fragmentManager, "example dialog");
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    @Override
    public int getItemCount() {
        return mReserveList.size();
    }



    public class ReserveViewHolder extends  RecyclerView.ViewHolder {
        //public ImageView mImageView;
        public TextView mTextViewProvider;
        public TextView mTextViewTime;
        public TextView mTextViewDate;
        public Button edit,delete;
        public ImageView directions;
        public TextView mTextViewApproved;


        public ReserveViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.image_provider);
            mTextViewProvider = itemView.findViewById(R.id.tvProviderName);
            mTextViewTime = itemView.findViewById(R.id.tvTime);
            mTextViewDate = itemView.findViewById(R.id.tvDate);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
            directions = itemView.findViewById(R.id.directions);
            mTextViewApproved = itemView.findViewById(R.id.tvApproved);

        }
    }
}
