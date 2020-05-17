package database.Requests.CreateMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;
import database.Requests.GetServerTime.GetServerTime;
import org.bson.Document;

public class CreateMessage {
    public static boolean newMessage(String from, String to, String token, String message) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject loginQuery = new BasicDBObject();
        loginQuery.append("login", from);
        loginQuery.append("token", token);
        FindIterable<Document> findUserByObject = connector.getMongoDatabase().getCollection("Users").find(loginQuery);

        if (FindUser.findLogin(from) && FindUser.findLogin(to) && CheckToken.checkToken(token)) {
            if (findUserByObject.iterator().hasNext()) {
                connector.getMongoDatabase().getCollection("Messages").insertOne(new Document().append("from", from).append("to", to).append("message", message)
                        .append("time", GetServerTime.getTime()));
            } else {
                return false;
            }
        }
        return true;
    }
}
