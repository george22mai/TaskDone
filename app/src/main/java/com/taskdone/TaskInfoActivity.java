package com.taskdone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskdone.Fragments.FragmentsForAddTaskActivity.ChooseFolderFragment;
import com.taskdone.Fragments.FragmentsForTaskInfo.TaskInfoFragment;
import com.taskdone.Utils.ItemModel;

import java.util.Calendar;
import java.util.HashMap;

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
