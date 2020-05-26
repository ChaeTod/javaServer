package database.Requests.GetMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
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
    public static JSONObject getMessage(String login, String token) {
        /*


        Bson bsonFilter = Filters.eq("from", login);
        FindIterable<Document> findByFromField = connector.getMongoDatabase().getCollection("Messages").find(bsonFilter);

        BasicDBObject findUserByLoginAndToken = new BasicDBObject();
        findUserByLoginAndToken.append("login", login);
        findUserByLoginAndToken.append("token", token);
        FindIterable<Document> findUserByObject = connector.getMongoDatabase().getCollection("Users").find(findUserByLoginAndToken);

        List<String> messageList = new ArrayList<>();
        JSONObject obj = new JSONObject();

        if (FindUser.findByUserLogin(login) && CheckToken.checkToken(token)) {
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

         */

        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        JSONObject messages = new JSONObject();
        MongoCursor mongoCursor = connector.getMessageCollection().find().iterator();
        int count = 0;
        while (mongoCursor.hasNext()) {
            Document doc = (Document) mongoCursor.next();
            JSONObject object = new JSONObject(doc.toJson());
            if (object.getString("from").equals(login) || object.getString("to").equals(login)) {
                count++;
                messages.put(String.valueOf(count), object);
            }
        }
        return messages;
    }
}
