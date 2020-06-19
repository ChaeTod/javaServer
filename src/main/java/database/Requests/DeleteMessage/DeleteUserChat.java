package database.Requests.DeleteMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import database.Connector.Connector;
import database.Requests.CheckToken.CheckToken;
import database.Requests.FindUser.FindUser;

public class DeleteUserChat {
    public static boolean deleteUserChat(String login, String token) {

        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();

        BasicDBObject obj = new BasicDBObject();
        obj.put("from", login);
        //obj.put("token", token);
        FindIterable cursor = connector.getMessageCollection().find(obj);
        //MongoCursor mongoCursor = connector.getMessageCollection().find().iterator();

        if (FindUser.findByUserLogin(login) && CheckToken.checkToken(token) /*&& mongoCursor != null*/) {
            while (cursor.iterator().hasNext()) {
                connector.getMessageCollection().deleteOne(obj);
            }
            return true;
        } else {
            return false;
        }
            //connector.getMongoConnector().close();
        //connector.getMongoConnector().close();
    }
}
