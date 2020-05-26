package database.Requests.ChangePassword;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import database.Requests.GetToken.GetToken;
import database.Requests.HashPassword.HashPassword;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;

import java.util.Objects;

public class ChangePassword {
    public static boolean changePassword(String oldpassword, String newpassword, String login, String token) {
        //Scanner in = new Scanner(System.in);
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.append("login", login);
        obj.append("oldpassword", oldpassword);
        obj.append("token", token);

        FindIterable<Document> res = connector.getUserCollection().find(obj);
        User user = FindUser.getUserByLogin(login);

        if (user != null && user.getLogin().equalsIgnoreCase(obj.getString("login")) && HashPassword.checkChange(login, obj.getString("oldpassword")) && Objects.equals(GetToken.getToken(user.getLogin()), token)){
            String hashPass = HashPassword.makeHash(newpassword);
            connector.getUserCollection().updateOne(obj, new BasicDBObject("$set", new BasicDBObject("newpassword", hashPass)));
            //connector.getMongoConnector().close();
            return true;
        } else {
            System.out.println("Error in updating!");
            //connector.getMongoConnector().close();
            return false;
        }
    }
}
