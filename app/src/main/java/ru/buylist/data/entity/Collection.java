package ru.buylist.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "collections")
public class Collection {
    @PrimaryKey
    private long id;
    private String title;
    private String type;
    private String description;

    public Collection() {
        id = System.currentTimeMillis();
    }

    @Ignore
    public Collection(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEmpty() {
        return title == null || title.isEmpty();
    }
}
