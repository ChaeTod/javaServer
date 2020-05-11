package database.Logs.UserLog;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import org.springframework.http.ResponseEntity;
import sample.User;
import org.bson.Document;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MakeLog {
    public boolean log(String login, String type) {
        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        MongoCollection<Document> collection_log = connector.getMongoDatabase().getCollection("Log");

        BasicDBObject obj = new BasicDBObject();
        obj.append("login", login);

        User user = FindUser.getUser(login);

        assert user != null;
        if (FindUser.findLogin(login) && user.getLogin().equals(login)) {

            connector.getMongoCollection().insertOne(new Document().append("type", type).append("login", login)
                    .append("datetime", getTime()));
            return true;
        }
        return false;
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
