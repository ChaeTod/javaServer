package database.Connector;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
//import net.minidev.json.parser.JSONParser;
import org.bson.Document;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Connector {

    private String url;
    private int port;
    private String namedb;

    public void inputSettings() {
        JSONParser parser = new JSONParser();
        try {
            org.json.simple.JSONObject getConfig = (org.json.simple.JSONObject) parser.parse(new FileReader("D:\\Study\\javaServer\\src\\main\\java\\database\\Connector\\settings.json")); // it's suppose to use try-catch block here to avoid critical errors.
            JSONObject settings = new JSONObject();
            settings.put("url", getConfig.get("url"));
            settings.put("port", getConfig.get("port"));
            settings.put("namedb", getConfig.get("namedb"));

            //if (!settings.has("url")  /*&& settings.has("namedb") && settings.has("port")*/) {
            //System.out.println("There is no such file with the settings, please check again!");
            //} else {
            url = settings.getString("url");
            String portNum = settings.getString("port");
            port = Integer.parseInt(portNum);
            namedb = settings.getString("namedb");
            //}
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public MongoClient getMongoConnector(){
        inputSettings();
        MongoClient mongo = new MongoClient(url, port); //create a mongo client
        System.out.println("Connected to the database successfully");
        return mongo;
    }

    public MongoDatabase getMongoDatabase(){
        return getMongoConnector().getDatabase(namedb);
    }

    public MongoCollection getUserCollection() {
        return getMongoDatabase().getCollection("Users");

    }

    public MongoCollection getLogCollection() {
        return getMongoDatabase().getCollection("Log");
    }

    public MongoCollection getMessageCollection() {
        return getMongoDatabase().getCollection("Messages");
    }
}