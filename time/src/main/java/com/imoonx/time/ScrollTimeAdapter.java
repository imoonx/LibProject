package com.imoonx.time;

public interface ScrollTimeAdapter<T> {

    int getItemsCount();

    T getItem(int index);

    int indexOf(T o);
}
