package database.Requests.FindUser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import com.mongodb.BasicDBObject;
import org.bson.conversions.Bson;
import org.bson.Document;
import sample.User;

public class FindUser {
    public static boolean findLogin(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        FindIterable search = connector.getMongoCollection().find(obj);

        if (search != null) {
            System.out.println("Login match found!");
            connector.getMongoConnector().close();
            return true;
        } else {
            System.out.println("Haven't found any match by login!");
            connector.getMongoConnector().close();
            return false;
        }
    }

    public static User getUser(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = Filters.eq("login", login);
        Document myDoc = (Document) connector.getMongoCollection().find(filter).first();

        if (findLogin(login) && myDoc != null) {
                return new User(myDoc.getString("fname"), myDoc.getString("lname"),
                        myDoc.getString("login"), myDoc.getString("password"));
        }
        connector.getMongoConnector().close();
        return null;
    }
}
