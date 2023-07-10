package com.sardari.daterangepicker.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sardari.daterangepicker.R;
import com.sardari.daterangepicker.dialog.TimePickerDialog;
import com.sardari.daterangepicker.models.DayContainer;
import com.sardari.daterangepicker.utils.FontUtils;
import com.sardari.daterangepicker.utils.MyUtils;
import com.sardari.daterangepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.core.content.ContextCompat;

import static com.sardari.daterangepicker.utils.PersianCalendarConstants.persianMonthNames;

public class DateRangeCalendarView extends LinearLayout {
    private final Context mContext;
    private final AttributeSet attrs;
    private LinearLayout llDaysContainer;
    private LinearLayout llTitleWeekContainer, linNavHeader;
    private TextView tvYearTitle;
    private TextView tvYearGeorgianTitle;
    private ImageView imgVNavLeft, imgVNavRight;
    private Locale locale;

    private PersianCalendar currentCalendarMonth, minSelectedDate, maxSelectedDate;
    private final ArrayList<Integer> selectedDatesRange = new ArrayList<>();
    private Typeface typeface;
    private RelativeLayout rlHeaderCalendar;

    private static final int STRIP_TYPE_NONE = 0;
    private static final int STRIP_TYPE_LEFT = 1;
    private static final int STRIP_TYPE_RIGHT = 2;

    private int headerBackgroundColor, weekColor, rangeStripColor, selectedDateCircleColor, selectedDateColor, defaultDateColor, disableDateColor, rangeDateColor, holidayColor,
            todayColor, startYear;
    private boolean shouldEnabledTime = false;
    private float textSizeTitle, textSizeWeek, textSizeDate;
    private PersianCalendar selectedCal;
    private NumberPickerPosition yearPickerPosition;

    public void setYearPickerPosition(NumberPickerPosition yearPickerPosition) {
        this.yearPickerPosition = yearPickerPosition;
    }

    //region Enum
    public enum SelectionMode {
        Single(1),
        Range(2),
        None(3),
        Both(4);

        private final int value;

        SelectionMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum NumberPickerPosition {
        START,
        MIDDLE,
        END
    }

    private int selectionMode = SelectionMode.Range.getValue();
    private int selectedYear = 1380;
    private int selectedMonth = 1;
    private PersianCalendar currentDate = new PersianCalendar();
    private PersianCalendar maxDate;
    private boolean isDisableDaysAgo = true;
    private boolean isDisableDaysAfter = true;
    private boolean showGregorianDate = false;
    String messageEnterEndDate;
    private CalendarListener calendarListener;


    //region Constructor
    public DateRangeCalendarView(Context context) {
        super(context);

        this.mContext = context;
        this.attrs = null;

        initView();
    }

    public DateRangeCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        this.attrs = attrs;

        initView();
    }

    private void initView() {
        typeface = FontUtils.Default(mContext);
        locale = mContext.getResources().getConfiguration().locale;

        setDefaultValues();

        setAttributes();

        init();
    }

    private void setDefaultValues() {
        textSizeTitle = getResources().getDimension(R.dimen.text_size_title);
        textSizeWeek = getResources().getDimension(R.dimen.text_size_week);
        textSizeDate = getResources().getDimension(R.dimen.text_size_date);

        headerBackgroundColor = ContextCompat.getColor(mContext, R.color.headerBackgroundColor);

        weekColor = ContextCompat.getColor(mContext, R.color.week_color);
        selectedDateCircleColor = ContextCompat.getColor(mContext, R.color.selected_date_circle_color);
        selectedDateColor = ContextCompat.getColor(mContext, R.color.selected_date_color);
        defaultDateColor = ContextCompat.getColor(mContext, R.color.default_date_color);
        rangeDateColor = ContextCompat.getColor(mContext, R.color.range_date_color);
        disableDateColor = ContextCompat.getColor(mContext, R.color.disable_date_color);

        rangeStripColor = ContextCompat.getColor(mContext, R.color.range_bg_color);
        holidayColor = ContextCompat.getColor(mContext, R.color.holiday_date_color);
        todayColor = ContextCompat.getColor(mContext, R.color.today_date_color);
    }

    public void setAttributes() {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.DateRangeCalendarView, 0, 0);
        try {
            shouldEnabledTime = ta.getBoolean(R.styleable.DateRangeCalendarView_enable_time_selection, shouldEnabledTime);

            //text size
            textSizeTitle = ta.getDimension(R.styleable.DateRangeCalendarView_text_size_title, textSizeTitle);
            textSizeWeek = ta.getDimension(R.styleable.DateRangeCalendarView_text_size_week, textSizeWeek);
            textSizeDate = ta.getDimension(R.styleable.DateRangeCalendarView_text_size_date, textSizeDate);

            //header color
            headerBackgroundColor = ta.getColor(R.styleable.DateRangeCalendarView_header_background_color, headerBackgroundColor);

            //weekColor
            weekColor = ta.getColor(R.styleable.DateRangeCalendarView_week_color, weekColor);
            selectedDateCircleColor = ta.getColor(R.styleable.DateRangeCalendarView_selected_date_circle_color, selectedDateCircleColor);
            selectedDateColor = ta.getColor(R.styleable.DateRangeCalendarView_selected_date_color, selectedDateColor);
            defaultDateColor = ta.getColor(R.styleable.DateRangeCalendarView_default_date_color, defaultDateColor);
            rangeDateColor = ta.getColor(R.styleable.DateRangeCalendarView_range_date_color, rangeDateColor);
            disableDateColor = ta.getColor(R.styleable.DateRangeCalendarView_disable_date_color, disableDateColor);
            rangeStripColor = ta.getColor(R.styleable.DateRangeCalendarView_range_color, rangeStripColor);
            holidayColor = ta.getColor(R.styleable.DateRangeCalendarView_holidayColor, holidayColor);
            todayColor = ta.getColor(R.styleable.DateRangeCalendarView_todayColor, todayColor);

            selectionMode = ta.getInt(R.styleable.DateRangeCalendarView_selectionMode, selectionMode);
        } catch (Exception e) {
            Log.e("setAttributes", e.getMessage());
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        LinearLayout mainView = (LinearLayout) layoutInflater.inflate(R.layout.layout_calendar_month, this, true);
        llDaysContainer = mainView.findViewById(R.id.llDaysContainer);
        llTitleWeekContainer = mainView.findViewById(R.id.llTitleWeekContainer);
        tvYearTitle = mainView.findViewById(R.id.tvYearTitle);
        tvYearGeorgianTitle = mainView.findViewById(R.id.tvYearGeorgianTitle);
        imgVNavLeft = mainView.findViewById(R.id.imgVNavLeft);
        imgVNavRight = mainView.findViewById(R.id.imgVNavRight);
        linNavHeader = mainView.findViewById(R.id.linNavHeader);

        rlHeaderCalendar = mainView.findViewById(R.id.rlHeaderCalendar);

        setListeners();

        if (isInEditMode()) {
            return;
        }

        build();
    }
    //endregion

    //region Core
    //region NavigationClickListener & dayClickListener
    private void nextMonth() {
        currentCalendarMonth.setPersianMonth(currentCalendarMonth.getPersianMonth() + 1);
        currentCalendarMonth.setPersianDay(1);

        drawCalendarForMonth(currentCalendarMonth);
    }

    private void prevMonth() {
        if (currentCalendarMonth.getPersianMonth() == 0) {
            currentCalendarMonth.setPersianYear(currentCalendarMonth.getPersianYear() - 1);
            currentCalendarMonth.setPersianMonth(11);
        } else {
            currentCalendarMonth.setPersianMonth(currentCalendarMonth.getPersianMonth() - 1);
        }

        currentCalendarMonth.setPersianDay(1);

        drawCalendarForMonth(currentCalendarMonth);
    }

    private void prevYearsAndMonth() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_year_month, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(view);
        AlertDialog alertDialog = alert.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText(String.format(locale, "%s %d", currentCalendarMonth.getPersianMonthName(), currentCalendarMonth.getPersianYear()));
        txt_title.setTypeface(typeface);
        NumberPicker numPickerYear = view.findViewById(R.id.numPickerYear);
        NumberPicker numPickerMouth = view.findViewById(R.id.numPickerMouth);

        List<String> yearsList = new ArrayList<>();
        int currentYear = new PersianCalendar().getPersianYear();

        for (int year = startYear; year <= currentYear; year++)
            yearsList.add(String.valueOf(year));

        selectedYear = currentCalendarMonth.getPersianYear();
        selectedMonth = currentCalendarMonth.getPersianMonth();

        int lastYearIndex = yearsList.size() - 1;

        if (yearsList.size() > 0) {
            numPickerYear.setDisplayedValues(yearsList.toArray(new String[]{}));
            numPickerYear.setMaxValue(lastYearIndex);
        }
        numPickerYear.setMinValue(0);
        numPickerYear.setWrapSelectorWheel(false);
        numPickerYear.setOnValueChangedListener((picker, oldVal, newVal) -> {
            selectedYear = Integer.parseInt(yearsList.get(newVal));
            txt_title.setText(String.format(locale, "%s %d", persianMonthNames[selectedMonth], selectedYear));
        });

        switch (yearPickerPosition) {
            case START -> numPickerYear.setValue(0);
            case MIDDLE -> numPickerYear.setValue(lastYearIndex / 2);
            case END -> numPickerYear.setValue(lastYearIndex);
        }

        numPickerMouth.setDisplayedValues(persianMonthNames);
        numPickerMouth.setMaxValue(persianMonthNames.length - 1);
        numPickerMouth.setMinValue(0);
        numPickerMouth.setValue(selectedMonth);
        numPickerMouth.setWrapSelectorWheel(false);
        numPickerMouth.setOnValueChangedListener((picker, oldVal, newVal) -> {
            selectedMonth = newVal;
            txt_title.setText(String.format(locale, "%s %d", persianMonthNames[selectedMonth], selectedYear));
        });

        alertDialog.show();

        TextView txt_ok = view.findViewById(R.id.txt_ok);
        txt_ok.setTypeface(typeface);
        txt_ok.setOnClickListener(v -> {
            selectedMonth = numPickerMouth.getValue();
            selectedYear = Integer.parseInt(yearsList.get(numPickerYear.getValue()));
            currentCalendarMonth.setPersianMonth(selectedMonth);
            currentCalendarMonth.setPersianYear(selectedYear);
            currentCalendarMonth.setPersianDay(1);
            drawCalendarForMonth(currentCalendarMonth);
            txt_title.setText(String.format(locale, "%s %d", persianMonthNames[selectedMonth], selectedYear));
            alertDialog.dismiss();
        });

        TextView txt_cancel = view.findViewById(R.id.txt_cancel);
        txt_cancel.setTypeface(typeface);
        txt_cancel.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void setListeners() {
        //region imgVNavLeft.setOnClickListener
        imgVNavLeft.setOnClickListener(v -> nextMonth());
        //endregion

        //region imgVNavRight.setOnClickListener
        imgVNavRight.setOnClickListener(v -> prevMonth());
        //endregion

        // region linNavHeader.setOnClickListener
        linNavHeader.setOnClickListener(v -> prevYearsAndMonth());
        //endregion
    }

    private final OnClickListener dayClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            DayContainer container = new DayContainer((RelativeLayout) view);
            int key = (int) view.getTag();

            selectedCal = new PersianCalendar();
            PersianCalendar date = DayContainer.GetDateFromKey(String.valueOf(key));
            selectedCal.setPersianDate(date.getPersianYear(), date.getPersianMonth(), date.getPersianDay());

            if (selectionMode == SelectionMode.Single.getValue()) {
                //region SelectionMode.Single
                resetAllSelectedViews();
                makeAsSelectedDate(container, 0);

                if (shouldEnabledTime) {
                    //region shouldEnabledTime
                    TimePickerDialog awesomeTimePickerDialog = new TimePickerDialog(mContext, mContext.getString(R.string.title_time_picker), typeface,
                            new TimePickerDialog.TimePickerCallback() {
                                @Override
                                public void onTimeSelected(int hours, int minutes) {
                                    PersianCalendar p = (PersianCalendar) selectedCal.clone();

                                    selectedCal.set(Calendar.HOUR, hours);
                                    selectedCal.set(Calendar.MINUTE, minutes);
                                    selectedCal.set(Calendar.SECOND, 0);
                                    selectedCal.set(Calendar.MILLISECOND, 0);
                                    selectedCal.setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());

                                    Log.i("timePicker", "onTimeSelected_1_h=" + hours + " m=" + minutes);

                                    if (calendarListener != null) {
                                        calendarListener.onDateSelected(selectedCal);
                                        Log.i("timePicker", "onTimeSelected_2_h=" + hours + " m=" + minutes);

                                    }
                                }

                                @Override
                                public void onCancel() {
                                    resetAllSelectedViews();
                                }
                            });
                    awesomeTimePickerDialog.showDialog();
                    //endregion
                } else {
                    //region SelectDateWithoutTime
                    if (calendarListener != null) {
                        calendarListener.onDateSelected(selectedCal);
                    }
                    //endregion
                }
                //endregion
            } else if (selectionMode == SelectionMode.Range.getValue() || selectionMode == SelectionMode.Both.getValue()) {
                //region SelectionMode.Range
                if (minSelectedDate != null) {
                    if (maxSelectedDate == null) {
                        maxSelectedDate = selectedCal;
                        drawSelectedDateRange(minSelectedDate, maxSelectedDate);
                    } else {
                        if (!TextUtils.isEmpty(getMessageEnterEndDate())) {
                            MyUtils.getInstance().showToast(mContext, getMessageEnterEndDate());
                        }

                        resetAllSelectedViews();

                        minSelectedDate = selectedCal;
                        maxSelectedDate = null;

                        makeAsSelectedDate(container, 0);
                    }
                } else {
                    if (!TextUtils.isEmpty(getMessageEnterEndDate())) {
                        MyUtils.getInstance().showToast(mContext, getMessageEnterEndDate());
                    }

                    minSelectedDate = selectedCal;
                    maxSelectedDate = null;

                    makeAsSelectedDate(container, 0);
                }

                if (shouldEnabledTime) {
                    Log.w("TAG", "DateRangeCalendarView_onClick_296-> :");

                    //region shouldEnabledTime
                    TimePickerDialog awesomeTimePickerDialog = new TimePickerDialog(mContext, mContext.getString(R.string.title_time_picker), typeface,
                            new TimePickerDialog.TimePickerCallback() {
                                @Override
                                public void onTimeSelected(int hours, int minutes) {
                                    PersianCalendar p = (PersianCalendar) selectedCal.clone();

                                    selectedCal.set(Calendar.HOUR, hours);
                                    selectedCal.set(Calendar.MINUTE, minutes);
                                    selectedCal.set(Calendar.SECOND, 0);
                                    selectedCal.set(Calendar.MILLISECOND, 0);

                                    Log.i("timePicker", "onTimeSelected_h=" + hours + " m=" + minutes);


                                    selectedCal.setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());

                                    if (calendarListener != null && minSelectedDate != null && maxSelectedDate != null) {
                                        calendarListener.onDateRangeSelected(minSelectedDate, maxSelectedDate);
                                    }
                                }

                                @Override
                                public void onCancel() {
                                    resetAllSelectedViews();
                                }
                            });
                    awesomeTimePickerDialog.showDialog();
                    //endregion
                } else {
                    //region SelectDateWithoutTime
                    if (calendarListener != null && minSelectedDate != null && maxSelectedDate != null) {
                        calendarListener.onDateRangeSelected(minSelectedDate, maxSelectedDate);
                    }
                    //endregion
                }
                //endregion
            }
        }
    };
    //endregion
    //---------------------------------------------------------------------------------------------

    /**
     * To clone calendar obj and get current month calendar starting from 1st date.
     *
     * @param calendar - Calendar
     * @return - Modified calendar obj of month of 1st date.
     */
    private PersianCalendar getCurrentMonth(PersianCalendar calendar) {
        PersianCalendar current = (PersianCalendar) calendar.clone();
        current.setPersianDay(1);
        return current;
    }

    /**
     * To draw calendar for the given month. Here calendar object should start from date of 1st.
     *
     * @param month month
     */
    private void drawCalendarForMonth(PersianCalendar month) {
        tvYearTitle.setTextSize(textSizeTitle);

        tvYearTitle.setText(String.format(locale, "%s %d", month.getPersianMonthName(), month.getPersianYear()));
        Log.e(" drawCalendarForMonth", "year: " + (month.getPersianYear()));
        Log.e(" drawCalendarForMonth", "Month: " + (month.getPersianMonth()));
        int _month = month.getPersianMonth() + 1;
        switch (_month) {
            case 1 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "March - April %d", month.get(Calendar.YEAR)));
            case 2 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "April - May %d", month.get(Calendar.YEAR)));
            case 3 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "May - June %d", month.get(Calendar.YEAR)));
            case 4 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "June - July %d", month.get(Calendar.YEAR)));
            case 5 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "July - August %d", month.get(Calendar.YEAR)));
            case 6 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "August - September %d", month.get(Calendar.YEAR)));
            case 7 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "September - October %d", month.get(Calendar.YEAR)));
            case 8 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "October - November %d", month.get(Calendar.YEAR)));
            case 9 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "November - December %d", month.get(Calendar.YEAR)));
            case 10 ->
                    tvYearGeorgianTitle.setText(String.format("December %s - January %s ", month.get(Calendar.YEAR), month.get(Calendar.YEAR) + 1));
            case 11 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "January - February %d", month.get(Calendar.YEAR)));
            case 12 ->
                    tvYearGeorgianTitle.setText(String.format(locale, "February - March %d", month.get(Calendar.YEAR)));
        }

        currentCalendarMonth = (PersianCalendar) month.clone();

        int startDay = month.get(Calendar.DAY_OF_WEEK);
        int persianDay = month.getPersianDay();

        if (startDay != 7) {
            month.setPersianDay(persianDay - startDay);
        }

        for (int i = 0; i < llDaysContainer.getChildCount(); i++) {
            LinearLayout weekRow = (LinearLayout) llDaysContainer.getChildAt(i);

            for (int j = 0; j < 7; j++) {
                RelativeLayout rlDayContainer = (RelativeLayout) weekRow.getChildAt(j);
                DayContainer container = new DayContainer(rlDayContainer);

                if (typeface != null) {
                    container.tvDate.setTypeface(typeface);
                }

                container.tvDate.setTextSize(textSizeDate);

                drawDayContainer(container, month);

                month.setPersianDay(month.getPersianDay() + 1);
            }
        }
    }

    /**
     * To draw specific date container according to past date, today, selected or from range.
     *
     * @param container - Date container
     * @param calendar  - Calendar obj of specific date of the month.
     */
    private void drawDayContainer(DayContainer container, PersianCalendar calendar) {
        int date = calendar.getPersianDay();
        int dateGR = calendar.get(Calendar.DATE);

        //----------------------------------------------------------------
        if (currentCalendarMonth.getPersianMonth() != calendar.getPersianMonth()) {
            hideDayContainer(container);
        } else if (isDisableDaysAgo && getCurrentDate().after(calendar) && (getCurrentDate().get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR))) {
            disableDayContainer(container);

            setToday(container, calendar);
        } else if (isDisableDaysAfter && getCurrentDate().before(calendar) && (getCurrentDate().get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR))) {
            disableDayContainer(container);

            setToday(container, calendar);
        } else {
            int key = DayContainer.GetContainerKey(calendar);

            setToday(container, calendar);

            if (selectedDatesRange.indexOf(key) == 0) {
                makeAsSelectedDate(container, STRIP_TYPE_LEFT);
            } else if (selectedDatesRange.size() != 0 && selectedDatesRange.indexOf(key) == selectedDatesRange.size() - 1) {
                makeAsSelectedDate(container, STRIP_TYPE_RIGHT);
            } else if (selectedDatesRange.contains(key)) {
                makeAsRangeDate(container);
            } else {
                enabledDayContainer(container);
                selectHolidayContainer(container, calendar);
            }

            //---check date selected-------------------------------------------------------------
            if ((selectionMode == SelectionMode.Single.getValue()) && (selectedCal != null && calendar.getPersianShortDate().equals(selectedCal.getPersianShortDate()))) {
                makeAsSelectedDate(container, STRIP_TYPE_LEFT);
            } else if ((selectionMode == SelectionMode.Range.getValue()) && (minSelectedDate != null && calendar.getPersianShortDate().equals(minSelectedDate.getPersianShortDate()))) {
                makeAsSelectedDate(container, STRIP_TYPE_LEFT);
            }

            //---disable max date-------------------------------------------------------------
            if ((getMaxDate() != null) && (getMaxDate().before(calendar))) {
                disableDayContainer(container);
//                container.tvDate.setText(String.valueOf(date));
            }
        }

        container.tvDate.setText(String.valueOf(date));
        container.tvDateGeorgian.setText(String.valueOf(dateGR));
        container.tvDateGeorgian.setVisibility(showGregorianDate ? VISIBLE : GONE);
        container.rootView.setTag(DayContainer.GetContainerKey(calendar));
    }

    //---------------------------------------------------------------------------------------------
    private void setToday(DayContainer container, PersianCalendar persianCalendar) {
        if (getCurrentDate().getPersianShortDate().compareTo(persianCalendar.getPersianShortDate()) == 0) {
            container.imgEvent.setVisibility(VISIBLE);
            container.imgEvent.setColorFilter(todayColor, android.graphics.PorterDuff.Mode.SRC_IN);
//            container.tvDate.setTextColor(todayColor);
            container.tvDate.setTypeface(typeface, Typeface.BOLD);
        } else {
            container.imgEvent.setVisibility(GONE);
            container.tvDate.setTypeface(typeface, Typeface.NORMAL);
        }
    }

    /**
     * To hide date if date is from previous month.
     *
     * @param container - Container
     */
    private void hideDayContainer(DayContainer container) {
        container.tvDate.setText("");
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setVisibility(INVISIBLE);
        container.rootView.setOnClickListener(null);
    }

    /**
     * To disable past date. Click listener will be removed.
     *
     * @param container - Container
     */
    private void disableDayContainer(DayContainer container) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.tvDate.setTextColor(disableDateColor);
        container.rootView.setVisibility(VISIBLE);
        container.rootView.setOnClickListener(null);
    }

    /**
     * To enable date by enabling click listeners.
     *
     * @param container - Container
     */
    private void enabledDayContainer(DayContainer container) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        container.strip.setBackgroundColor(Color.TRANSPARENT);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.tvDate.setTextColor(defaultDateColor);
        container.rootView.setVisibility(VISIBLE);
        container.rootView.setOnClickListener(dayClickListener);
    }

    /**
     * To enable date by enabling click listeners.
     *
     * @param container - Container
     */
    private void selectHolidayContainer(DayContainer container, PersianCalendar _calendar) {
        if (_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            container.tvDate.setTextColor(holidayColor);
        }

        if (_calendar.getPersianMonth() + 1 == 1) {
            if (_calendar.getPersianDay() == 1 ||
                    _calendar.getPersianDay() == 2 ||
                    _calendar.getPersianDay() == 3 ||
                    _calendar.getPersianDay() == 4 ||
                    _calendar.getPersianDay() == 13) {
                container.tvDate.setTextColor(holidayColor);
            }
        }

        if (_calendar.getPersianMonth() + 1 == 12) {
            if (_calendar.getPersianDay() == 29 || _calendar.getPersianDay() == 30) {
                container.tvDate.setTextColor(holidayColor);
            }
        }
    }

    /**
     * To draw date container as selected as end selection or middle selection.
     *
     * @param container - Container
     * @param stripType - Right end date, Left end date or middle
     */
    private void makeAsSelectedDate(DayContainer container, int stripType) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.strip.getLayoutParams();
        if (stripType == STRIP_TYPE_LEFT) {
            GradientDrawable mDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.range_bg_left);
            assert mDrawable != null;
            mDrawable.setColor(rangeStripColor);
            container.strip.setBackground(mDrawable);
            layoutParams.setMargins(0, 0, 20, 0);
        } else if (stripType == STRIP_TYPE_RIGHT) {
            GradientDrawable mDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.range_bg_right);
            assert mDrawable != null;
            mDrawable.setColor(rangeStripColor);
            container.strip.setBackground(mDrawable);
            layoutParams.setMargins(20, 0, 0, 0);
        } else {
            container.strip.setBackgroundColor(Color.TRANSPARENT);
            layoutParams.setMargins(0, 0, 0, 0);
        }

        container.strip.setLayoutParams(layoutParams);
        GradientDrawable mDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.shape_rect);
        assert mDrawable != null;
        mDrawable.setColor(selectedDateCircleColor);
        container.tvDate.setBackground(mDrawable);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.tvDate.setTextColor(selectedDateColor);
//        container.imgEvent.setColorFilter(selectedDateColor, PorterDuff.Mode.SRC_IN);
        container.rootView.setVisibility(VISIBLE);
    }

    /**
     * To draw date as middle date
     *
     * @param container - Container
     */
    private void makeAsRangeDate(DayContainer container) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT);
        GradientDrawable mDrawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.range_bg);
        assert mDrawable != null;
        mDrawable.setColor(rangeStripColor);
        container.strip.setBackground(mDrawable);
        container.rootView.setBackgroundColor(Color.TRANSPARENT);
        container.tvDate.setTextColor(rangeDateColor);
//        container.imgEvent.setColorFilter(rangeDateColor, android.graphics.PorterDuff.Mode.SRC_IN);
        container.rootView.setVisibility(VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.strip.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        container.strip.setLayoutParams(layoutParams);
    }

    /**
     * To draw selected date range.
     *
     * @param startDate - Start date
     * @param lastDate  - End date
     */
    private void drawSelectedDateRange(PersianCalendar startDate, PersianCalendar lastDate) {
        selectedDatesRange.clear();
        int startDateKey = DayContainer.GetContainerKey(startDate);
        int lastDateKey = DayContainer.GetContainerKey(lastDate);

        PersianCalendar temp = (PersianCalendar) startDate.clone();

        if (startDateKey != lastDateKey) {
            if (startDateKey > lastDateKey) {
                maxSelectedDate = startDate;
                minSelectedDate = lastDate;

                int tempXyz = startDateKey;
                startDateKey = lastDateKey;
                lastDateKey = tempXyz;

                temp = (PersianCalendar) lastDate.clone();
            }
        }

        RelativeLayout rlDayContainer = llDaysContainer.findViewWithTag(startDateKey);

        if (rlDayContainer != null && minSelectedDate.getPersianMonth() == currentCalendarMonth.getPersianMonth()) {
            DayContainer container = new DayContainer(rlDayContainer);
            int stripType = STRIP_TYPE_LEFT;
            if (startDateKey == lastDateKey) {
                stripType = STRIP_TYPE_NONE;
            }

            makeAsSelectedDate(container, stripType);
        }

        rlDayContainer = llDaysContainer.findViewWithTag(lastDateKey);
        if (rlDayContainer != null && maxSelectedDate.getPersianMonth() == currentCalendarMonth.getPersianMonth()) {
            DayContainer container = new DayContainer(rlDayContainer);
            int stripType = STRIP_TYPE_RIGHT;
            if (startDateKey == lastDateKey) {
                stripType = STRIP_TYPE_NONE;
            }
            makeAsSelectedDate(container, stripType);
        }

        selectedDatesRange.add(startDateKey);
        temp.setPersianDate(temp.getPersianYear(), temp.getPersianMonth(), temp.getPersianDay() + 1);

        int nextDateKey = DayContainer.GetContainerKey(temp);
        while (lastDateKey > nextDateKey) {
            if (temp.getPersianMonth() == currentCalendarMonth.getPersianMonth()) {
                rlDayContainer = llDaysContainer.findViewWithTag(nextDateKey);
                if (rlDayContainer != null) {
                    DayContainer container = new DayContainer(rlDayContainer);

                    makeAsRangeDate(container);
                }
            }
            selectedDatesRange.add(nextDateKey);
            temp.setPersianDate(temp.getPersianYear(), temp.getPersianMonth(), temp.getPersianDay() + 1);

            nextDateKey = DayContainer.GetContainerKey(temp);
        }
        selectedDatesRange.add(lastDateKey);
    }

    /**
     * To remove all selection and redraw current calendar
     */
    public void resetAllSelectedViews() {
        selectedDatesRange.clear();

        minSelectedDate = null;
        maxSelectedDate = null;

        drawCalendarForMonth(currentCalendarMonth);

        if (calendarListener != null) {
            calendarListener.onCancel();
        }
    }

    private void setWeekTitleColor() {
        for (int i = 0; i < llTitleWeekContainer.getChildCount(); i++) {
            TextView textView = (TextView) llTitleWeekContainer.getChildAt(i);
            textView.setTextColor(weekColor);
            textView.setTextSize(textSizeWeek);
        }
    }

    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            this.typeface = typeface;

            drawCalendarForMonth(currentCalendarMonth);
            tvYearTitle.setTypeface(this.typeface);

            for (int i = 0; i < llTitleWeekContainer.getChildCount(); i++) {
                TextView textView = (TextView) llTitleWeekContainer.getChildAt(i);
                textView.setTypeface(this.typeface);
            }
        }
    }

    //endregion
    //---------------------------------------------------------------------------------------------
    //region Properties
    public void build() {
        drawCalendarForMonth(getCurrentMonth(currentDate));
        setWeekTitleColor();
        rlHeaderCalendar.setBackgroundColor(headerBackgroundColor);
    }

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }

    public PersianCalendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(PersianCalendar currentDate) {
        this.currentDate = currentDate;
    }

    public PersianCalendar getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(PersianCalendar maxDate) {
        this.maxDate = maxDate;
    }

    public void setDisableDaysAgo(boolean disableDaysAgo) {
        isDisableDaysAgo = disableDaysAgo;
    }

    public void setDisableDaysAfter(boolean disableDaysAfter) {
        isDisableDaysAfter = disableDaysAfter;
    }

    public void setShowGregorianDate(boolean showGregorianDate) {
        this.showGregorianDate = showGregorianDate;
    }

    public String getMessageEnterEndDate() {
        return messageEnterEndDate;
    }

    public void setShouldEnabledTime(boolean shouldEnabledTime) {
        this.shouldEnabledTime = shouldEnabledTime;
    }

    //--------------------------------------------------------------------------------------------

    public void setHeaderBackgroundColor(int headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor != 0 ? headerBackgroundColor : this.headerBackgroundColor;
    }

    public void setWeekColor(int weekColor) {
        this.weekColor = weekColor != 0 ? weekColor : this.weekColor;
    }

    public void setRangeStripColor(int rangeStripColor) {
        this.rangeStripColor = rangeStripColor != 0 ? rangeStripColor : this.rangeStripColor;
    }

    public void setSelectedDateCircleColor(int selectedDateCircleColor) {
        this.selectedDateCircleColor = selectedDateCircleColor != 0 ? selectedDateCircleColor : this.selectedDateCircleColor;
    }

    public void setSelectedDateColor(int selectedDateColor) {
        this.selectedDateColor = selectedDateColor != 0 ? selectedDateColor : this.selectedDateColor;
    }

    public void setDefaultDateColor(int defaultDateColor) {
        this.defaultDateColor = defaultDateColor != 0 ? defaultDateColor : this.defaultDateColor;
    }

    public void setDisableDateColor(int disableDateColor) {
        this.disableDateColor = disableDateColor != 0 ? disableDateColor : this.disableDateColor;
    }

    public void setRangeDateColor(int rangeDateColor) {
        this.rangeDateColor = rangeDateColor != 0 ? rangeDateColor : this.rangeDateColor;
    }

    public void setHolidayColor(int holidayColor) {
        this.holidayColor = holidayColor != 0 ? holidayColor : this.holidayColor;
    }

    public void setTodayColor(int todayColor) {
        this.todayColor = todayColor != 0 ? todayColor : this.todayColor;
    }


    public void setTextSizeTitle(float textSizeTitle) {
        this.textSizeTitle = textSizeTitle != 0 ? textSizeTitle : this.textSizeTitle;
    }

    public void setTextSizeWeek(float textSizeWeek) {
        this.textSizeWeek = textSizeWeek != 0 ? textSizeWeek : this.textSizeWeek;
    }

    public void setTextSizeDate(float textSizeDate) {
        this.textSizeDate = textSizeDate != 0 ? textSizeDate : this.textSizeDate;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    public interface CalendarListener {
        void onDateSelected(PersianCalendar date);

        void onDateRangeSelected(PersianCalendar startDate, PersianCalendar endDate);

        void onCancel();
    }
}
