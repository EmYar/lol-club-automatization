import lolapi.ApiRequestLimiter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
//        ScoreChecker scoreChecker = new ScoreChecker();
//        scoreChecker.getActiveUsers();
//        try {
//            System.out.println(ScoresParser.getInstance().parse());
//        } catch (ParsingException e) {
//            e.printStackTrace();
//        }
        int i = 1;
        ApiRequestLimiter limiter = ApiRequestLimiter.build()
                .add(2, TimeUnit.SECONDS, 1)
                .add(4, TimeUnit.SECONDS, 5, false)
                .build();
        while (true) {
            limiter.acquire();
            System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + String.valueOf(i++));
        }
    }
}
