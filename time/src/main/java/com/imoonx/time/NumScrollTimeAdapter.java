package com.imoonx.time;


public class NumScrollTimeAdapter implements ScrollTimeAdapter {

    private static final int DEFAULT_MAX_VALUE = 9;

    private static final int DEFAULT_MIN_VALUE = 0;
    private int minValue;
    private int maxValue;
    private String format;

    public NumScrollTimeAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    public NumScrollTimeAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public NumScrollTimeAdapter(int minValue, int maxValue, String format) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return 0;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int indexOf(Object o) {
        return Integer.parseInt(o.toString());
    }
}
