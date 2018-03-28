package com.taskdone.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
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
import android.widget.RelativeLayout;
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
import com.taskdone.Utils.ItemModel;
import com.taskdone.Utils.RecyclerAdapter;
import java.util.ArrayList;
import java.util.List;

public class AllTasksFragment extends Fragment {
    SimpleCallback itemTouchHelperCallback = new SimpleCallback(0, 12) {
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            return false;
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
            final ItemModel removedTask;
            if (direction == 8) {
                final int removedTaskPosition = viewHolder.getAdapterPosition();
                removedTask = list.get(removedTaskPosition);
                final DatabaseReference mDatabaseReferenceForDelete = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/list/" + ((ItemModel) AllTasksFragment.this.list.get(viewHolder.getAdapterPosition())).id);
                linearLayout = getActivity().findViewById(R.id.fragment);
                final Snackbar snackbar = Snackbar.make(AllTasksFragment.this.linearLayout, "You’ve just completed a task.", 0).setActionTextColor(AllTasksFragment.this.getResources().getColor(R.color.white)).setAction("Undo", new OnClickListener() {
                    public void onClick(View v) {
                        AllTasksFragment.this.list.add(removedTaskPosition, removedTask);
                        AllTasksFragment.this.recyclerView.setAdapter(new RecyclerAdapter(AllTasksFragment.this.list));
                        mDatabaseReferenceForDelete.child("id").setValue(removedTask.id);
                        mDatabaseReferenceForDelete.child("text").setValue(removedTask.text);
                        mDatabaseReferenceForDelete.child("color").setValue(removedTask.color);
                        AllTasksFragment.this.updateNumberOfTasksCompleted(true);
                    }
                });
                mDatabaseReferenceForDelete.removeValue();
                AllTasksFragment.this.updateNumberOfTasksCompleted(false);
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
                AllTasksFragment.this.list.remove(viewHolder.getAdapterPosition());
                AllTasksFragment.this.recyclerView.setAdapter(new RecyclerAdapter(AllTasksFragment.this.list));
                snackbar.addCallback(new Callback() {
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        NotificationManager notificationManager = (NotificationManager) AllTasksFragment.this.getActivity().getSystemService("notification");
                        SharedPreferences sharedPreferences = AllTasksFragment.this.getContext().getSharedPreferences("general", 0);
                        Editor editor = sharedPreferences.edit();
                        if (removedTask.idPermanentNotification != null)notificationManager.cancel(removedTask.idPermanentNotification.intValue());
                        editor.putInt("activePNotifications", sharedPreferences.getInt("activePNotifications", 0) - 1).commit();
                        if (sharedPreferences.getInt("activePNotifications", 0) == 0) {
                            notificationManager.cancel(100);
                        }
                    }
                });
            } else if (direction == 4) {
                removedTask = (ItemModel) AllTasksFragment.this.list.get(viewHolder.getAdapterPosition());
                Intent intent = new Intent(AllTasksFragment.this.getContext(), TaskInfoActivity.class);
                intent.putExtra("idTask", removedTask.id);
                AllTasksFragment.this.startActivity(intent);
                AllTasksFragment.this.getActivity().overridePendingTransition(R.anim.to_add_anim_start, R.anim.to_add_anim_end);
            }
        }
    };
    LinearLayoutManager linearLayoutManager;
    List<ItemModel> list = new ArrayList();
    RecyclerAdapter mAdapter;
    LinearLayout mBlock;
    Builder notification;
    RecyclerView recyclerView;
    DatabaseReference referenceToAllTasks;
    LinearLayout linearLayout;
    InboxStyle style = new InboxStyle();
    NotificationManager manager;
    PendingIntent notificationPending;
    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                ItemModel task = dataSnapshot.getValue(ItemModel.class);
                list.add(task);
                if (task.text != null) {
                    mAdapter.notifyDataSetChanged();
                }
                if (task.permanentNotification) {
                    style.addLine(((ItemModel) dataSnapshot.getValue(ItemModel.class)).text);
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
    String selectedFolder;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);
        manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(6383165);
        notificationPending = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        mBlock = (LinearLayout) view.findViewById(R.id.block);
        notification = new Builder(getContext()).setSmallIcon(R.drawable.ic_logo_notification).setContentTitle("Your Tasks").setOngoing(true);
        referenceToAllTasks = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("list");
        referenceToAllTasks.addChildEventListener(childEventListener);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAllTasks);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new RecyclerAdapter(this.list);
        recyclerView.setLayoutManager(this.linearLayoutManager);
        recyclerView.setAdapter(this.mAdapter);
        new ItemTouchHelper(this.itemTouchHelperCallback).attachToRecyclerView(this.recyclerView);

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
