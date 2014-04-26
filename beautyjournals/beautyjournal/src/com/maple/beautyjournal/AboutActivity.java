package com.maple.beautyjournal;

import com.maple.beautyjournal.base.BaseActivity;

import android.os.Bundle;
import android.view.View;

public class AboutActivity extends BaseActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    public void onBack(View v){
        onBackPressed();
    }
}
