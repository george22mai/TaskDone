package com.taskdone.Fragments.FragmentsForAddTaskActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.taskdone.AddTaskActivity;
import com.taskdone.Fragments.FragmentsForTaskInfo.TaskInfoFragment;
import com.taskdone.R;
import com.taskdone.TaskInfoActivity;
import com.taskdone.Utils.FolderModelSelect;
import com.taskdone.Utils.FolderMoldelSelectAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChooseFolderFragment extends Fragment {
    FolderMoldelSelectAdapter adapter;
    List<FolderModelSelect> list = new ArrayList();
    ListView listView;
    View view;

    class C07481 implements ChildEventListener {
        C07481() {
        }

        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.exists()) {
                ChooseFolderFragment.this.list.add((FolderModelSelect) dataSnapshot.getValue(FolderModelSelect.class));
                ChooseFolderFragment.this.adapter.notifyDataSetChanged();
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

    class C07503 implements OnClickListener {
        C07503() {
        }

        public void onClick(View view) {
            ChooseFolderFragment.this.getFragmentManager().beginTransaction().setCustomAnimations(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end).replace(R.id.fragment_add_task, new AddTaskFragment()).commit();
        }
    }

    class C07525 implements DialogInterface.OnClickListener {
        C07525() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_choose_folder, container, false);
        setHasOptionsMenu(true);
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("folders").addChildEventListener(new C07481());
        listView = (ListView) this.view.findViewById(R.id.listView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        adapter = new FolderMoldelSelectAdapter(getContext(), R.layout.radio_button_item, this.list);
        listView.setAdapter(this.adapter);
        view.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ChooseFolderFragment.this.adapter.getSelectedFolder() == null) {
                    Toast.makeText(ChooseFolderFragment.this.getContext(), "You need to select a folder", 0).show();
                }
                if (getActivity().getClass() == TaskInfoActivity.class){
                    ((TaskInfoActivity)getActivity()).selectedFolder = adapter.getSelectedFolder();
                    getFragmentManager().popBackStack();
                }
                else{
                    ((AddTaskActivity) getActivity()).folder = adapter.getSelectedFolder();
                    getFragmentManager().beginTransaction().setCustomAnimations(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end).replace(R.id.fragment_add_task, new AddTaskFragment()).commit();
                }
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new C07503());
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_actions_add_task, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_folder_button) {
            addFolderAlertDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFolderAlertDialog() {
        final Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_folder, null);
        builder.setView(view);
        final EditText editText = view.findViewById(R.id.editText);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("folders")
                        .child(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                        .child("title")
                        .setValue(editText.getText().toString());
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
