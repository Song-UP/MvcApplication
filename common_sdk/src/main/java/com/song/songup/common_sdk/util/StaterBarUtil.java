package com.song.songup.common_sdk.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * @Description：描述信息
 * @Author：Song UP
 * @Date：2019/8/12 17:21
 * 修改备注：
 */
public class StaterBarUtil {
    //设置状态栏透明，但是布局在状态栏的下面
    public static void setScreenFullWhite(Activity activity) {
        setStatusBar(activity,true, false, Color.TRANSPARENT);
    }
    public static void setScreenFullBlack(Activity activity) {
        setStatusBar(activity,true, true, Color.TRANSPARENT);
    }
    public static void setStatusBarTvColor(Activity activity,  boolean isTextBlack,int staterBarColoe) {
        setStatusBar(activity,false, isTextBlack,staterBarColoe);
    }

    //设置状态栏透明，但是布局在状态栏的下面
    protected static void setStatusBar(Activity activity, boolean isFullScreen, boolean isTextBlack, int staterBarColoe) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上 可以设置字体黑色
            int uiVisibility = -1;
            if (isFullScreen)
                uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;  //设置状态栏悬浮于自己的布局
            if (isTextBlack)
                uiVisibility = uiVisibility| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//字体黑色
            if (uiVisibility != -1)
                activity.getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
            activity.getWindow().setStatusBarColor(staterBarColoe);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上,可以设置状态栏悬浮
            if (isFullScreen) {
                View decorView = activity.getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
            }
            activity.getWindow().setStatusBarColor(staterBarColoe);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            if (isFullScreen)
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

    }
}
