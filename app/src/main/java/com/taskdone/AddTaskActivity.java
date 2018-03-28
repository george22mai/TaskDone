package com.taskdone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.taskdone.Fragments.FragmentsForAddTaskActivity.AddTaskFragment;
import java.util.ArrayList;
import java.util.HashMap;

public class AddTaskActivity extends AppCompatActivity {
    public HashMap<String, Integer> deadline = new HashMap();
    public Boolean existDeadline = Boolean.valueOf(false);
    public String folder;
    public int reminders = 0;
    public ArrayList<Long> remindersMilis = new ArrayList();
    public String savedText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Add a task");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_add_task, new AddTaskFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
    }
}
