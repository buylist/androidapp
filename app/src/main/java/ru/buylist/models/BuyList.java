package ru.buylist.models;

import java.util.UUID;

public class BuyList {
    //UUID генерирует  универсально-уникальный идентификатор
    private UUID id;
    private String title;

    public BuyList() {
        this(UUID.randomUUID());
    }

    public BuyList(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
