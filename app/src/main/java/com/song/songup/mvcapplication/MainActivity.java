package com.song.songup.mvcapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.song.songup.common_sdk.util.StaterBarUtil;
import com.song.songup.mvcapplication.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        StaterBarUtil.setScreenFullWhite(this);
//        setFullScrBlack();
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView() {
        return R.layout.activity_main02;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        TextView textView = findViewById(R.id.textview);
        textView.setText("setStatusBar()");

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });


//        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
//            @Override
//            public void onRequestPermissionSuccess() {
//
//            }
//            @Override
//            public void onRequestPermissionFailure(List<String> permissions) {
//
//            }
//            @Override
//            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
//
//            }
//        }, new RxPermissions(this),  RxErrorHandler.builder().with(this).responseErrorListener(new ResponseErrorListener() {
//            @Override
//            public void handleResponseError(Context context, Throwable t) {
//
//            }
//        }).build());

    }
}
