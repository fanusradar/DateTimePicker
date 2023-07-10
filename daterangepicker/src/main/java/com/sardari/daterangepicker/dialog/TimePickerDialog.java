package com.sardari.daterangepicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sardari.daterangepicker.R;

import java.util.Calendar;

public class TimePickerDialog extends Dialog {
    private TextView tvDialogDone;
    private TextView tvDialogCancel;

    private final String mTitle;
    private final Typeface typeface;

    public static int hours, minutes;

    private final TimePickerCallback onTimeChangedListener;

    public interface TimePickerCallback {
        void onTimeSelected(int hours, int minutes);

        void onCancel();
    }


    public TimePickerDialog(Context context, String title, Typeface typeface, TimePickerCallback timePickerCallback) {
        super(context);
        mTitle = title;
        this.onTimeChangedListener = timePickerCallback;
        this.typeface = typeface;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
        }
        setCanceledOnTouchOutside(false);

        initView();

        setListeners();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());

            //This makes the dialog take up the full width
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(layoutParams);
        }
    }

    private void initView() {
        setContentView(R.layout.dialog_time_picker);

        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvDialogDone = findViewById(R.id.tvHeaderDone);
        tvDialogCancel = findViewById(R.id.tvHeaderCancel);

        tvHeaderTitle.setTypeface(typeface);
        tvDialogDone.setTypeface(typeface);
        tvDialogCancel.setTypeface(typeface);

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            hours = hourOfDay;
            minutes = minute;
            Log.i("timePicker", "setOnTimeChangedListener_h=" + hourOfDay + " m=" + minute);
        });

        tvHeaderTitle.setText(mTitle);
    }

    private void setListeners() {
        tvDialogCancel.setOnClickListener(v -> {
            if (onTimeChangedListener != null) {
                onTimeChangedListener.onCancel();
            }
            TimePickerDialog.this.dismiss();
        });

        tvDialogDone.setOnClickListener(v -> {
            if (onTimeChangedListener != null) {
                onTimeChangedListener.onTimeSelected(hours, minutes);
            }
            Log.i("timePicker", "setOnClickListener_h=" + hours + " m=" + minutes);

            TimePickerDialog.this.dismiss();
        });
    }

    public void showDialog() {
        hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minutes = Calendar.getInstance().get(Calendar.MINUTE);
        this.show();
    }
}
