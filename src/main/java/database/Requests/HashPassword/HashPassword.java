package database.Requests.HashPassword;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    public static String makeHash(String password){
       return hashPassword(password);
    }
}
