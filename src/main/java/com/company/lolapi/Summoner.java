package com.company.lolapi;

public class Summoner {
    private long profileIconId;
    private String name;
    private long summonerLevel;
    private long accountId;
    private long id;
    private long revisionDate;

    public Summoner() {
    }

    public Summoner(long profileIconId, String name, long summonerLevel, long accountId, long id, long revisionDate) {
        this.profileIconId = profileIconId;
        this.name = name;
        this.summonerLevel = summonerLevel;
        this.accountId = accountId;
        this.id = id;
        this.revisionDate = revisionDate;
    }

    public long getProfileIconId() {
        return profileIconId;
    }

    public void setProfileIconId(long profileIconId) {
        this.profileIconId = profileIconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSummonerLevel() {
        return summonerLevel;
    }

    public void setSummonerLevel(long summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(long revisionDate) {
        this.revisionDate = revisionDate;
    }
}
