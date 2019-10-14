package ru.buylist.data;

public class BuyList {

    private long id;
    private String title;
    private String type;

    public BuyList() {
        id = System.currentTimeMillis();
    }

    public BuyList(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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

    public boolean isEmpty() {
        return title == null || title.isEmpty();
    }
}
