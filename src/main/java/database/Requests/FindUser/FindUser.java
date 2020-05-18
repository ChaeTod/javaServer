package database.Requests.FindUser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import com.mongodb.BasicDBObject;
import org.bson.BSONObject;
import org.bson.BsonObjectId;
import org.bson.conversions.Bson;
import org.bson.Document;
import org.json.JSONObject;
import sample.User;

public class FindUser {
    public static boolean findByUserLogin(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        connector.getMongoCollection();

        Document obj = new Document("login", login);
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

    public static User getUserByLogin(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = Filters.eq("login", login);
        //FindIterable findByUserLogin = connector.getMongoCollection().find(filter);
        Document com = (Document) connector.getMongoCollection().find(filter).first();

        if (findByUserLogin(login) && com != null) {
            return new User(com.getString("fname"), com.getString("lname"),
                    com.getString("login"), com.getString("password"));
        }
        connector.getMongoConnector().close();
        return null;
    }
}
