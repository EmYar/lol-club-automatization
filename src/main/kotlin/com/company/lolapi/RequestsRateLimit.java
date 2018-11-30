package com.company.lolapi;

import org.jetbrains.annotations.NotNull;

public interface RequestsRateLimit extends Comparable<RequestsRateLimit> {

    void acquire();
    long getCooldownTime();
    void shutdown();

    @Override
    default int compareTo(@NotNull RequestsRateLimit o) {
        return Long.compare(getCooldownTime(), o.getCooldownTime());
    }
}
