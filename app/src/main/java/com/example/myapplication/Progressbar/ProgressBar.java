package com.example.myapplication.Progressbar;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

public class ProgressBar  {

    ProgressDialog loading = null;

    public void showProgress(Context context)
    {
        loading = new ProgressDialog(context);
        loading.setCancelable(true);
        loading.setMessage("Loading...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
    }

    public void dismissProgress()
    {
        this.loading.dismiss();
    }

}
