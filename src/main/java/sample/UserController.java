package sample;

import database.Logs.UserLog.MakeLog;
import database.Requests.ChangePassword.ChangePassword;
import database.Requests.CheckToken.CheckToken;
import database.Requests.CreateMessage.CreateMessage;
import database.Requests.DeleteUser.DeleteUser;
import database.Requests.FindUser.FindUser;
import database.Requests.GetMessage.GetMessage;
import database.Requests.GetServerTime.GetServerTime;
import database.Requests.GetToken.GetToken;
import database.Requests.LoginUser.LoginUser;
import database.Requests.LogoutUser.LogoutUser;
import database.Requests.SignupUser.AddOneUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import database.Connector.Connector;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class UserController {
    Connector DB = new Connector();

    public UserController() {
        DB.inputSettings();
        DB.getMongoConnector();  // Make a connection to local MongoDB
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {

        System.out.println(data);
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        if (!obj.getString("fname").isEmpty() && !obj.getString("lname").isEmpty() && !obj.getString("login").isEmpty() && !obj.getString("password").isEmpty()) { // vstup je ok, mame vsetky kluce
            if (FindUser.findByUserLogin(obj.getString("login"))) {
                res.put("Error!", "User with the same login already exists!");
                return ResponseEntity.status(400).body(res.toString());
            }

            String password = obj.getString("password");
            if (password.isEmpty()) {
                res.put("Error!", "Password is a mandatory field!");
                return ResponseEntity.status(400).body(res.toString());
            }

            String hashPass = BCrypt.hashpw(obj.getString("password"), BCrypt.gensalt(12));

            if (AddOneUser.addOneUser(obj.getString("fname"), obj.getString("lname"),
                    obj.getString("login"), hashPass)) {

                res.put("fname", obj.getString("fname"));
                res.put("lname", obj.getString("lname"));
                res.put("login", obj.getString("login"));

                return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("Error!", "Login already exists!");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }
        res.put("Error!", "Check the input - there should be no empty fields!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String credential) {
        JSONObject obj = new JSONObject(credential);

        if (obj.has("login") && obj.has("password")) {
            JSONObject res = new JSONObject();
            MakeLog localLog = new MakeLog();
            if (obj.getString("password").isEmpty() || obj.getString("login").isEmpty()) {
                res.put("error", "Password and login are mandatory fields");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

            User user = FindUser.getUserByLogin((obj.getString("login")));

            if (LoginUser.loginUser(obj.getString("login"), obj.getString("password"))) {
                assert user != null;
                res.put("fname", user.getFname());
                res.put("lname", user.getLname());
                res.put("login", user.getLogin());
                res.put("token", GetToken.getToken(user.getLogin()));
                localLog.log(obj.getString("login"), "login"); //write changes to the log
                return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("Error!", "Wrong login or password!");
                return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }

        JSONObject res = new JSONObject();
        res.put("Error!", "Missing login or password!");
        return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> logout(@RequestBody String data, @RequestHeader(name = "token") String userToken) {
        JSONObject obj = new JSONObject(data);
        MakeLog localLog = new MakeLog();

        String login = obj.getString("login");
        User user = FindUser.getUserByLogin(login);
        if (user != null && LogoutUser.logoutUser(login, userToken)) {
            user.setToken(null);
            LogoutUser.logoutUser(obj.getString("login"), userToken);
            localLog.log(obj.getString("login"), "logout");
            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("{Logout successful!}");
        }
        JSONObject res = new JSONObject();
        res.put("Error!", "Incorrect login or token!");
        return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody String data, @RequestHeader(name = "token") String userToken) {

        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        MakeLog localLog = new MakeLog();
        User user = FindUser.getUserByLogin(obj.getString("login"));

        if (!data.isEmpty() && obj.has("login") && obj.has("newPassword") && obj.has("oldPassword") && user != null) {
            if (ChangePassword.changePassword(user.getPassword(), obj.getString("newPassword"),
                    obj.getString("login"), userToken)) {

                user.setPassword(obj.getString("newPassword"));
                localLog.log(obj.getString("login"), "passwordChange");
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());

            } else {
                res.put("Error!", "Wrong password or token!");
                return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

        } else {
            res.put("Error!", "Wrong input!");
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{login}")
    public ResponseEntity<String> deleteUser(@RequestHeader(name = "token") String userToken, @PathVariable String userLogin) {
        JSONObject res = new JSONObject();
        User user = FindUser.getUserByLogin(userLogin);

        if (user != null && DeleteUser.deleteUser(user.getLogin(), userToken)) {
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            res.put("Error!", "Wrong user or token is missing!");
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
            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body("{\"Error!\":\"Invalid token\"}");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/message/new")
    public ResponseEntity<String> sendMessage(@RequestBody String data, @RequestHeader(name = "Authorization") String userToken) {
        JSONObject obj = new JSONObject(data);
        User user = FindUser.getUserByLogin(obj.getString("from"));
        JSONObject res = new JSONObject();

        if (user != null && obj.has("from") && obj.has("to") && obj.has("message")) {
            if (CreateMessage.newMessage(obj.getString("from"), obj.getString("to"), userToken, obj.getString("message"))) {
                res.put("from", obj.getString("from"));
                res.put("to", obj.getString("to"));
                res.put("message", obj.getString("message"));
                res.put("time", GetServerTime.getTime());
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("Error!", "Some of the message details is missing!");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        } else {
            res.put("Error!", "User hasn't been found, check the input!");
        }
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }


    @RequestMapping(value = "/message")
    public ResponseEntity<String> getMessage(@RequestBody String data, @RequestHeader(name = "Authorization") String token, @RequestParam(required = false) String from) {
        JSONObject obj = new JSONObject(data);
        JSONObject ans = new JSONObject();
        User user = FindUser.getUserByLogin(obj.getString("login"));

        if (user != null && obj.has("login")) {
            //JSONObject full = new JSONObject();
            JSONArray full = new JSONArray();
            for (String st : GetMessage.getMessage(obj.getString("login"), token)) {
                JSONObject res = new JSONObject(st);
                if (from == null) {
                    full.put(res);
                } else {
                    if (FindUser.findByUserLogin(from))
                        if (res.getString("from").equals(from)) {
                            full.put(res);
                            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(full.toString());
                        } else {
                            ans.put("Error!", "Sender was not found!");
                            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(ans.toString());
                        }
                    ans.put("Error!", "User hasn't been found, check the input!");
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(ans.toString());
                }
            }
        }
        ans.put("Error!", "User hasn't been found, check the input!");
        return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(ans.toString());
    }

    /* +++++++++++ */
    /* update user */
    /* +++++++++++ */


/*
    @RequestMapping("/time")
    public ResponseEntity<String> getTime(@RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User user : list) {
                if (user.getToken() != null && user.getToken().equals(userToken)) {
                    JSONObject res = new JSONObject();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    res.put("Time", formatter.format(date));
                    return ResponseEntity.status(201).body(res.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User is supposed to be signed up first!");
            return ResponseEntity.status(400).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "User's login is empty!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/changepassword")
    public ResponseEntity<String> changePass(@RequestBody String data, @RequestParam(value = "token") String
            userToken) {
        JSONObject obj = new JSONObject(data);
        if (!data.isEmpty()) {
            for (User user : list) {
                if (user != null && user.getLogin().equalsIgnoreCase(obj.getString("login")) && BCrypt.checkpw(obj.getString("password"), user.getPassword()) && obj.has("newpassword") && user.getToken() != null && user.getToken().equals(userToken)) {
                    String hash = BCrypt.hashpw(obj.getString("newpassword"), BCrypt.gensalt(12));
                    user.setPassword(hash);
                    return ResponseEntity.status(201).body("Password has changed successfully!");
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "Bad inputs!");
            return ResponseEntity.status(400).body(res.toString());
        }
        JSONObject res = new JSONObject();
        res.put("Error!", "You need to input a login, an old password and a new password!");
        return ResponseEntity.status(400).body(res.toString());
    }

    @RequestMapping("/time/hours")
    public ResponseEntity<String> getTimeHour(@RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User user : list) {
                if (user.getToken() != null && user.getToken().equals(userToken)) {
                    JSONObject res = new JSONObject();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH");
                    Date date = new Date();
                    res.put("Time in hours", formatter.format(date));
                    return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User is supposed to be signed up first!");
            return ResponseEntity.status(400).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "User's login is empty!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

 */
}
