package com.taskdone.Utils;

public class ItemModel {
    public String color;
    public String folder;
    public String id;
    public Integer idPermanentNotification;
    public boolean permanentNotification;
    public String text;
    public String deadline;

    public ItemModel() {
    }

    public ItemModel(String text, String id, String folder, int idPermanentNotification, boolean permanentNotification, String deadline) {
        this.id = id;
        this.text = text;
        this.folder = folder;
        this.color = this.color;
        this.idPermanentNotification = Integer.valueOf(idPermanentNotification);
        this.permanentNotification = permanentNotification;
        this.deadline = deadline;
    }
}
