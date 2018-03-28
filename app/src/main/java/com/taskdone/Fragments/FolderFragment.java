package com.taskdone.Fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
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

public class FolderFragment extends Fragment {
    ChildEventListener childEventListener = new C07351();
    SimpleCallback itemTouchHelperCallback = new SimpleCallback(0, 12) {
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
            return false;
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
            final ItemModel removedTask;
            if (direction == 8) {
                final int removedTaskPosition = viewHolder.getAdapterPosition();
                removedTask = (ItemModel) FolderFragment.this.list.get(removedTaskPosition);
                final DatabaseReference mDatabaseReferenceForDelete = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/list/" + ((ItemModel) FolderFragment.this.list.get(viewHolder.getAdapterPosition())).id);
                linearLayout = getActivity().findViewById(R.id.relativeLayout);
                Snackbar snackbar = Snackbar.make(FolderFragment.this.linearLayout, "You’ve just completed a task.", 0).setActionTextColor(FolderFragment.this.getResources().getColor(R.color.white)).setAction("Undo", new OnClickListener() {
                    public void onClick(View v) {
                        FolderFragment.this.list.add(removedTaskPosition, removedTask);
                        FolderFragment.this.recyclerView.setAdapter(new RecyclerAdapter(FolderFragment.this.list));
                        mDatabaseReferenceForDelete.child("id").setValue(removedTask.id);
                        mDatabaseReferenceForDelete.child("text").setValue(removedTask.text);
                        mDatabaseReferenceForDelete.child("color").setValue(removedTask.color);
                        FolderFragment.this.updateNumberOfTasksCompleted(true);
                    }
                });
                mDatabaseReferenceForDelete.removeValue();
                FolderFragment.this.updateNumberOfTasksCompleted(false);
                snackbar.getView().setBackgroundColor(FolderFragment.this.getResources().getColor(R.color.colorPrimary));
                snackbar.show();
                FolderFragment.this.list.remove(viewHolder.getAdapterPosition());
                FolderFragment.this.recyclerView.setAdapter(new RecyclerAdapter(FolderFragment.this.list));
                snackbar.addCallback(new Callback() {
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        NotificationManager notificationManager = (NotificationManager) FolderFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        SharedPreferences sharedPreferences = FolderFragment.this.getContext().getSharedPreferences("general", 0);
                        Editor editor = sharedPreferences.edit();
                        if (removedTask.idPermanentNotification != null)notificationManager.cancel(removedTask.idPermanentNotification.intValue());
                        editor.putInt("activePNotifications", sharedPreferences.getInt("activePNotifications", 0) - 1).commit();
                        if (sharedPreferences.getInt("activePNotifications", 0) == 0) {
                            notificationManager.cancel(100);
                        }
                        Log.d("TotalNotifications", sharedPreferences.getInt("activePNotifications", 0) + "");
                    }
                });
            } else if (direction == 4) {
                removedTask = (ItemModel) FolderFragment.this.list.get(viewHolder.getAdapterPosition());
                Intent intent = new Intent(FolderFragment.this.getContext(), TaskInfoActivity.class);
                intent.putExtra("idTask", removedTask.id);
                FolderFragment.this.startActivity(intent);
                FolderFragment.this.getActivity().overridePendingTransition(R.anim.to_add_anim_start, R.anim.to_add_anim_end);
            }
        }
    };
    LinearLayoutManager linearLayoutManager;
    List<ItemModel> list = new ArrayList();
    LinearLayout mBlock;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    RelativeLayout linearLayout;
    String titleOfFolder;

    class C07351 implements ChildEventListener {
        C07351() {
        }

        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                FolderFragment.this.list.add((ItemModel) dataSnapshot.getValue(ItemModel.class));
                FolderFragment.this.recyclerAdapter.notifyDataSetChanged();
                FolderFragment.this.mBlock.setVisibility(View.GONE);
            }
        }

        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        public void onCancelled(DatabaseError databaseError) {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        this.mBlock = (LinearLayout) view.findViewById(R.id.block);
        if (getArguments() != null) {
            this.titleOfFolder = getArguments().getString("title");
        }
        setTtitleActionBar(this.titleOfFolder);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.folderRecyclerView);
        this.linearLayoutManager = new LinearLayoutManager(getContext());
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("list").orderByChild("folder").equalTo(this.titleOfFolder).addChildEventListener(this.childEventListener);
        this.recyclerAdapter = new RecyclerAdapter(this.list);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
        this.recyclerView.setAdapter(this.recyclerAdapter);
        new ItemTouchHelper(this.itemTouchHelperCallback).attachToRecyclerView(this.recyclerView);
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        return view;
    }

    void setTtitleActionBar(String title) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
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
