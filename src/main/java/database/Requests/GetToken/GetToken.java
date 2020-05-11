package database.Requests.GetToken;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import org.bson.Document;

public class GetToken {
    public static String getToken(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);

        FindIterable search = connector.getMongoCollection().find(obj);
        Document res = new Document();

        if (FindUser.findLogin(login) && search != null) {
            return res.getString("token");
        }
        return null;
    }
}
