package com.company.lolapi;

public interface RequestsRateLimit extends Comparable<RequestsRateLimit> {

    void acquire();
    long getCooldown();
    void shutdown();

    @Override
    default int compareTo(RequestsRateLimit o) {
        return Long.compare(getCooldown(), o.getCooldown());
    }
}
