package com.company.googledrive.entity;

public class Role {

    public enum ID {ADMIN, MEMBER, KICKED, BANNED, STRANGER, UNKNOWN, LEAVER};

    private ID id;
    private String name;

    public Role(ID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(String id, String name) {
        this(ID.valueOf(id), name);
    }

    public ID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
