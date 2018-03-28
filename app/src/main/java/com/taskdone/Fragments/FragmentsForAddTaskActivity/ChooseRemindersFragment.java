package com.taskdone.Fragments.FragmentsForAddTaskActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import com.taskdone.AddTaskActivity;
import com.taskdone.R;
import com.taskdone.Utils.Reminder;
import com.taskdone.Utils.ReminderAdapter;
import java.util.ArrayList;
import java.util.List;

public class ChooseRemindersFragment extends Fragment {
    List<Reminder> list = new ArrayList();
    ListView listView;
    ReminderAdapter reminderAdapter;
    View view;

    class C07531 implements OnClickListener {
        C07531() {
        }

        public void onClick(View view) {
            ((AddTaskActivity) ChooseRemindersFragment.this.getActivity()).reminders = ChooseRemindersFragment.this.reminderAdapter.totalChecked();
            ((AddTaskActivity) ChooseRemindersFragment.this.getActivity()).remindersMilis = ChooseRemindersFragment.this.reminderAdapter.getTimes();
            ChooseRemindersFragment.this.getFragmentManager().popBackStack();
        }
    }

    class C07542 implements OnClickListener {
        C07542() {
        }

        public void onClick(View view) {
            ChooseRemindersFragment.this.getFragmentManager().popBackStack();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_choose_reminders, container, false);
        this.listView = (ListView) this.view.findViewById(R.id.listView);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.list.add(new Reminder("30 minutes before", "preset", 1800000));
        this.list.add(new Reminder("1 hour before", "preset", 3600000));
        this.list.add(new Reminder("6 hours before", "preset", 21600000));
        this.list.add(new Reminder("1 day before", "preset", 86400000));
        this.list.add(new Reminder("Titlu 2", "custom", 0));
        this.reminderAdapter = new ReminderAdapter(getContext(), R.layout.reminder_preset, this.list);
        this.listView.setAdapter(this.reminderAdapter);
        this.view.findViewById(R.id.confirm).setOnClickListener(new C07531());
        this.view.findViewById(R.id.cancel).setOnClickListener(new C07542());
        return this.view;
    }
}
