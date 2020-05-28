package database.LoginAttemptController;

import database.Requests.LoginUser.LoginUser;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.AclEntry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static int MAX_ATTEMPT = 3;
/*
    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

 */

    private static boolean isBanned(int attempts){
       try {
           return attempts >= MAX_ATTEMPT;
       } catch (Exception e){
           return false;
       }
    }

    public static boolean getStatus(int attempts) throws ParseException {
        String attemptTime = LoginUser.getAttempTime();
        if (isBanned(attempts)){
            String formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
            SimpleDateFormat current = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            Date date = current.parse(attemptTime);
            if (Calendar.getInstance().getTime().after(date)){
                System.out.println("You got banned! Wait for 50 seconds!");
                return false;
            }
            return true;
        }
        return true;
    }
}
