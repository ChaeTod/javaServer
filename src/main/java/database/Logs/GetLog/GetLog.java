package database.Logs.GetLog;


import com.mongodb.client.MongoCollection;
import database.Connector.Connector;
import org.bson.Document;

import java.util.List;

public class GetLog {
    public List<String> getLog(String login, String token) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        MongoCollection<Document> collection_log = connector.getMongoDatabase().getCollection("Log");

        /* Find user via login and show log. */
        return null;
    }
}
