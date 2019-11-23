package ru.buylist.data.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    private long id;
    private String name;
    private String color;

    public Category() {
        id = System.currentTimeMillis();
    }

    @Ignore
    public Category(long id) {
        this.id = id;
    }

    @Ignore
    public Category(String name, String color) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.color = color;
    }

    @Ignore
    public Category(int count, String name, String color) {
        // для уникальности, т.к. при переносе нескольких позиций генерируемые id начинают совпадать
        this.id = System.currentTimeMillis() + count;
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
