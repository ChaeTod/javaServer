package database.Requests.DeleteUser;

import com.mongodb.BasicDBObject;
import database.Connector.Connector;
import com.mongodb.client.FindIterable;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;

public class DeleteUser {
    public static boolean deleteUser(String login, String token) {

        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        obj.put("token", token);
        FindIterable cursor = connector.getUserCollection().find(obj);

        if (FindUser.findByUserLogin(login) && CheckToken.checkToken(token)) {
            if (cursor.iterator().hasNext()) {
                connector.getUserCollection().deleteOne(obj);
            } else {
                //connector.getMongoConnector().close();
                return false;
            }
            //connector.getMongoConnector().close();
            return true;
        }
        //connector.getMongoConnector().close();
        return false;
    }
}
