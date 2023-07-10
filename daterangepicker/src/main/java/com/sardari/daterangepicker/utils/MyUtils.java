package com.sardari.daterangepicker.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MyUtils {
    private static final MyUtils INSTANCE = new MyUtils();
    private OnShowToastListener onShowToastListener;

    private MyUtils() {
    }

    public static MyUtils getInstance() {
        return INSTANCE;
    }

    public void setOnShowToastListener(OnShowToastListener onShowToastListener) {
        this.onShowToastListener = onShowToastListener;
    }

    public void showToast(Context context, String message) {
        if (onShowToastListener != null) {
            onShowToastListener.onShow(message);

        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 200);
            toast.show();
        }
    }

    public interface OnShowToastListener {
        void onShow(String message);
    }
}