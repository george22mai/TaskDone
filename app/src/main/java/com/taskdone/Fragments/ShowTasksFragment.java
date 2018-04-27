package com.taskdone.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskdone.MainActivity;
import com.taskdone.R;
import com.taskdone.TaskInfoActivity;
import com.taskdone.Utils.Objects.Task;
import com.taskdone.Utils.Singleton.Query;
import com.taskdone.Utils.Adapters.RecyclerAdapter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowTasksFragment extends Fragment {
    SimpleCallback itemTouchHelperCallback = new SimpleCallback(0, 12) {
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            return false;
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
            final Task removedTask;
            if (direction == 8) {
                final int removedTaskPosition = viewHolder.getAdapterPosition();
                removedTask = list.get(removedTaskPosition);
                linearLayout = getActivity().findViewById(R.id.fragment);
                final Snackbar snackbar = Snackbar.make(linearLayout, "You’ve just completed a task.", 0).setActionTextColor(getResources().getColor(R.color.white)).setAction("Undo", new OnClickListener() {
                    public void onClick(View v) {
                        list.add(removedTaskPosition, removedTask);
                        ShowTasksFragment.this.recyclerView.setAdapter(new RecyclerAdapter(ShowTasksFragment.this.list));
                        Query.getInstance().undoTaskDatabase(removedTask);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                query.deleteTaskFromDatabase(removedTask);
                ShowTasksFragment.this.updateNumberOfTasksCompleted(false);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        style = new InboxStyle();
                        manager.cancel(6383165);
                        referenceToAllTasks.addChildEventListener(childEventListener);
                    }
                });
                snackbar.show();
                list.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyDataSetChanged();
            } else if (direction == 4) {
                removedTask = list.get(viewHolder.getAdapterPosition());
                Intent intent = new Intent(ShowTasksFragment.this.getContext(), TaskInfoActivity.class);
                intent.putExtra("idTask", removedTask.id);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.to_add_anim_start, R.anim.to_add_anim_end);
            }
        }
    };
    LinearLayoutManager linearLayoutManager;
    List<Task> list = new ArrayList();
    RecyclerAdapter mAdapter = new RecyclerAdapter(list);
    LinearLayout mBlock;
    Builder notification;
    DatabaseReference referenceToAllTasks;
    LinearLayout linearLayout;
    InboxStyle style = new InboxStyle();
    NotificationManager manager;
    PendingIntent notificationPending;
    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                Task task = dataSnapshot.getValue(Task.class);
                list.add(task);
                if (task.text != null) {
                    mAdapter.notifyDataSetChanged();
                }
                if (task.permanentNotification) {
                    style.addLine(((Task) dataSnapshot.getValue(Task.class)).text);
                    notification.setStyle(style);
                    notification.setContentIntent(notificationPending);
                    ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).notify(6383165, notification.build());
                }
                mBlock.setVisibility(View.GONE);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    Query query;

    @BindView(R.id.recyclerViewAllTasks) RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);
        ButterKnife.bind(this, view);
        query = Query.getInstance();
        manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(6383165);
        notificationPending = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        mBlock = (LinearLayout) view.findViewById(R.id.block);
        notification = new Builder(getContext()).setSmallIcon(R.drawable.ic_logo_notification).setContentTitle("Your Tasks").setOngoing(true);
        referenceToAllTasks = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("list");
        if (getArguments() != null){
            String folder = getArguments().getString("title");
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("list")
                    .orderByChild("folder")
                    .equalTo(folder)
                    .addChildEventListener(childEventListener);
        }else referenceToAllTasks.addChildEventListener(childEventListener);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

        return view;
    }

    void updateNumberOfTasksCompleted(final boolean undo) {
        final DatabaseReference referenceToValue = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tasksCompleted");
        referenceToValue.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    int value = ((Integer) dataSnapshot.getValue(Integer.class)).intValue();
                    if (undo) {
                        referenceToValue.setValue(Integer.valueOf(value - 1));
                    } else {
                        referenceToValue.setValue(Integer.valueOf(value + 1));
                    }
                } else if (undo) {
                    referenceToValue.setValue(Integer.valueOf(0));
                } else {
                    referenceToValue.setValue(Integer.valueOf(1));
                }
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
