package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Reserves;

import java.util.ArrayList;

public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.ReserveViewHolder> {

    private Context mContext;
    private ArrayList<Reserves> mReserveList;

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

        public ReserveViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.image_provider);
            mTextViewProvider = itemView.findViewById(R.id.tvProviderName);
            mTextViewTime = itemView.findViewById(R.id.tvTime);
            mTextViewDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
