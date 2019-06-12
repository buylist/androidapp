package ru.buylist.models;

import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private String color;

    public Category() {
        this(UUID.randomUUID());
    }

    public Category(UUID id) {
        this.id = id;
    }

    public UUID getId() {
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
