package ru.buylist.models;

public class Product {
    private String buylistId;
    private String name;
    private boolean isPurchased;
    private String category;

    public String getBuylistId() {
        return buylistId;
    }

    public void setBuylistId(String buylistId) {
        this.buylistId = buylistId;
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
}
