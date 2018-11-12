package com.company.tasks;

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

public class NamesUpdater implements BotTask {
    private static final Logger LOG = LoggerFactory.getLogger(NamesUpdater.class);

    public void run() {
        LOG.info("NamesUpdater started");
        Parser parser = Parser.getInstance();
        try (ApiFabric apiFabric = ApiFabric.getInstance()) {
            List<User> users = parser.parse(User.class);
            LOG.info("{} users parsed", users.size());
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
                        LOG.info("User {} has outdated name", user.getAccountId());
                        usersToSave.add(user);
                    }
                } else {
                    summoner = summonerApi.getSummonerByName(user.getName()).execute().body();
                    if (summoner != null) {
                        user.setAccountId(summoner.getAccountId());
                        LOG.info("User {} has no accountId");
                        usersToSave.add(user);
                    }
                }
            }
            if (!usersToSave.isEmpty()) {
                parser.update(User.class, usersToSave);
            }
            LOG.info("Updated {} users", usersToSave.size());
        } catch (IOException e) {
            LOG.error("Failed to update names", e);
        }
        LOG.info("NamesUpdater completed");
    }
}
