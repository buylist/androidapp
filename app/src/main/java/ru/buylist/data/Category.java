package ru.buylist.data;

import java.util.UUID;

public class Category {
    private long id;
    private String name;
    private String color;

    public Category() {
        id = System.currentTimeMillis();
    }

    public Category(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
