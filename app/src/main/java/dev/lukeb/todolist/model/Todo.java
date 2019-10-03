package dev.lukeb.todolist.model;

import android.os.Parcelable;

public class Todo {

    int id;
    String title;
    String description;
    boolean done;
    String date;

    public Todo() {
        this.title = "Uninitialized";
        this.description = "Uninitialized";
        this.date = "Unitialized";
        this.done = false;
    }

    public Todo(int id, String title, String desc, String date, boolean done){
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

    public String getDate(){ return this.date; }

    public boolean getDone(){
        return this.done;
    }

    // Setters
    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String desc){ this.description = desc; }

    public void setDate(String date) { this.date = date; }

    public void setDone(boolean done){
        this.done = done;
    }
}
