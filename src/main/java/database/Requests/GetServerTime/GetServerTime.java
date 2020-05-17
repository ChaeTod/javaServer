package database.Requests.GetServerTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetServerTime {
    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date();
        return formatter.format(time);
    }
}
