package lolapi;

public interface RequestsRateLimit extends Comparable<RequestsRateLimit> {

    void acquire();
    long getCooldown();

    @Override
    default int compareTo(RequestsRateLimit o) {
        return Long.compare(getCooldown(), o.getCooldown());
    }
}
