package database.LoginAttemptController;

import org.springframework.stereotype.Service;
import sample.UserController;

import java.text.ParseException;

@Service
public class LoginAttemptService {

    private static int MAX_ATTEMPT = 3;

    private static boolean isBanned(int attempts) {
        try {
            return attempts >= MAX_ATTEMPT;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getStatus(int attempts) throws ParseException {
        if (isBanned(attempts) || BanTimer.checkIsTimerStarted()) {
            System.out.println("You have been banned for 50 seconds!");
            if (BanTimer.checkIsTimerOver() || !BanTimer.checkIsTimerStarted()){
                new BanTimer();
                return false;
            } else {
                BanTimer.setIsTimeOver(false);
                BanTimer.setIsTimeStarted(true);
                return false;
            }
        } else {
            System.out.println("Ban is over!");
            return true;
        }
    }
}
