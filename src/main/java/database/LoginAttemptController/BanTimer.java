package database.LoginAttemptController;

import java.util.Timer;
import java.util.TimerTask;

public class BanTimer {
    Timer timer;
     static boolean isTimeOver;
     static boolean isTimeStarted;

    public static void setIsTimeOver(boolean isTimeOver) {
        BanTimer.isTimeOver = isTimeOver;
    }

    public static void setIsTimeStarted(boolean isTimeStarted) {
        BanTimer.isTimeStarted = isTimeStarted;
    }

    public BanTimer(){
        timer = new Timer();
        timer.schedule(new ReminderTask(), 0, 1000);
    }

    public static boolean checkIsTimerStarted(){
        return isTimeStarted;
    }

    public static boolean checkIsTimerOver(){
        return isTimeOver;
    }

    class ReminderTask extends TimerTask {
       int numOfBanSeconds = 50;
        @Override
        public void run() {
            if (numOfBanSeconds > 0) {
                isTimeOver = false;
                isTimeStarted = true;
                System.out.println("You in ban for " + numOfBanSeconds + " seconds!");
                numOfBanSeconds--;
            } else {
                System.out.println("Ban is over! You can try to login again!");
                timer.cancel();
                isTimeOver = true;
                isTimeStarted = false;
            }
        }
    }
}
