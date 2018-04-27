package com.taskdone.Utils.Objects;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FolderSelect {
    public ImageView icon;
    public boolean state = false;
    public String title;
    public TextView tv;
    public View view;

    public FolderSelect() {
    }

    public FolderSelect(int id, String title, ImageView icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getText() {
        return this.title;
    }

    public void setText(String text) {
        this.title = text;
    }

    public void setSelected(TextView text, ImageView icon) {
        int color = Color.parseColor("#0E6EFF");
        icon.setColorFilter(color);
        text.setTextColor(color);
    }

    public void clearSelectedState(TextView text, ImageView icon) {
        int color = Color.parseColor("#9E9E9E");
        icon.setColorFilter(color);
        text.setTextColor(color);
    }
}
