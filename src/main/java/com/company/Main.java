package com.company;

import com.company.googledrive.Parser;
import com.company.googledrive.entity.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            List<User> users = Parser.parse(User.class);
            users.size();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
