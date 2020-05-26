package database.Requests.CheckPassword;
/*
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;

public class CheckUserPass {
    private boolean checkUserPass(String login, String password) {
        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        obj.put("password", password);

        for (User user : Document document : (Iterable<Document>) connector.getUserCollection().find()) {
            if (user != null && user.getLogin().equalsIgnoreCase(login)) {
                if (BCrypt.checkpw(password, user.getPassword()))
                    //if (user.getPassword().equalsIgnoreCase(password))
                    return true;
            }
        }
        return false;
    }

    public boolean getPassCheck(String login, String password){
        return checkUserPass(login, password);
    }
}
*/