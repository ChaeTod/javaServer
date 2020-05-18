package database.Requests.ChangePassword;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import database.Requests.HashPassword.HashPassword;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;

public class ChangePassword {
    public static boolean changePassword(String oldPassword, String newPassword, String login, String token) {
        //Scanner in = new Scanner(System.in);
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.append("login", login);
        obj.append("password", oldPassword);
        obj.append("token", token);

        FindIterable<Document> res = connector.getMongoCollection().find(obj);
        User user = FindUser.getUserByLogin(login);

        if (user != null && user.getLogin().equalsIgnoreCase(obj.getString("login")) && BCrypt.checkpw(obj.getString("password"), user.getPassword()) && user.getToken() != null && user.getToken().equals(token)){
            connector.getMongoCollection().updateOne(obj, new BasicDBObject("password", HashPassword.makeHash(newPassword)));
            connector.getMongoConnector().close();
            return true;
        } else {
            System.out.println("Error in updating!");
            connector.getMongoConnector().close();
            return false;
        }
    }
}
