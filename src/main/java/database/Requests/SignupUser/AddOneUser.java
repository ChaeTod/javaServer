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
        connector.getUserCollection();

        if (!FindUser.findByUserLogin(login)) {
            Document userInput = new Document();
            userInput.append("fname", fname);
            userInput.append("lname", lname);
            userInput.append("login", login);
            userInput.append("password", HashPassword.makeHash(password));
            connector.getUserCollection().insertOne(userInput);
            //connector.getMongoConnector().close();
            return true;
        } else {
            //connector.getMongoConnector().close();
            return false;
        }
    }
}
