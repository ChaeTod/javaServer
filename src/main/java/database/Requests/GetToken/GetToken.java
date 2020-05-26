package database.Requests.GetToken;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import sample.User;

import java.util.Objects;

public class GetToken {
    public static String getToken(String login) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        Bson filter = Filters.eq("login", login);
        Document res = (Document) connector.getUserCollection().find(filter).first();
        long totalRecords = connector.getUserCollection().countDocuments(new BsonDocument("login", new BsonString(login)));

        if (FindUser.findByUserLogin(login) && totalRecords > 0) {
            assert res != null;
            return res.getString("token");
        }
        return null;
    }
}
