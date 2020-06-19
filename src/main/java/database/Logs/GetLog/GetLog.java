package database.Logs.GetLog;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetLog {
    public static JSONObject getLog(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        JSONObject logs = new JSONObject();
        MongoCursor mongoCursor = connector.getLogCollection().find().iterator();
        int count = 0;
        while (mongoCursor.hasNext()) {
            Document doc = (Document) mongoCursor.next();
            JSONObject object = new JSONObject(doc.toJson());
            if (object.getString("login").equals(login) || CheckToken.checkToken(object.getString("login"))) {
                count++;
                logs.put(String.valueOf(count), object);
            }
        }
        return logs;
    }
}
