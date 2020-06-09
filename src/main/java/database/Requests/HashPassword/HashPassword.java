package database.Requests.HashPassword;

import com.mongodb.BasicDBObject;
import database.Requests.FindUser.FindUser;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class HashPassword {
    public static final String DEFAULT_ENCODING = "UTF-8";
    static BASE64Encoder enc = new BASE64Encoder();
    static BASE64Decoder dec = new BASE64Decoder();

    public static String makeHash(String password) {
        try {
            return enc.encode(password.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String decode(String password) {
        try {
            return new String(dec.decodeBuffer(password), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }

    //public static String action(String password) {
        /*User user = FindUser.getUserByLogin(login);
        if (user != null && FindUser.findByUserLogin(login)) {
            System.out.println(user.getPassword());

         */
            //String encode = makeHash(password);
            //return decode(dec.de);
    //}
/*
    public static String xorMessage(String password, String key) {
        try {
            if (message == null || key == null) return null;

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }//for i
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }
*/

    public static boolean checkPass(String login, String password) {
        User user = FindUser.getUserByLogin(login);
        if (user != null && FindUser.findByUserLogin(login)) {
            System.out.println(user.getPassword());
            if (decode(user.getPassword()).equals(makeHash(password)))
                return true;
            else
                return false;
        }
        return false;
    }

    public static boolean checkChange(String login, String password) {
        User user = FindUser.getUserByLogin(login);
        if (user != null && FindUser.findByUserLogin(login)) {
            System.out.println(user.getPassword());
            //String temp = decode(user.getPassword());
            if (user.getPassword().equals(password))
                return true;
            else
                return false;
        }
        return false;
    }

    //private static String hashPassword(String password) {
    //return BCrypt.hashpw(password, BCrypt.gensalt(12));
    //}
    //public static String makeHash(String password){
    //return hashPassword(password);
    //}
/*
    public static boolean checkPass(String login, String password) {
        User user = FindUser.getUserByLogin(login);
        if (user != null && FindUser.findByUserLogin(login)) {
            String tempPass = user.getPassword();
            System.out.println(tempPass);
            //System.out.println(BCrypt.checkpw("113", tempPass));
            if (BCrypt.checkpw("113", tempPass)){
                return true;
            }
        }
        return false;

 */
/*
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("login", login);

        if (BCrypt.checkpw(password, basicDBObject.getString("password"))) {
            return true;
        }
        return false;
*/
}
//}
