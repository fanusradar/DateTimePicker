package com.fanus.datetimepicker;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;


import com.fanus.datetimepicker.databinding.ActivityMainBinding;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;

import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.fanus.datetimepicker.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(view -> {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                    datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
                    datePickerDialog.setEnableTimePicker(true);
                    datePickerDialog.setShowGregorianDate(false);
                    datePickerDialog.setTextSizeTitle(10.0f);
                    datePickerDialog.setTextSizeWeek(12.0f);
                    datePickerDialog.setTextSizeDate(14.0f);
                    datePickerDialog.setCanceledOnTouchOutside(true);
                    //datePickerDialog.setHolidayColor(getResources().getColor(R.color.colorRed));
                    datePickerDialog.setDisableDaysAgo(false);
                    datePickerDialog.setShowGregorianDate(false);
                    // datePickerDialog.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/IRSans.ttf"));
                    // datePickerDialog.setTodayColor(getResources().getColor(R.color.colorPrimaryDark));
                    // datePickerDialog.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    // datePickerDialog.setTextBtnAccept(getResources().getString(R.string.cancel), false);

                    /*long time = RealTime.isInitialized() ? RealTime.now().getTime() : new Date().getTime();
                    PersianCalendar persianCalendar = new PersianCalendar(time);
                    persianCalendar.setTimeZone(TimeZone.getTimeZone(GlobalVariable.TIME_ZONE_TEHRAN));
                    PersianDate persianDate = new PersianDate(time);
                    persianCalendar.setPersianDate(persianDate.getShYear(), persianDate.getShMonth() - 1, persianDate.getShDay());

                    datePickerDialog.setCurrentDate(persianCalendar);*/

                    // datePickerDialog.setAcceptButtonColor(context.getResources().getColor(R.color.colorPrimary));
                    datePickerDialog.setOnSingleDateSelectedListener(date -> {
                        if (date != null)
                            Snackbar.make(view, date.getPersianShortDateTime(), Snackbar.LENGTH_LONG)
                                    .setAnchorView(R.id.fab)
                                    .setAction("Action", null).show();
                    });
                    datePickerDialog.showDialog();
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}