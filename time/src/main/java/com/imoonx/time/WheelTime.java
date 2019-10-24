package com.imoonx.time;

import android.view.View;

import com.imoonx.util.Res;
import com.imoonx.util.XLog;

import java.util.Arrays;
import java.util.List;

public class WheelTime {

    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hour;
    private WheelView wv_minute;

    private ScrollTimePickerView.Type type;
    private static final int DEFULT_START_YEAR = 1990;
    private static final int DEFULT_END_YEAR = 2100;
    private int startYear = DEFULT_START_YEAR;
    private int endYear = DEFULT_END_YEAR;

    public WheelTime(View view) {
        super();
        this.view = view;
        type = ScrollTimePickerView.Type.ALL;
        setView(view);
    }

    public WheelTime(View view, ScrollTimePickerView.Type type) {
        super();
        this.view = view;
        this.type = type;
        setView(view);
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0);
    }

    public void setPicker(int year, int month, int day, int hour, int minute) {
        XLog.i(this.getClass(), "year=" + year + "month" + month + "day=" + day + "hour=" + hour);
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        //年
        wv_year = view.findViewById(R.id.year);
        wv_year.setAdapter(new NumScrollTimeAdapter(startYear, endYear));
        wv_year.setLabel(Res.getString(R.string.pickerview_year));
        wv_year.setCurrentItem(year - startYear);

        //月
        wv_month = view.findViewById(R.id.month);
        wv_month.setAdapter(new NumScrollTimeAdapter(1, 12, "%02d"));
        wv_month.setLabel(Res.getString(R.string.pickerview_month));
        wv_month.setCurrentItem(month);

        //日
        wv_day = view.findViewById(R.id.day);
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumScrollTimeAdapter(1, 31, "%02d"));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumScrollTimeAdapter(1, 30, "%02d"));
        } else {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumScrollTimeAdapter(1, 29, "%02d"));
            else
                wv_day.setAdapter(new NumScrollTimeAdapter(1, 28, "%02d"));
        }
        wv_day.setLabel(Res.getString(R.string.pickerview_day));
        wv_day.setCurrentItem(day - 1);

        //时
        wv_hour = view.findViewById(R.id.hour);
        wv_hour.setAdapter(new NumScrollTimeAdapter(0, 23, "%02d"));
        wv_hour.setLabel(Res.getString(R.string.pickerview_hours));
        wv_hour.setCurrentItem(hour - 1);
        //分
        wv_minute = view.findViewById(R.id.minute);
        wv_minute.setAdapter(new NumScrollTimeAdapter(1, 60, "%02d"));
        wv_minute.setLabel(Res.getString(R.string.pickerview_minutes));
        wv_minute.setCurrentItem(minute);

        OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int maxItem;
                if (list_big.contains(String.valueOf(wv_month.getCurrentItem()))) {
                    wv_day.setAdapter(new NumScrollTimeAdapter(1, 31, "%02d"));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(wv_month.getCurrentItem()))) {
                    wv_day.setAdapter(new NumScrollTimeAdapter(1, 30, "%02d"));
                    maxItem = 30;
                } else {
                    if ((index % 4 == 0 && index % 100 != 0) || index % 400 == 0) {
                        wv_day.setAdapter(new NumScrollTimeAdapter(1, 29, "%02d"));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumScrollTimeAdapter(1, 28, "%02d"));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
        };

        OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int maxItem;
                if (list_big.contains(String.valueOf(index))) {
                    wv_day.setAdapter(new NumScrollTimeAdapter(1, 31, "%02d"));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(index))) {
                    wv_day.setAdapter(new NumScrollTimeAdapter(1, 30, "%02d"));
                    maxItem = 30;
                } else {
                    if (((wv_year.getCurrentItem()) % 4 == 0 && (wv_year.getCurrentItem()) % 100 != 0)
                            || (wv_year.getCurrentItem()) % 400 == 0) {
                        wv_day.setAdapter(new NumScrollTimeAdapter(1, 29, "%02d"));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumScrollTimeAdapter(1, 28, "%02d"));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
        };
        wv_year.setOnItemSelectedListener(wheelListener_year);
        wv_month.setOnItemSelectedListener(wheelListener_month);

        int textSize = 6;
        XLog.i(this.getClass(), "当前类型=" + type);

        switch (type) {
            case ALL:
                textSize = textSize * 3;
                break;
            case YEAR_HOUR:
                textSize = textSize * 3;
                wv_minute.setVisibility(View.GONE);
                break;
            case YEAR_MONTH_DAY:
                textSize = textSize * 3;
                wv_hour.setVisibility(View.GONE);
                wv_minute.setVisibility(View.GONE);
                break;
            case HOURS_MINS:
                textSize = textSize * 4;
                wv_year.setVisibility(View.GONE);
                wv_month.setVisibility(View.GONE);
                wv_day.setVisibility(View.GONE);
                break;
            case MONTH_DAY_HOUR_MIN:
                textSize = textSize * 3;
                wv_year.setVisibility(View.GONE);
                break;
            case YEAR_MONTH:
                textSize = textSize * 4;
                wv_day.setVisibility(View.GONE);
                wv_hour.setVisibility(View.GONE);
                wv_minute.setVisibility(View.GONE);
        }
        wv_year.setTextSize(textSize);
        wv_month.setTextSize(textSize);
        wv_day.setTextSize(textSize);
        wv_hour.setTextSize(textSize);
        wv_minute.setTextSize(textSize);
    }

    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hour.setCyclic(cyclic);
        wv_minute.setCyclic(cyclic);
    }

    public String getTime() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ALL:
                sb.append((wv_year.getCurrentItem()))
                        .append("-")
                        .append((wv_month.getCurrentItem()))
                        .append("-")
                        .append((wv_day.getCurrentItem()))
                        .append(" ").append(wv_hour.getCurrentItem())
                        .append(":")
                        .append(wv_minute.getCurrentItem());
                break;
            case YEAR_HOUR:
                sb.append((wv_year.getCurrentItem()))
                        .append("-")
                        .append((wv_month.getCurrentItem()))
                        .append("-")
                        .append((wv_day.getCurrentItem()))
                        .append(" ").append(wv_hour.getCurrentItem());
                break;
            case YEAR_MONTH_DAY:
                sb.append((wv_year.getCurrentItem()))
                        .append("-")
                        .append((wv_month.getCurrentItem()))
                        .append("-")
                        .append((wv_day.getCurrentItem()));
                break;
            case HOURS_MINS:
                sb.append(wv_hour.getCurrentItem())
                        .append(":")
                        .append(wv_minute.getCurrentItem());
                break;
            case MONTH_DAY_HOUR_MIN:
                sb.append((wv_month.getCurrentItem())).append("-")
                        .append((wv_day.getCurrentItem()))
                        .append(" ")
                        .append(wv_hour.getCurrentItem())
                        .append(":")
                        .append(wv_minute.getCurrentItem());
                break;
            case YEAR_MONTH:
                sb.append((wv_year.getCurrentItem()))
                        .append("-")
                        .append((wv_month.getCurrentItem()));
                break;
            default:
                break;
        }
        return sb.toString();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

}
