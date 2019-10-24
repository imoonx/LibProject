package com.cssi.common.app;

import java.io.File;

public class Item {
    public File file;
    public String string;

    public Item(File file) {
        this.file = file;
        if (file.isDirectory())
            string = file.getName() + "/";
        else
            string = file.getName();
    }

    public Item(File file, String string) {
        this.file = file;
        this.string = string;
    }

    public String toString() {
        return string;
    }
}