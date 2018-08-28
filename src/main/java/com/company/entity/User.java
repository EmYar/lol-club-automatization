package com.company.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class User {
    private String accountId;
    private String name;
    private Set<String> oldNames;
    private String vkPage;
    private String discord;
    private Role role;

    private List<Pair<LocalDate, LocalDate>> absences;

    public User(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOldNames(Set<String> oldNames) {
        this.oldNames = oldNames;
    }

    public Set<String> getOldNames() {
        return oldNames;
    }

    public String getVkPage() {
        return vkPage;
    }

    public void setVkPage(String vkPage) {
        this.vkPage = vkPage;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Pair<LocalDate, LocalDate>> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Pair<LocalDate, LocalDate>> absences) {
        this.absences = absences;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(getAccountId())
                .add(getName())
                .add(getVkPage())
                .add(getDiscord())
                .add(getOldNames().toString())
                .add(getRole().getName());
        return stringJoiner.toString();
    }
}
