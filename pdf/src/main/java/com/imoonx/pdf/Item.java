package com.imoonx.pdf;

import java.io.Serializable;

public class Item implements Serializable {

    public String title;
    public int page;

    public Item(String title, int page) {
        this.title = title;
        this.page = page;
    }

    public String toString() {
        return title;
    }
}