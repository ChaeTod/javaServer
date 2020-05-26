package database.Requests.FindUser;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.bson.Document;
import org.json.JSONObject;
import sample.User;

import javax.print.Doc;

public class FindUser {
    public static boolean findByUserLogin(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        connector.getUserCollection();

        //Document obj = new Document();
        //obj.append("login", login);
        //FindIterable search = connector.getUserCollection().find(obj);
        //MongoCollection collection = connector.getMongoDatabase().getCollection("Users");
        long totalRecords = connector.getUserCollection().countDocuments(new BsonDocument("login", new BsonString(login)));
        if (totalRecords > 0) {
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
        Document selected = (Document) connector.getUserCollection().find(filter).first();
        //Document selected = (Document) connector.getUserCollection().find(filter).iterator();

        if (findByUserLogin(login) && selected != null) {
            return new User(selected.getString("fname"), selected.getString("lname"), selected.getString("login"), selected.getString("password"));
        }
        //connector.getMongoConnector().close();
        return null;
    }

    //public static User getUserByLogin(String login) {
    public static JSONObject getUserRetry(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = Filters.eq("login", login);
        //FindIterable findByUserLogin = connector.getMongoCollection().find(filter);
        //Document selected = (Document) connector.getUserCollection().find(filter).first();
        //Document selected = (Document) connector.getUserCollection().find(filter).iterator();
        for (Document res : (Iterable<Document>) connector.getUserCollection().find()) {
            JSONObject obj = new JSONObject(res.toJson());
            if (obj.getString("login").equalsIgnoreCase(login)) {
                return obj;
            }
        }
        /*
        if (findByUserLogin(login) && selected != null) {
            return new User(selected.getString("fname"), selected.getString("lname"), selected.getString("login"), selected.getString("password"));
        }

         */
        //connector.getMongoConnector().close();
        return null;
    }
}
