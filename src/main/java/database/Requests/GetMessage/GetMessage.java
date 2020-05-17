package database.Requests.GetMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;
import org.bson.conversions.Bson;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetMessage {
    public static List<String> getMessage(String login, String token) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson bsonFilter = Filters.eq("from", login);
        FindIterable<Document> findByFromField = connector.getMongoDatabase().getCollection("Messages").find(bsonFilter);

        BasicDBObject findUserByLoginAndToken = new BasicDBObject();
        findUserByLoginAndToken.append("login", login);
        findUserByLoginAndToken.append("token", token);
        FindIterable<Document> findUserByObject = connector.getMongoDatabase().getCollection("Users").find(findUserByLoginAndToken);

        List<String> messageList = new ArrayList<>();
        JSONObject obj = new JSONObject();

        if (FindUser.findLogin(login) && CheckToken.checkToken(token)) {
            if (findUserByObject.iterator().hasNext()) {
                for (Document p : findByFromField) {
                    obj.put("from", p.getString("from"));
                    obj.put("to", p.getString("to"));
                    obj.put("message", p.getString("message"));
                    obj.put("time", p.getString("time"));
                    messageList.add(obj.toString());
                }
            }
        }
        return messageList;
    }
}
