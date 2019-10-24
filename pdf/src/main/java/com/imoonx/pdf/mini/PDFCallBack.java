package com.imoonx.pdf.mini;


public interface PDFCallBack {

    void onPageViewSizeChanged(int width, int height);

    void gotoURI(String uri);

    void gotoPage(int pageCount);

    void goForward();

    void goBackward();

    void toggleUI();

}
