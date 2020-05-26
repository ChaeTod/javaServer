package database.Logs.GetLog;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import database.Connector.Connector;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetLog {
    public static List<String> getLog(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        //MongoCollection<Document> collection_log = connector.getMongoDatabase().getCollection("Log");

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("login", login);

        List<String> list = new ArrayList<String>();
        for (Document doc : (Iterable<Document>) connector.getLogCollection().find()) {
            JSONObject object = new JSONObject(doc.toJson());
            if (object.getString("login").equals(login)) {
                list.add(object.toString());
            }
        }
        System.out.println(list);
        return list;
    }

        /* Find user via login and show log. */
       // return null;
}
