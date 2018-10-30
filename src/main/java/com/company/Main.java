package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            NamesUpdater.run();
        } catch (IOException e) {
            LOG.error("Failed to update names", e);
        }
    }
}
