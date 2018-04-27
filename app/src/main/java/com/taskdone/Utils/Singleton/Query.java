package com.taskdone.Utils.Singleton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taskdone.Utils.Objects.Task;

import java.util.Calendar;

public class Query {

    private static Query instance = new Query();

    private Query(){}

    public static Query getInstance(){
        return instance;
    }

    private static DatabaseReference referenceToTasks = FirebaseDatabase.getInstance().getReference()
            .child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("list");

    private static DatabaseReference referenceToDatabase = FirebaseDatabase.getInstance().getReference()
            .child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public void undoTaskDatabase(Task task){
        referenceToTasks = referenceToTasks.child(task.id);
        referenceToTasks.child("id").setValue(task.id);
        referenceToTasks.child("text").setValue(task.text);
    }

    public void deleteTaskFromDatabase(Task task){
        referenceToTasks.child(task.id).removeValue();
    }

    public void addTaskToDatabase(Task task){
        referenceToTasks = referenceToTasks.child(task.id);
        referenceToTasks.child("id").setValue(task.id);
        referenceToTasks.child("text").setValue(task.text);
        referenceToTasks.child("folder").setValue(task.folder);
        referenceToTasks.child("permanentNotification").setValue(task.permanentNotification);
    }
    public void clearDatabase(){
        referenceToDatabase.child("list").removeValue();
        referenceToDatabase.child("folders").removeValue();
        referenceToDatabase.child("tasksCompleted").removeValue();
    }

    public void createFolder(String folder){
        referenceToDatabase
                .child("folders")
                .child(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                .child("title")
                .setValue(folder);
    }

}
