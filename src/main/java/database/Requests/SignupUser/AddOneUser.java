package database.Requests.SignupUser;

import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import database.Requests.HashPassword.HashPassword;
import org.bson.Document;

public class AddOneUser {
    public static boolean addOneUser(String fname, String lname, String login, String password) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        if (FindUser.findLogin(login)) {
            Document users = new Document("fname", fname)
                    .append("lname", lname)
                    .append("login", login)
                    .append("password", HashPassword.makeHash(password));
            connector.getMongoCollection().insertOne(users);
            connector.getMongoConnector().close();
            return true;
        } else {
            connector.getMongoConnector().close();
            return false;
        }
    }
}
