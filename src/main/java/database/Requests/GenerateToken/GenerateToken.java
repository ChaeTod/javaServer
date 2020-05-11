package database.Requests.GenerateToken;

import org.apache.commons.lang3.RandomStringUtils;

public class GenerateToken {
    private static String generateToken() {
        int length = 27;
        return RandomStringUtils.random(length, true, true); // Generate random string with Apache Commons Lang. Use letters and numbers - true.
    }

    public static String getToken(){
       return generateToken();
    }
}
