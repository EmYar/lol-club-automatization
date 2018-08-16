package lolapi;

import entity.Pair;
import one.util.streamex.StreamEx;

import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class ApiRequestExecutor {
    private static final List<Pair<Integer, Integer>> limits = Collections.unmodifiableList(List.of(
            new Pair<>(20, 1),
            new Pair<>(100, 120)
    ));

    private List<Pair<Integer, Timer>> currentStatuses;

    public ApiRequestExecutor() {
        var timer = new Timer(true);

        currentStatuses = StreamEx.of(limits)
                .map(pair -> new Pair<>(0, new Timer(true)))
                .toList();
    }
}
