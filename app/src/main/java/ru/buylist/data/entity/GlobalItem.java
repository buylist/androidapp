package ru.buylist.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "global_items")
public class GlobalItem {
    @PrimaryKey
    private long id;
    private String name;
    private String category;
    private String colorCategory;

    public GlobalItem(long id, String name, String category, String colorCategory) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.colorCategory = colorCategory;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColorCategory() {
        return colorCategory;
    }

    public void setColorCategory(String colorCategory) {
        this.colorCategory = colorCategory;
    }
}
