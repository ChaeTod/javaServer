package database.Requests.LoginUser;

import com.mongodb.BasicDBObject;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import database.Requests.GenerateToken.GenerateToken;
import database.Requests.HashPassword.HashPassword;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;

public class LoginUser {
    public static boolean loginUser(String login, String password) {

        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        obj.put("password", password);

        Document input = new Document();
        input.put("login", login);

        User user = FindUser.getUser(login);
        for (Document document : (Iterable<Document>) connector.getMongoCollection().find()) {
            if (FindUser.findLogin(login) && BCrypt.checkpw(obj.getString("password"), user.getPassword())){
                BasicDBObject token = new BasicDBObject().append("token", GenerateToken.getToken());
                user.setToken(token.getString("token"));
                return true;
            } else {
                return false;
            }
            //System.out.println(document.toJson());
        }

        System.out.println("Nothing to found!");
        return false;
    }
}
