package com.taskdone.Utils;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDone extends Application {
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
