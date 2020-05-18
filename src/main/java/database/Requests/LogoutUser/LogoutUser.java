package database.Requests.LogoutUser;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;
import org.bson.Document;
import sample.User;

public class LogoutUser {
    public static boolean logoutUser(String login, String token) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        obj.put("token", token);

        BasicDBObject checkQuery = new BasicDBObject();
        checkQuery.append("login", login);
        checkQuery.append("token", token);

        FindIterable<Document> res = connector.getMongoCollection().find(checkQuery);

        User user = FindUser.getUserByLogin(login);
        if (FindUser.findByUserLogin(login) && CheckToken.checkToken(token))  // Patrik's variant. Temporary, need to be updated in future.
            if (user.getLogin().equals(login) && res.iterator().hasNext()) {
                connector.getMongoCollection().updateOne(obj, new BasicDBObject("$unset", new BasicDBObject("token", token)));
                user.setToken(null);
                connector.getMongoConnector().close();
            } else {
                connector.getMongoConnector().close();
                return false;
            }
        connector.getMongoConnector().close();
        return true;
    }
}
