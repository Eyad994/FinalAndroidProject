package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.ProviderReserves;
import com.example.myapplication.Activitys.ProviderActivity;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ReserveViewHolder> {

    private Context mContext;
    private ArrayList<ProviderReserves> mReserveList;
    private static final String TAG = "ProviderAdapter";
    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    public ProviderAdapter(Context mContext, ArrayList<ProviderReserves> mReserveList) {
        this.mContext = mContext;
        this.mReserveList = mReserveList;
    }

    @NonNull
    @Override
    public ReserveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_provider, parent, false);
        return new ReserveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReserveViewHolder holder, int position) {
        ProviderReserves currentItem = mReserveList.get(position);
        String providerName = currentItem.getName();
        String providerDate = currentItem.getDate();
        String providerTime = currentItem.getTime();
        holder.tvReserveName.setText(providerName);
        holder.tvProviderDate.setText(providerDate);
        holder.tvProviderTime.setText(providerTime);

        if (currentItem.getApproved() == 0) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnDecline.setVisibility(View.VISIBLE);
        }
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, pref.getString("firebaseToken", null), Toast.LENGTH_SHORT).show();
                ((ProviderActivity) mContext).sendNotification();
                /*mReserveList.clear();
                ((ProviderActivity) mContext).getReservation();
                notifyDataSetChanged();*/
            }
        });
        //Toast.makeText(mContext, ""+pref.getString("name", null), Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount() {
        return mReserveList.size();
    }

    public class ReserveViewHolder extends  RecyclerView.ViewHolder {
        //public ImageView mImageView;
        public TextView tvReserveName, tvProviderDate, tvProviderTime;
        public Button btnApprove, btnDecline;

        public ReserveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReserveName = itemView.findViewById(R.id.tvReserveName);
            tvProviderDate = itemView.findViewById(R.id.tvProviderDate);
            tvProviderTime = itemView.findViewById(R.id.tvProviderTime);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            pref = mContext.getSharedPreferences("MyPref", 0);
        }
    }
}
