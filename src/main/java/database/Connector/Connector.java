package database.Connector;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.bson.Document;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Connector {

    private String url;
    private int port;
    private String DBname;

    public void inputSettings(){
        JSONParser obj = new JSONParser();  //JSONParser - deprecated? Find analog?
        try
        {
            Object getOBJ = obj.parse(new FileReader("settings.json")); // it's suppose to use try-catch block here to avoid critical errors.
            JSONObject settings = new JSONObject(getOBJ);

            if (settings.has("url") && settings.has("DBname") && settings.has("port")) {
                url = settings.getString("url");
                String portNum = settings.getString("port");
                port = Integer.parseInt(portNum);
                DBname = settings.getString("DBname");
            } else {
                System.out.println("There is no such file with the settings, please check again!");
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public MongoClient getMongoConnector() {
        MongoClient mongo = new MongoClient(url, port); //create a mongo client
        System.out.println("Connected to the database successfully");
        return mongo;
    }

    public MongoDatabase getMongoDatabase(){
        MongoDatabase database = getMongoConnector().getDatabase(DBname);
        return database;
    }

    public MongoCollection getMongoCollection(){
        MongoCollection<Document> collection = getMongoDatabase().getCollection("Users");
        return collection;
    }
}