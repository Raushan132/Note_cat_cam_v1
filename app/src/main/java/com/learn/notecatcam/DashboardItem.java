package com.learn.notecatcam;


public class DashboardItem {
    String title;
    int icon;

    public String getTitle() {
        return title;
    }

    public DashboardItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
