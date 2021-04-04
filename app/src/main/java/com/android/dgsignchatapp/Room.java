package com.android.dgsignchatapp;

public class Room {
    public String name;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Room() {
    }

    public Room(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
