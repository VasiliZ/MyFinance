package com.github.vasiliz.myfinance;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;


    public void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "", "wait");
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
