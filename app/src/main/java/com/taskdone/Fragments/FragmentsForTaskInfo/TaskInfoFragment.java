package com.taskdone.Fragments.FragmentsForTaskInfo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskdone.AddTaskActivity;
import com.taskdone.Fragments.FragmentsForAddTaskActivity.AddTaskFragment;
import com.taskdone.Fragments.FragmentsForAddTaskActivity.ChooseFolderFragment;
import com.taskdone.MainActivity;
import com.taskdone.R;
import com.taskdone.TaskInfoActivity;
import com.taskdone.Utils.DeadlineNotification;
import com.taskdone.Utils.ItemModel;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.taskdone.TaskInfoActivity.TASK_ID;

public class TaskInfoFragment extends Fragment {

    View view;
    Boolean deadlineTouched = false;

    @BindView(R.id.cancel)
    RelativeLayout cancelButton;
    @BindView(R.id.confirm) RelativeLayout confirmButton;
    @BindView(R.id.deadline) LinearLayout deadline;
    @BindView(R.id.deadline_icon) ImageView deadlineIcon;
    @BindView(R.id.deadline_tv) TextView deadlineText;
    @BindView(R.id.deadline_tv_optional) TextView deadlineTextOptional;
    @BindView(R.id.switch_perm_notification) SwitchCompat permanentSwitch;
//    @BindView(R.id.reminders_tv) TextView remindersText;
//    @BindView(R.id.reminders_tv_optional) TextView remindersTextOptional;
    @BindView(R.id.select_folder_tv) TextView selectFolderText;
    @BindView(R.id.select_folder_tv_optional) TextView selectFolderTextOptional;
    @BindView(R.id.task) TextView taskText;

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_info, container, false);
        ButterKnife.bind(this, view);
        fillContent();
        selectFolder();
        setDeadline();
        bottomButtonsActions();

        if (getArguments() != null) Toast.makeText(getContext(), getArguments().getString("selectedFolder"), Toast.LENGTH_SHORT).show();

        return view;
    }

    void fillContent() {
        final String selectFolder = ((TaskInfoActivity)getActivity()).selectedFolder;
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("list")
                .child(TASK_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ItemModel itemModel = dataSnapshot.getValue(ItemModel.class);
                            taskText.setText(itemModel.text);
                            if (selectFolder != null) {
                                selectFolderText.setText(selectFolder);
                                selectFolderTextOptional.setVisibility(View.GONE);
                            }
                            else if (itemModel.folder != null){
                                selectFolderText.setText(itemModel.folder);
                                selectFolderTextOptional.setVisibility(View.GONE);
                            }
                            if (itemModel.permanentNotification) {
                                permanentSwitch.setChecked(true);
                            }
                            if (itemModel.deadline != null){
                                ((TaskInfoActivity)getActivity()).existDeadline = true;
                                deadlineText.setText("Deadline : " + itemModel.deadline);
                                deadlineTextOptional.setVisibility(View.GONE);
                                deadlineIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_remove));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void bottomButtonsActions() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTaskInfo();
                switchPermanentNotification();

                AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), DeadlineNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)Long.parseLong(TASK_ID), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                manager.cancel(pendingIntent);
                if (((TaskInfoActivity)getActivity()).existDeadline && deadlineTouched) startDeadlineNotification(taskText.getText().toString(), Long.parseLong(TASK_ID));

                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
            }
        });
    }

    void updateTaskInfo() {
        DatabaseReference referenceToTask = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("list")
                .child(TASK_ID);

        referenceToTask.child("text").setValue(taskText.getText().toString());
        referenceToTask.child("folder").setValue(((TaskInfoActivity)getActivity()).selectedFolder);
        if (((TaskInfoActivity)getActivity()).existDeadline)referenceToTask.child("deadline").setValue(deadlineText.getText().toString());
    }

    void switchPermanentNotification() {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService("notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, new Intent(getContext(), MainActivity.class), 0);
        NotificationCompat.Builder permnanentNotification = new NotificationCompat.Builder(getContext()).setSmallIcon(R.drawable.ic_logo_notification).setContentTitle(this.taskText.getText().toString()).setContentIntent(pendingIntent).setGroup("GRUP").setOngoing(true);
        NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(getContext()).setSmallIcon(R.drawable.ic_logo_notification).setContentIntent(pendingIntent).setGroup("GRUP").setGroupSummary(true).setOngoing(true);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("list").child(TASK_ID).child("permanentNotification");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("general", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int total = sharedPreferences.getInt("activePNotifications", 0);
        if (permanentSwitch.isChecked()) {
            reference.setValue(Boolean.valueOf(true));
        } else {
            reference.setValue(Boolean.valueOf(false));
        }
    }

    void selectFolder(){
        view.findViewById(R.id.select_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("TaskInfo")
                        .setCustomAnimations(R.anim.to_add_anim_start, R.anim.to_add_anim_end, R.anim.to_menu_anim_start, R.anim.to_menu_anim_end)
                        .addToBackStack("TaskInfoFragment")
                        .replace(R.id.fragment, new ChooseFolderFragment())
                        .commit();
            }
        });
    }

    void setDeadline(){
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deadlineTouched = true;
                if (((TaskInfoActivity)getActivity()).existDeadline) {
                    ((TaskInfoActivity) getActivity()).existDeadline = false;
                    ((TaskInfoActivity) getActivity()).deadline.clear();
                    deadlineIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_grey));
                    deadlineText.setText("Set a deadline");
                    deadlineTextOptional.setVisibility(View.VISIBLE);
                    deadlineIcon.setImageDrawable(getActivity().getDrawable(R.drawable.plus_vec));
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getContext(), DeadlineNotification.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)Long.parseLong(TASK_ID), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("list")
                            .child(TASK_ID);
                    ref.child("deadline").removeValue();
                } else {
                    final Calendar current = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, final int i, final int i1, final int i2) {
                            ((TaskInfoActivity) getActivity()).deadline.set(i, i1, i2);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i3, int i4) {
                                    ((TaskInfoActivity) getActivity()).deadline.set(i, i1, i2, i3, i4, 0);
                                    Calendar deadline = ((TaskInfoActivity) getActivity()).deadline;
                                    deadlineTextOptional.setVisibility(View.GONE);
                                    deadlineText.setText("Deadline: " + deadline.get(Calendar.DAY_OF_MONTH) + " / " + deadline.get(Calendar.MONTH) + " / " + deadline.get(Calendar.YEAR) + ", " + deadline.get(Calendar.HOUR_OF_DAY) + ":" + deadline.get(Calendar.MINUTE));
                                    deadlineIcon.setImageResource(R.drawable.ic_remove);
                                    ((TaskInfoActivity) getActivity()).deadline = deadline;
                                    ((TaskInfoActivity) getActivity()).existDeadline = Boolean.valueOf(true);
                                }
                            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true);
                            timePickerDialog.setTitle("Select time");
                            timePickerDialog.show();
                        }
                    }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setTitle("Select date");
                    datePickerDialog.show();
                }
            }
        });
    }

    void startDeadlineNotification(String text, long id) {
        Intent intent = new Intent(getContext(), DeadlineNotification.class);
        intent.putExtra("text", text);
        intent.putExtra("id", (int)id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = ((TaskInfoActivity) getActivity()).deadline;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("DEADLINE", calendar.getTime().toString());

        String deadlineForDatabase = calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " / " + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("list")
                .child(TASK_ID);
        ref.child("deadline").setValue(deadlineForDatabase);
    }

}
