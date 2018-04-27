package com.taskdone.Utils.Objects;

public class Reminder {
    private boolean isChecked = false;
    private long milis;
    private String title;
    private String type;

    public Reminder(String title, String type, long milis) {
        this.title = title;
        this.type = type;
        this.milis = milis;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public long getMilis() {
        return this.milis;
    }

    public void setMilis(long milis) {
        this.milis = milis;
    }
}
