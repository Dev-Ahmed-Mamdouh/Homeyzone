package com.example.smarthomesecurity.models;

public class Notification {

    String title;
    String time;
    String hint;
    int value;
    boolean isDanger;

    public Notification(String title, String time, int value, boolean isDanger, String hint) {
        this.title = title;
        this.time = time;
        this.value = value;
        this.isDanger = isDanger;
        this.hint = hint;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getValue() {
        return value;
    }

    public boolean isDanger() {
        return isDanger;
    }

    public String getHint() {
        return hint;
    }
}
