package database.Requests.CreateMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;
import database.Requests.GetServerTime.GetServerTime;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateMessage {
    public static void newMessage(String from, String to, String message) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        String timeStamp = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date());
        Document document = new Document();
        document.append("from", from);
        document.append("to", to);
        document.append("message", message);
        document.append("time", timeStamp);
        connector.getMessageCollection().insertOne(document);

        /*
        BasicDBObject loginQuery = new BasicDBObject();
        loginQuery.append("login", from);
        loginQuery.append("token", token);
        FindIterable<Document> findUserByObject = connector.getMongoDatabase().getCollection("Users").find(loginQuery);

        if (FindUser.findByUserLogin(from) && FindUser.findByUserLogin(to) && CheckToken.checkToken(token)) {
            if (findUserByObject.iterator().hasNext()) {
                connector.getMongoDatabase().getCollection("Messages").insertOne(new Document().append("from", from).append("to", to).append("message", message)
                        .append("time", GetServerTime.getTime()));
            } else {
                return false;
            }
        }
        return true;

         */
    }
}
