package com.example.bookmarkback.book.entity;

public enum RecordStatus {
    READING("독서중"),
    DONE("완독");

    private final String name;

    RecordStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
