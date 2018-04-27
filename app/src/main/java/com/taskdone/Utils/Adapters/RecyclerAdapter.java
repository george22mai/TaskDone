package com.taskdone.Utils.Adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taskdone.R;
import com.taskdone.Utils.Objects.Task;

import java.util.List;

public class RecyclerAdapter extends Adapter<RecyclerAdapter.ItemViewHolder> {
    private List<Task> list;

    class ItemViewHolder extends ViewHolder {
        ImageView dot = ((ImageView) this.itemView.findViewById(R.id.dot));
        TextView listItemText = ((TextView) this.itemView.findViewById(R.id.title));

        public ItemViewHolder(View viewItem) {
            super(viewItem);
        }
    }

    public RecyclerAdapter(List<Task> list) {
        this.list = list;
    }

    public int getItemCount() {
        return this.list.size();
    }

    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.listItemText.setText(((Task) this.list.get(position)).text);
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false));
    }
}
