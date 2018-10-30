package com.company;

import com.company.googledrive.Parser;
import com.company.googledrive.entity.User;
import com.company.lolapi.ApiFabric;
import com.company.lolapi.Summoner;
import com.company.lolapi.SummonerApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NamesUpdater {
    private static final Logger log = LoggerFactory.getLogger(NamesUpdater.class);

    public static void run() throws IOException {
        Parser parser = Parser.getInstance();
        List<User> users = parser.parse(User.class);
        try (ApiFabric apiFabric = ApiFabric.getInstance()) {
            SummonerApi summonerApi = apiFabric.getSummonerApi();
            List<User> usersToSave = new ArrayList<>();
            for (User user : users) {
                if (StringUtils.isAllBlank(user.getAccountId(), user.getName())) {
                    continue;
                }
                Summoner summoner;
                if (StringUtils.isNotBlank(user.getAccountId())
                        && (summoner = summonerApi.getSummonerByAccountId(user.getAccountId()).execute().body()) != null) {
                    if (!StringUtils.equals(user.getName(), summoner.getName())) {
                        user.setName(summoner.getName());
                        usersToSave.add(user);
                    }
                } else {
                    summoner = summonerApi.getSummonerByName(user.getName()).execute().body();
                    if (summoner != null) {
                        user.setAccountId(summoner.getAccountId());
                        usersToSave.add(user);
                    }
                }
            }
            if (!usersToSave.isEmpty()) {
                parser.update(User.class, usersToSave);
            }
        }
    }
}
