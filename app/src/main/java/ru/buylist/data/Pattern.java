package ru.buylist.data;

public class Pattern {
    private long id;
    private String name;

    // конструктор для создания нового шаблона
    public Pattern() {
        id = System.currentTimeMillis();
    }

    // конструктор для существующего шаблона
    public Pattern(long id) {
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
}
