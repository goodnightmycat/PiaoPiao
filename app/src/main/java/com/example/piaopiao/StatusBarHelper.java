package com.example.piaopiao;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;


/**
 * 状态栏管理类
 */
public class StatusBarHelper {
    /**
     * 不设置任何状态栏
     */
    public static final int NONE = 0;
    /**
     * 状态栏占用位置，颜色是白色，字体是黑色
     */
    public static final int SHOW_STATUS_BAR = 1;
    /**
     * 状态栏不占用位置，Activity可以从底下穿过，状态栏是透明，字体是深色
     */
    public static final int HINT_STATUS_BAR_TEXT_DARK = 2;
    /**
     * 状态栏不占用位置，Activity可以从底下穿过，状态栏是透明，字体是浅色
     */
    public static final int HINT_STATUS_BAR_TEXT_WHITE = 3;

    /**
     * 设置状态栏透明，同时可以修改状态栏颜色
     * 相当于状态栏是悬浮于我们的Activity之上，使得Activity可以从状态栏底下穿过
     */
    public static void setStatusBarHint(Activity activity, int color, boolean textColorDarkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 6.0以上系统
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            setStatusBarTextColor(activity, textColorDarkMode);
        }
    }

    /**
     * 设置状态栏为其他颜色，textColorDarkMode-true表示状态栏字体和图标为深色
     */
    public static void setStatusBarColor(Activity activity, int statusColor, boolean textColorDarkMode) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 6.0以上系统
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusColor);
            ViewGroup mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }

            setStatusBarTextColor(activity, textColorDarkMode);
        }
    }

    /**
     * 设置状态栏字体颜色
     */
    private static void setStatusBarTextColor(Activity activity, boolean textDarkMode) {
        // 安卓原生系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && textDarkMode) {
            // 6.0+，直接设置状态栏字体颜色为深色
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
