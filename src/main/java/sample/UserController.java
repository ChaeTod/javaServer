package sample;

import com.mongodb.util.JSON;
import database.LoginAttemptController.LoginAttemptService;
import database.Logs.GetLog.GetLog;
import database.Logs.UserLog.MakeLog;
import database.Requests.ChangePassword.ChangePassword;
import database.Requests.CheckToken.CheckToken;
import database.Requests.CreateMessage.CreateMessage;
import database.Requests.DeleteMessage.DeleteUserChat;
import database.Requests.DeleteUser.DeleteUser;
import database.Requests.FindUser.FindUser;
import database.Requests.GenerateToken.GenerateToken;
import database.Requests.GetMessage.GetMessage;
import database.Requests.GetServerTime.GetServerTime;
import database.Requests.GetToken.GetToken;
import database.Requests.HashPassword.HashPassword;
import database.Requests.LoginUser.LoginUser;
import database.Requests.LogoutUser.LogoutUser;
import database.Requests.SignupUser.AddOneUser;
import database.Requests.UpdateUserName.UpdateUserName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import database.Connector.Connector;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
public class UserController {
    Connector DB = new Connector();

    public UserController() {
        DB.inputSettings();
        DB.getMongoConnector();
        //DB.getMongoDatabase();// Make a connection to local MongoDB
    }

    private static int attemptCounter = 0;

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {

        System.out.println(data);
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        if (!obj.getString("fname").isEmpty() && !obj.getString("lname").isEmpty() && !obj.getString("login").isEmpty() && !obj.get("password").toString().isEmpty()) { // vstup je ok, mame vsetky kluce
            if (FindUser.findByUserLogin(obj.getString("login"))) {
                res.put("Error!", "User with the same login already exists!");
                return ResponseEntity.status(400).body(res.toString());
            }

            String password = obj.get("password").toString();
            if (password.isEmpty()) {
                res.put("Error!", "Password is a mandatory field!");
                return ResponseEntity.status(400).body(res.toString());
            }

            //String hashPass = BCrypt.hashpw(obj.getString("password"), BCrypt.gensalt(12));
            String hashPass = HashPassword.makeHash(obj.get("password").toString());

            if (AddOneUser.addOneUser(obj.getString("fname"), obj.getString("lname"), obj.getString("login"), hashPass)) {

                res.put("fname", obj.getString("fname"));
                res.put("lname", obj.getString("lname"));
                res.put("login", obj.getString("login"));

                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("Error!", "Login already exists!");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }
        res.put("Error!", "Check the input - there should be no empty fields!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    //@CrossOrigin(origins = "http://localhost:8080/")  not in use
    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String credential) throws ParseException {
        JSONObject res = new JSONObject();
        if(credential == null){
            res.put("Error!", "Password and login are mandatory fields!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
        JSONObject obj = new JSONObject(credential);
        if (obj.has("login") && obj.has("password")) {

            MakeLog localLog = new MakeLog();
            if (obj.get("password").toString().isEmpty() || obj.get("login").toString().isEmpty()) {
                res.put("Error!", "Password and login are mandatory fields!");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

            User user = FindUser.getUserByLogin(obj.get("login").toString());

            if (!LoginAttemptService.getStatus(attemptCounter)) {
                System.out.println("You got banned! Wait for 50 seconds!");
                res.put("Error!", "You got banned! Wait for 50 seconds!");
                attemptCounter = 0;
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                if (LoginUser.loginUser(obj.get("login").toString(), obj.get("password").toString())) {
                    assert user != null;
                    res.put("fname", user.getFname());
                    res.put("lname", user.getLname());
                    res.put("login", user.getLogin());
                    res.put("token", GetToken.getToken(user.getLogin()));
                    localLog.log(obj.getString("login"), "login"); //write changes to the log
                    return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
                } else {
                    res.put("Error!", "Wrong login or password!");
                    attemptCounter++;
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
                }
            }
        }
        //JSONObject res = new JSONObject();
        res.put("Error!", "Missing login or password!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> logout(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject obj = new JSONObject(data);
        MakeLog localLog = new MakeLog();

        String login = obj.getString("login");
        User user = FindUser.getUserByLogin(login);
        JSONObject res = new JSONObject();
        if (user != null && LogoutUser.logoutUser(login, userToken)) {
            user.setToken(null);
            LogoutUser.logoutUser(obj.getString("login"), userToken);
            localLog.log(obj.getString("login"), "logout");
            attemptCounter = 0;
            res.put("Success!", "Logout successful!");
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
        res.put("Error!", "Incorrect login or token!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {

        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        MakeLog localLog = new MakeLog();
        User user = FindUser.getUserByLogin(obj.getString("login"));

        if (!data.isEmpty() && obj.has("login") && obj.has("newpassword") && obj.has("oldpassword") && user != null) {
            if (ChangePassword.changePassword(user.getPassword(), obj.get("newpassword").toString(), obj.getString("login"), userToken)) {
                //user.setPassword(obj.getString("newpassword"));
                String hashPass = HashPassword.makeHash(obj.get("newpassword").toString());
                user.setPassword(hashPass);
                localLog.log(obj.getString("login"), "Password Changing");
                res.put("Success!", "Password changed!");
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());

            } else {
                res.put("Error!", "Wrong password or token!");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

        } else {
            res.put("Error!", "Wrong input!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    //@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{login}")
    //public ResponseEntity<String> deleteUser(@RequestHeader(name = "token") String userToken, @PathVariable String userLogin) {
    @RequestMapping(method = RequestMethod.DELETE, value = "/delete")
    public ResponseEntity<String> deleteUser(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {
        JSONObject res = new JSONObject();
        JSONObject obj = new JSONObject(data);
        User user = FindUser.getUserByLogin(obj.getString("login"));

        if (user != null && DeleteUser.deleteUser(user.getLogin(), userToken)) {
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            res.put("Error!", "Wrong user or token is missing!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deletechat")
    public ResponseEntity<String> deleteUserChat(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {
        JSONObject res = new JSONObject();
        JSONObject obj = new JSONObject(data);
        User user = FindUser.getUserByLogin(obj.getString("login"));

        if (user != null && DeleteUserChat.deleteUserChat(user.getLogin(), userToken)) {
            res.put("Succeed!", "Messages in chat has been deleted!");
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            res.put("Error!", "Correct user or token is missing!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping("/time")
    public ResponseEntity<String> getTime(@RequestParam(value = "token") String userToken) {
        if (userToken == null) {
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body("{\"Error!\",\"Bad request\"}");
        }
        if (CheckToken.checkToken(userToken)) {
            JSONObject res = new JSONObject();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            res.put("Time", formatter.format(date));
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body("{\"Error!\":\"Invalid token\"}");
        }
    }

    @RequestMapping("/time/hours")
    public ResponseEntity<String> getTimeHours(@RequestParam(value = "token") String userToken) {
        if (userToken == null) {
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body("{\"Error!\",\"Bad request\"}");
        }
        if (CheckToken.checkToken(userToken)) {
            JSONObject res = new JSONObject();
            SimpleDateFormat formatter = new SimpleDateFormat("HH");
            Date date = new Date();
            res.put("Time in hours", formatter.format(date));
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body("{\"Error!\":\"Invalid token\"}");
        }
    }

    /*
        @PatchMapping(value = "update/{login}")
        public ResponseEntity<String> updateLogin(@RequestBody String data, @RequestHeader String token, @PathVariable String login) {
            JSONObject obj = new JSONObject(data);
            JSONObject res = new JSONObject();
            if (CheckToken.checkToken(token)) {
                if (obj.has("firstName")) {
                    //findInformation(login).setFname(obj.getString("firstName"));
                    UpdateUserName.updateFirstName(login, obj.getString("fname"));
                }
                if (obj.has("lastName")) {
                    //findInformation(login).setFname(obj.getString("lastName"));
                    UpdateUserName.updateLastName(login, obj.getString("lname"));
                }
                res.put("Success!", "Data has been changed!");
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("Error!", "Token hasn't been found!");
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }
    */

    @RequestMapping(method = RequestMethod.POST, value = "/log")
    public ResponseEntity showLog(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        //JSONArray logList = res.toJSONArray(GetLog.getLog(obj.getString("login")));
        if (FindUser.findByUserLogin(obj.getString("login")) && CheckToken.checkToken(userToken)) {
            //JSONArray logList = GetLog.getLog(obj.getString("login"));

            res = GetLog.getLog(obj.getString("login"));
//            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
        res.put("Error!", "Token or login hasn't been found!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/message/new")
    public ResponseEntity<String> newMessage(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {
        JSONObject res = new JSONObject();
        if (data.isEmpty()){
            res.put("Error!", "Message body is empty!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
        JSONObject obj = new JSONObject(data);

        if (CheckToken.checkToken(userToken) && FindUser.findByUserLogin(obj.getString("from")) && FindUser.findByUserLogin(obj.getString("to"))) {
            CreateMessage.newMessage(obj.getString("from"), obj.getString("to"), obj.getString("message"));
            res.put("Success!", "Message has been sent");
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            res.put("Error!", "Message hasn't been sent! Check inputs!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages")
    public ResponseEntity<String> showMessages(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        if (CheckToken.checkToken(userToken)) {
            res = GetMessage.getMessage(obj.getString("login"), userToken);
            return ResponseEntity.status(201).body(res.toString());
        } else {
            res.put("Error!", "Login not found!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }
}
