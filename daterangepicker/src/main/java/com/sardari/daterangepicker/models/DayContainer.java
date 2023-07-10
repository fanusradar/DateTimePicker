package com.sardari.daterangepicker.models;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sardari.daterangepicker.utils.PersianCalendar;

import androidx.appcompat.widget.AppCompatImageView;

public class DayContainer {
    public final RelativeLayout rootView;
    public final TextView tvDate;
    public final TextView tvDateGeorgian;
    public final AppCompatImageView imgEvent;
    public final View strip;

    public DayContainer(RelativeLayout rootView) {
        this.rootView = rootView;
        strip = rootView.getChildAt(0);
        tvDate = (TextView) rootView.getChildAt(1);
        tvDateGeorgian = (TextView) rootView.getChildAt(3);
        imgEvent = (AppCompatImageView) rootView.getChildAt(2);
    }

    public static int GetContainerKey(PersianCalendar cal) {
        String str = cal.getPersianShortDate().replace("/", "");
        return Integer.parseInt(str);
    }

    public static PersianCalendar GetDateFromKey(String key) {
        PersianCalendar persianCalendar = new PersianCalendar();

        int _y = Integer.parseInt(key.substring(0, 4));
        int _m = Integer.parseInt(key.substring(4, 6));
        int _d = Integer.parseInt(key.substring(6));

        persianCalendar.setPersianDate(_y, _m - 1, _d);
        return persianCalendar;
    }
}
