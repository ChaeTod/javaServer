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

        long totalRecords = connector.getUserCollection().countDocuments(obj);
        if (totalRecords > 0) {
            System.out.println("Token has been found!");
            connector.getMongoConnector().close();
            return true;
        } else {
            System.out.println("That token hasn't been found!");
            connector.getMongoConnector().close();
            return false;
        }
    }
}
