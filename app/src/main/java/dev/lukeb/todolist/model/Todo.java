package dev.lukeb.todolist.model;

import android.os.Parcelable;

public class Todo {

    String title;
    String description;
    boolean done;

    public Todo() {
        this.title = "Uninitialized";
        this.description = "Uninitialized";
        this.done = false;
    }

    public Todo(String title, String desc, boolean done){
        this.title = title;
        this.description = desc;
        this.done = done;
    }

    // Getters
    public String getTitle(){ return this.title; }

    public String getDescription(){
        return this.description;
    }

    public boolean getDone(){
        return this.done;
    }

    // Setters
    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setDone(boolean done){
        this.done = done;
    }
}
