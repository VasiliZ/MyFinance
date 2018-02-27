package com.github.vasiliz.myfinance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class BaseActivity extends AppCompatActivity {

    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar_layout);
    }

    public void startShowProgressOperation(){
        View view = View.inflate(this,R.layout.progressbar_layout, null);
        view = findViewById(R.id.linlaHeaderProgress);
        view.setVisibility(View.VISIBLE);
    }

    public void endShowProgressOperation(){
        LinearLayout linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);
    }

}
