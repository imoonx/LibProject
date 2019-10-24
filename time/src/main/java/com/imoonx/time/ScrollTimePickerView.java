package com.imoonx.time;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imoonx.util.Res;

import java.util.Calendar;
import java.util.Date;

public class ScrollTimePickerView extends ScrollBasePickerView implements View.OnClickListener {

    public enum Type {
        ALL, YEAR_MONTH_DAY, HOURS_MINS, MONTH_DAY_HOUR_MIN, YEAR_MONTH, YEAR_HOUR
    }

    private WheelTime wheelTime;

    private TextView tvTitle;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private OnTimeSelectListener timeSelectListener;

    public ScrollTimePickerView(Context context, Type type) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.scrolltime_time, contentContainer);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnSubmit.setOnClickListener(this);

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnCancel.setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        View timepickerview = findViewById(R.id.timepicker);
        wheelTime = new WheelTime(timepickerview, type);

        initPicker();
    }

    private void initPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (null != wheelTime)
            wheelTime.setPicker(year, month, day, hours, minute);
    }

    /**
     * 设置起始时间范围
     *
     * @param startYear 开始时间
     */
    public void setYearRange(int startYear) {
        wheelTime.setStartYear(startYear);
        initPicker();
    }

    /**
     * 设置时间范围
     *
     * @param startYear 开始时间
     * @param endYear   结束时间
     */
    public void setYearRange(int startYear, int endYear) {
        wheelTime.setStartYear(startYear);
        wheelTime.setEndYear(endYear);
        initPicker();
    }

    public void setTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
    }

    public void setCyclic(boolean cyclic) {
        wheelTime.setCyclic(cyclic);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
        } else {
            if (null != timeSelectListener && null != wheelTime) {
                timeSelectListener.onTimeSelect(wheelTime.getTime());
            }
            dismiss();
        }
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(String time);
    }

    public void setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }

    /**
     * 设置时间标题
     *
     * @param titleId 标题id
     */
    public void setTitle(int titleId) {
        tvTitle.setText(Res.getString(titleId));
    }

    public void setTitle(String title) {
        if (null != tvTitle)
            tvTitle.setText(title);
    }
}
