package com.mrmq.uyoutube.beans;

public class ScreenSetting {
    public static final String SCREEN_MAIN = "SCREEN_MAIN";
    public static final String SCREEN_LOGIN = "SCREEN_LOGIN";

    private int width;
    private int height;

    public ScreenSetting(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
