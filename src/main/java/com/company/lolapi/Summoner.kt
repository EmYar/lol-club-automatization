package com.company.lolapi

class Summoner {
    var profileIconId: Long = 0
    var name: String = ""
    var summonerLevel: String = ""
    var accountId: String = ""
    var id: String = ""
    var revisionDate: String = ""

    constructor() {}

    constructor(profileIconId: Long, name: String, summonerLevel: String, accountId: String, id: String, revisionDate: String) {
        this.profileIconId = profileIconId
        this.name = name
        this.summonerLevel = summonerLevel
        this.accountId = accountId
        this.id = id
        this.revisionDate = revisionDate
    }
}
