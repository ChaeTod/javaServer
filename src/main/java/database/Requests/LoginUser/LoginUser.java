package database.Requests.LoginUser;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import database.Connector.Connector;
import database.Requests.FindUser.FindUser;
import database.Requests.GenerateToken.GenerateToken;
import database.Requests.GetToken.GetToken;
import database.Requests.HashPassword.HashPassword;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import sample.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class LoginUser {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    private static Date currentTime = new Date();

    public static boolean loginUser(String login, String password) {

        Connector connector = new Connector();
        connector.getMongoConnector();
        connector.getMongoDatabase();
        connector.getUserCollection();

        BasicDBObject obj = new BasicDBObject();
        obj.put("login", login);
        obj.put("password", password);

        //String tempPass = obj.getString("password");

        Document input = new Document();
        input.put("login", login);

        User user = FindUser.getUserByLogin(login);

        /*
        BasicDBObject loginQuery = new BasicDBObject();
        loginQuery.put("login", login);

        Bson bsonFilter = Filters.eq("login", login);
        Document myDoc = (Document) connector.getUserCollection().find(bsonFilter).first();
        User temp = FindUser.getUserByLogin(login);
        //for (Document document : connector.getUserCollection().find()) {
        //for (Document res : (Iterable<Document>) connector.getUserCollection().find()) {

        if (myDoc != null) {
            String hashed = myDoc.getString("password");
            if (BCrypt.checkpw(password, hashed)) {
                if (BCrypt.checkpw(password, temp.getPassword())) {
                    BasicDBObject token = new BasicDBObject().append("token", GetToken.getToken(temp.getLogin()));
                    temp.setToken(token.getString("token"));
                    connector.getUserCollection().updateOne(loginQuery, new BasicDBObject("$set", token));
                }else {
                    connector.getMongoConnector().close();
                    return false;
                }
                connector.getMongoConnector().close();
                return true;
            }
        }
        connector.getMongoConnector().close();
        return false;
*/
        //assert user != null;
        if (user == null)
            return false;

        String temp = user.getPassword();
        if (FindUser.findByUserLogin(login) &&  HashPassword.checkPass(login, password) /* && BCrypt.checkpw(password, user.getPassword())*/) {
            BasicDBObject token = new BasicDBObject();
            token.append("token", GenerateToken.getToken());
            //user.setToken(token.getString("token"));
            //assert user != null;
            //user.setToken(GenerateToken.getToken());
            Objects.requireNonNull(FindUser.getUserByLogin(login)).setToken(token.getString("token"));
            connector.getUserCollection().updateOne(input, new BasicDBObject("$set", token));
            setAttemptTime();
            return true;
        } else {
            setAttemptTime();
            return false;
        }
        //System.out.println(document.toJson());
        //}
        //System.out.println("Nothing to found!");
        //return false;


    }

    private static String setAttemptTime(){
        //JSONObject res = new JSONObject(simpleDateFormat.format(currentTime));
        String res = simpleDateFormat.format(currentTime);
        return res.toString();
    }

    public static String getAttempTime(){
        return setAttemptTime();
    }
}
