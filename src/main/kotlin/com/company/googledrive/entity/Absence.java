package com.company.googledrive.entity;

import java.time.LocalDate;

public class Absence extends GDriveEntity {
    private User user;
    private LocalDate from;
    private LocalDate to;

    public Absence(String documentId, String listName, int row, User user, LocalDate from, LocalDate to) {
        super(documentId, listName, row);
        this.user = user;
        this.from = from;
        this.to = to;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }
}
