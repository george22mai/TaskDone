package com.taskdone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.taskdone.Fragments.FragmentsForTaskInfo.TaskInfoFragment;

import java.util.Calendar;

public class TaskInfoActivity extends AppCompatActivity {

    public static String TASK_ID = null;
    public String selectedFolder = null;
    public boolean existDeadline = false;
    public Calendar deadline = Calendar.getInstance();
    @BindView(R.id.toolbar) Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage a task");
        if (getIntent() != null) {
            TASK_ID = getIntent().getExtras().getString("idTask");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new TaskInfoFragment()).commit();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(R.anim.to_menu_anim_start, R.anim.to_menu_anim_end);
    }

}
