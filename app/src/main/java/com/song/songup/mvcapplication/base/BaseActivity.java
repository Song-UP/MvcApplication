package com.song.songup.mvcapplication.base;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description：描述信息
 * @Author：Song UP
 * @Date：2019/5/1 13:46
 * 修改备注：
 */
public abstract class BaseActivity extends AppCompatActivity {

    Unbinder unbinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setImmersive();

        setContentView(initView());
        unbinder = ButterKnife.bind(this);

        initData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null){
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroy();
    }

    //设置状态栏透明并且底部布局顶到了最上方
    public void setImmersive() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().setBackgroundDrawable(null);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    //设置状态栏透明，但是布局在状态栏的下面
    protected void setStatusBar(boolean isFullScreen, boolean isTextBlack, int staterBarColoe) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上 可以设置字体黑色
            int uiVisibility = -1;
            if (isFullScreen)
                uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;  //设置状态栏悬浮于自己的布局
            if (isTextBlack)
                uiVisibility = uiVisibility|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//字体黑色
            if (uiVisibility != -1)
                getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
            getWindow().setStatusBarColor(staterBarColoe);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上,可以设置状态栏悬浮
            if (isFullScreen) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
            }
            getWindow().setStatusBarColor(staterBarColoe);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            if (isFullScreen)
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

    }

    public void setFullScrWhite(){
        setStatusBar(true,  false,  Color.TRANSPARENT);
    }
    public void setFullScrBlack(){
        setStatusBar(true,  true,  Color.TRANSPARENT);
    }
    public void setFullScrColor(int staueColor){
        setStatusBar(true,  true,  staueColor);
    }

    public abstract int initView();

    public abstract void initData(Bundle savedInstanceState);
}
