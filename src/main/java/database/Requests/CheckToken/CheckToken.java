package database.Requests.CheckToken;

import com.mongodb.BasicDBObject;
import database.Connector.Connector;

public class CheckToken {
    public static boolean checkToken(String token) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.append("token", token);

        long count = connector.getMongoCollection().countDocuments(obj);
        if (count > 0) {
            connector.getMongoConnector().close();
            return true;
        }
        connector.getMongoConnector().close();
        return false;
    }
}
