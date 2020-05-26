package database.Requests.UpdateUserName;

import database.Connector.Connector;
import org.bson.Document;
import org.bson.conversions.Bson;

public class UpdateUserName {
    public static void updateFirstName(String name, String fname) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = new Document("fname", name);
        Bson newValue = new Document("fname", fname);
        Bson updateOperationDocument = new Document("$set", newValue);
        connector.getUserCollection().updateOne(filter, updateOperationDocument);
    }

    public static void updateLastName(String name, String lname) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = new Document("fname", name);
        Bson newValue = new Document("fname", lname);
        Bson updateOperationDocument = new Document("$set", newValue);
        connector.getUserCollection().updateOne(filter, updateOperationDocument);
    }
}
