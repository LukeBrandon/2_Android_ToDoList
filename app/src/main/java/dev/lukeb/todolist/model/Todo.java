package dev.lukeb.todolist.model;

import android.os.Parcelable;

public class Todo {

    int id;
    String title;
    String description;
    boolean done;
    long date;

    public Todo() {
        this.title = "Uninitialized";
        this.description = "Uninitialized";
        this.date = 000000000000;
        this.done = false;
    }

    public Todo(int id, String title, String desc, long date, boolean done){
        this.id = id;
        this.title = title;
        this.description = desc;
        this.date = date;
        this.done = done;
    }

    // Getters
    public int getId(){ return this.id; }

    public String getTitle(){ return this.title; }

    public String getDescription(){
        return this.description;
    }

    public long getDate(){ return this.date; }

    public boolean getDone(){
        return this.done;
    }

    // Setters
    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String desc){ this.description = desc; }

    public void setDate(long date) { this.date = date; }

    public void setDone(boolean done){
        this.done = done;
    }
}
