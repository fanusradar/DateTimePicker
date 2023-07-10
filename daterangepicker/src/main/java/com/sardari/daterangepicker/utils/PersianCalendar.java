package com.sardari.daterangepicker.utils;

import android.util.Log;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;

import static com.sardari.daterangepicker.dialog.TimePickerDialog.hours;
import static com.sardari.daterangepicker.dialog.TimePickerDialog.minutes;

@SuppressWarnings("unused")
public class PersianCalendar extends GregorianCalendar {
    private static final long SERIAL_VERSION_UID = 5541422440580682494L;

    private int persianYear;
    private int persianMonth;
    private int persianDay;
    private String delimiter = "/";

    private long convertToMillis(long julianDate) {
        return PersianCalendarConstants.MILLIS_JULIAN_EPOCH + julianDate * PersianCalendarConstants.MILLIS_OF_A_DAY
                + PersianCalendarUtils.ceil(getTimeInMillis() - PersianCalendarConstants.MILLIS_JULIAN_EPOCH, PersianCalendarConstants.MILLIS_OF_A_DAY);
    }

    public PersianCalendar(long millis) {
        setTimeInMillis(millis);
    }

    public PersianCalendar() {
        setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private void calculatePersianDate() {
        long julianDate = ((long) Math.floor((getTimeInMillis() - PersianCalendarConstants.MILLIS_JULIAN_EPOCH)) / PersianCalendarConstants.MILLIS_OF_A_DAY);
        long PersianRowDate = PersianCalendarUtils.julianToPersian(julianDate);
        long year = PersianRowDate >> 16;
        int month = (int) (PersianRowDate & 0xff00) >> 8;
        int day = (int) (PersianRowDate & 0xff);
        this.persianYear = (int) (year > 0 ? year : year - 1);
        this.persianMonth = month;
        this.persianDay = day;
    }

    public boolean isPersianLeapYear() {
        return PersianCalendarUtils.isPersianLeapYear(this.persianYear);
    }

    public void setPersianDate(int persianYear, int persianMonth, int persianDay) {
        persianMonth += 1;
        this.persianYear = persianYear;
        this.persianMonth = persianMonth;
        this.persianDay = persianDay;
        setTimeInMillis(convertToMillis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? this.persianYear : this.persianYear + 1, this.persianMonth - 1, this.persianDay)));
    }

    public void setPersianYear(int persianYear) {
        this.persianMonth += 1;
        this.persianYear = persianYear;
        setTimeInMillis(convertToMillis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? this.persianYear : this.persianYear + 1, this.persianMonth - 1, this.persianDay)));
    }

    public void setPersianMonth(int persianMonth) {
        persianMonth += 1;
        this.persianMonth = persianMonth;
        setTimeInMillis(convertToMillis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? this.persianYear : this.persianYear + 1, this.persianMonth - 1, 1)));
    }

    public void setPersianDay(int persianDay) {
        this.persianMonth += 1;
        this.persianDay = persianDay;
        setTimeInMillis(convertToMillis(PersianCalendarUtils.persianToJulian(this.persianYear > 0 ? this.persianYear : this.persianYear + 1, this.persianMonth - 1, this.persianDay)));
    }

    public int getPersianYear() {
        // calculatePersianDate();
        return this.persianYear;
    }

    public int getPersianMonth() {
        // calculatePersianDate();
        return this.persianMonth;
    }

    public String getPersianMonthName() {
        // calculatePersianDate();
        return PersianCalendarConstants.persianMonthNames[this.persianMonth];
    }

    public int getPersianDay() {
        // calculatePersianDate();
        return this.persianDay;
    }

    public String getPersianWeekDayName() {
        return switch (get(DAY_OF_WEEK)) {
            case SATURDAY -> PersianCalendarConstants.persianWeekDays[0];
            case SUNDAY -> PersianCalendarConstants.persianWeekDays[1];
            case MONDAY -> PersianCalendarConstants.persianWeekDays[2];
            case TUESDAY -> PersianCalendarConstants.persianWeekDays[3];
            case WEDNESDAY -> PersianCalendarConstants.persianWeekDays[4];
            case THURSDAY -> PersianCalendarConstants.persianWeekDays[5];
            default -> PersianCalendarConstants.persianWeekDays[6];
        };
    }

    public String getPersianLongDate() {
        return getPersianWeekDayName() + "  " + this.persianDay + "  " + getPersianMonthName() + "  " + this.persianYear;
    }

    public String getPersianLongDateAndTime() {
        return getPersianLongDate() + " ساعت " + get(HOUR_OF_DAY) + ":" + get(MINUTE) + ":" + get(SECOND);
    }

    public String getPersianShortDate() {
        // calculatePersianDate();
        return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth() + 1) + delimiter + formatToMilitary(this.persianDay);
    }

    public String getPersianShortDateTime() {
        Log.i("timePicker", "getPersianShortDateTime_h=" + this.get(HOUR_OF_DAY) + " m=" + get(MINUTE));

        return "" + formatToMilitary(this.persianYear) + delimiter + formatToMilitary(getPersianMonth() + 1) + delimiter + formatToMilitary(this.persianDay) + " " + formatToMilitary(hours) + ":" + formatToMilitary(minutes)
                + ":" + formatToMilitary(get(SECOND));
    }

    private String formatToMilitary(int i) {
        return (i <= 9) ? "0" + i : String.valueOf(i);
    }

    public void addPersianDate(int field, int amount) {
        if (amount == 0) {
            return; // Do nothing!
        }

        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        if (field == YEAR) {
            setPersianDate(this.persianYear + amount, getPersianMonth() + 1, this.persianDay);
            return;
        } else if (field == MONTH) {
            setPersianDate(this.persianYear + ((getPersianMonth() + 1 + amount) / 12), (getPersianMonth() + 1 + amount) % 12, this.persianDay);
            return;
        }
        add(field, amount);
        calculatePersianDate();
    }

    public void addPersianDate1(int field, int amount) {
        if (amount == 0) {
            return; // Do nothing!
        }

        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        if (field == YEAR) {
            setPersianDate(this.persianYear + amount, getPersianMonth() + 1, 1);
            return;
        } else if (field == MONTH) {
            setPersianDate(this.persianYear + ((getPersianMonth() + 1 + amount) / 12), (getPersianMonth() + 1 + amount) % 12, this.persianDay);
            return;
        }

        calculatePersianDate();
    }

    public void parse(String dateString) {
        PersianCalendar p = new PersianDateParser(dateString, delimiter).getPersianDate();
        setPersianDate(p.getPersianYear(), p.getPersianMonth(), p.getPersianDay());
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @NonNull
    @Override
    public String toString() {
        String str = super.toString();
        return str.substring(0, str.length() - 1) + ",PersianDate=" + getPersianShortDate() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);

    }

    @Override
    public void set(int field, int value) {
        super.set(field, value);
        calculatePersianDate();
    }

    @Override
    public void setTimeInMillis(long millis) {
        super.setTimeInMillis(millis);
        calculatePersianDate();
    }

    @Override
    public void setTimeZone(TimeZone zone) {
        super.setTimeZone(zone);
        calculatePersianDate();
    }
}
