package ru.buylist.models;

import java.util.UUID;

public class Product {
    private String buylistId;
    private UUID productId;
    private String name;
    private boolean isPurchased;
    private String category;
    private String amount;
    private String unit;

    public Product() {
        this(UUID.randomUUID());
    }

    public Product(UUID productId) {
        this.productId = productId;
    }

    public String getBuylistId() {
        return buylistId;
    }

    public void setBuylistId(String buylistId) {
        this.buylistId = buylistId;
    }

    public UUID getProductId() {
        return productId;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
