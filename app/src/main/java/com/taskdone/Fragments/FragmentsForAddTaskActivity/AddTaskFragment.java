package com.taskdone.Fragments.FragmentsForAddTaskActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taskdone.AddTaskActivity;
import com.taskdone.MainActivity;
import com.taskdone.R;
import com.taskdone.Utils.Notifications.DeadlineNotification;
import com.taskdone.Utils.Objects.Task;
import com.taskdone.Utils.Singleton.Query;

import java.util.HashMap;

public class AddTaskFragment extends Fragment {
    public View view;
    EditText editText;

    class C07476 implements OnClickListener {
        C07476() {
        }

        public void onClick(final View view) {
            if (((AddTaskActivity) getActivity()).existDeadline) {
                ((AddTaskActivity) getActivity()).existDeadline = Boolean.valueOf(false);
                ((AddTaskActivity) getActivity()).deadline.clear();
                ((ImageView) view.findViewById(R.id.deadline_icon)).setImageResource(R.drawable.ic_add);
                ((TextView) view.findViewById(R.id.deadline_tv)).setText("Set a deadline");
                view.findViewById(R.id.deadline_tv_optional).setVisibility(View.VISIBLE);
            }
            else{
                final Calendar current = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskFragment.this.getContext(), new OnDateSetListener() {

                    class C07451 implements OnTimeSetListener {
                        C07451() {
                        }

                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline.put("hour", Integer.valueOf(i));
                            ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline.put("minute", Integer.valueOf(i1));
                            HashMap<String, Integer> deadline = ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline;
                            view.findViewById(R.id.deadline_tv_optional).setVisibility(View.GONE);
                            ((TextView) view.findViewById(R.id.deadline_tv)).setText("Deadline: " + deadline.get("day") + " / " + deadline.get("month") + " / " + deadline.get("year") + ", " + deadline.get("hour") + ":" + deadline.get("minute"));
                            ((ImageView) view.findViewById(R.id.deadline_icon)).setImageResource(R.drawable.ic_remove);
                            ((AddTaskActivity) AddTaskFragment.this.getActivity()).existDeadline = Boolean.valueOf(true);
                        }
                    }

                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline.put("year", Integer.valueOf(i));
                        ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline.put("month", Integer.valueOf(i1));
                        ((AddTaskActivity) AddTaskFragment.this.getActivity()).deadline.put("day", Integer.valueOf(i2));
                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskFragment.this.getContext(), new C07451(), current.get(11), current.get(12), true);
                        timePickerDialog.setTitle("Select time");
                        timePickerDialog.show();
                    }
                }, current.get(1), current.get(2), current.get(5));
                datePickerDialog.setTitle("Select date");
                datePickerDialog.show();
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_task, container, false);
        final Switch switchPermanentNotification = view.findViewById(R.id.switch_perm_notification);
        editText = (EditText) this.view.findViewById(R.id.task);
        setDeadline();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((AddTaskActivity) AddTaskFragment.this.getActivity()).savedText = editable.toString();
            }
        });
        if (((AddTaskActivity) getActivity()).savedText != null)
            editText.setText(((AddTaskActivity) getActivity()).savedText);
        view.findViewById(R.id.select_folder).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.to_add_anim_start, R.anim.to_add_anim_end, R.anim.to_menu_anim_start, R.anim.to_menu_anim_end)
                        .addToBackStack("main").
                        replace(R.id.fragment_add_task, new ChooseFolderFragment())
                        .commit();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                task.id = getTaskIdFromCurrentTime();
                task.text = editText.getText().toString();;
                task.folder = ((AddTaskActivity) getActivity()).folder;
                if (switchPermanentNotification.isChecked()) task.permanentNotification = true;

                if (task.text.trim().length() != 0) {
                    Query.getInstance().addTaskToDatabase(task);
                    if (((AddTaskActivity) getActivity()).existDeadline) startDeadlineNotification(task.text, Long.parseLong(task.id));

                    startActivity(new Intent(AddTaskFragment.this.getContext(), MainActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
                }
                else
                    Toast.makeText(AddTaskFragment.this.getContext(), "You need a description for your task!", 0).show();
            }
        });
        String folder = ((AddTaskActivity) getActivity()).folder;
        if (folder != null) {
            ((TextView) view.findViewById(R.id.select_folder_tv)).setText(folder);
            view.findViewById(R.id.select_folder_tv_optional).setVisibility(View.GONE);
        }
        if (((AddTaskActivity) getActivity()).reminders != 0) {
            view.findViewById(R.id.reminders_tv_optional).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.reminders_tv)).setText("You have " + ((AddTaskActivity) getActivity()).reminders + " reminders");
        }
        return view;
    }

    private String getTaskIdFromCurrentTime() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    void setDeadline() {
        if (((AddTaskActivity) getActivity()).existDeadline) {
            HashMap<String, Integer> deadline = ((AddTaskActivity) getActivity()).deadline;
            view.findViewById(R.id.deadline_tv_optional).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.deadline_tv)).setText("Deadline: " + deadline.get("day") + " / " + deadline.get("month") + " / " + deadline.get("year") + ", " + deadline.get("hour") + ":" + deadline.get("minute"));
            ((ImageView) view.findViewById(R.id.deadline_icon)).setImageResource(R.drawable.ic_remove);
        }
        this.view.findViewById(R.id.deadline).setOnClickListener(new C07476());
    }

    void startDeadlineNotification(String text, long id) {
        HashMap<String, Integer> map = ((AddTaskActivity) getActivity()).deadline;
        Intent intent = new Intent(getContext(), DeadlineNotification.class);
        intent.putExtra("text", text);
        intent.putExtra("id", (int)id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Calendar alarm = Calendar.getInstance();
        alarm.set(map.get("year"), map.get("month"), map.get("day"), map.get("hour"), map.get("minute"), 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pendingIntent);
        Log.d("DEADLINE", alarm.getTime().toString());

        String deadlineForDatabase = map.get("hour") + " : " + map.get("minute") + " / " + map.get("day") + "." + map.get("month") + "." + map.get("year");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("list")
                .child(String.valueOf(id));
        ref.child("deadline").setValue(deadlineForDatabase);
    }
}
