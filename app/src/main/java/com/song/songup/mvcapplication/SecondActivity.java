package com.song.songup.mvcapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.song.songup.mvcapplication.base.BaseActivity;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setImmersive();
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView() {
        return R.layout.activity_main02;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        TextView textView = findViewById(R.id.textview);
        textView.setText("setImmersive()");

    }
}
