package ru.buylist.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    private long id;
    private long collectionId;
    private String name;
    private boolean isPurchased;
    private String category;
    private String categoryColor;
    private String quantity;
    private String unit;

    public Item() {
        id = System.currentTimeMillis();
    }

    @Ignore
    public Item(long id) {
        this.id = id;
    }

    @Ignore
    public Item(int count, long collectionId, String name, String category, String categoryColor, String quantity,
                String unit) {
        // для уникальности, т.к. при переносе нескольких позиций генерируемые id начинают совпадать
        this.id = System.currentTimeMillis() + count;
        this.collectionId = collectionId;
        this.name = name;
        this.category = category;
        this.categoryColor = categoryColor;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isEmpty() {
        return name == null || name.isEmpty();
    }
}
