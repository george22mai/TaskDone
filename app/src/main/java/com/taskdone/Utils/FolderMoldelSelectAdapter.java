package com.taskdone.Utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taskdone.R;

import java.util.List;

public class FolderMoldelSelectAdapter extends ArrayAdapter<FolderModelSelect> {
    List<FolderModelSelect> list;
    String selected;

    public FolderMoldelSelectAdapter(@NonNull Context context, int resource, @NonNull List<FolderModelSelect> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.radio_button_item, null);
        }
        final TextView tv = (TextView) view.findViewById(R.id.text);
        final ImageView im = (ImageView) view.findViewById(R.id.icon);
        ((FolderModelSelect) getItem(position)).view = view;
        final FolderModelSelect item = (FolderModelSelect) getItem(position);
        tv.setText(item.getText());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int defaultColor = Color.parseColor("#9E9E9E");
                int defaultColorText = Color.parseColor("#393939");
                int color = Color.parseColor("#0E6EFF");
                for (FolderModelSelect model : FolderMoldelSelectAdapter.this.list) {
                    model.state = false;
                    ((ImageView) model.view.findViewById(R.id.icon)).setColorFilter(defaultColor);
                    ((TextView) model.view.findViewById(R.id.text)).setTextColor(defaultColorText);
                }
                item.state = true;
                im.setColorFilter(color);
                tv.setTextColor(color);
                FolderMoldelSelectAdapter.this.selected = item.getText();
            }
        });
        return view;
    }

    public String getSelectedFolder() {
        return this.selected;
    }
}
