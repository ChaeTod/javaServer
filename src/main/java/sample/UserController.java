package sample;

import database.Logs.UserLog.MakeLog;
import database.Requests.ChangePassword.ChangePassword;
import database.Requests.DeleteUser.DeleteUser;
import database.Requests.FindUser.FindUser;
import database.Requests.GetToken.GetToken;
import database.Requests.LoginUser.LoginUser;
import database.Requests.LogoutUser.LogoutUser;
import database.Requests.SignupUser.AddOneUser;
import database.Logs.*;
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
        DB.getMongoConnector();  // Make a connection to local MongoDB
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {

        System.out.println(data);
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        if (!obj.getString("fname").isEmpty() && !obj.getString("lname").isEmpty() && !obj.getString("login").isEmpty() && !obj.getString("password").isEmpty()) { // vstup je ok, mame vsetky kluce
            if (FindUser.findLogin(obj.getString("login"))) {
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
                res.put("error", "Login already exists");
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }
        res.put("error", "Something is missing");
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
            User user = FindUser.getUser((obj.getString("login")));

            if (LoginUser.loginUser(obj.getString("login"), obj.getString("password"))) {
                assert user != null;
                res.put("fname", user.getFname());
                res.put("lname", user.getLname());
                res.put("login", user.getLogin());
                res.put("token", GetToken.getToken(user.getLogin()));

                localLog.log(obj.getString("login"), "login"); //write changes to the log
                return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            } else {
                res.put("error", "Wrong login or password");
                return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }
        }
        JSONObject res = new JSONObject();
        res.put("error", "Missing login or password");
        return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> logout(@RequestBody String data, @RequestHeader(name = "token") String userToken) {
        JSONObject obj = new JSONObject(data);
        MakeLog localLog = new MakeLog();

        String login = obj.getString("login");
        User user = FindUser.getUser(login);
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
        User user = FindUser.getUser(obj.getString("login"));

        if (!data.isEmpty() && obj.has("login") && obj.has("newPassword") && obj.has("oldPassword") && user != null) {
            if (ChangePassword.changePassword(user.getPassword(), obj.getString("newPassword"),
                    obj.getString("login"), userToken)) {

                user.setPassword(obj.getString("newPassword"));
                localLog.log(obj.getString("login"), "passwordChange");
                return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());

            } else {
                res.put("error", "Wrong password or token");
                return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
            }

        } else {

            res.put("error", "Wrong input");
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{login}")
    public ResponseEntity<String> deleteUser(@RequestHeader(name = "token") String userToken, @PathVariable String userLogin) {
        JSONObject res = new JSONObject();
        User user = FindUser.getUser(userLogin);

        if (user != null && DeleteUser.deleteUser(user.getLogin(), userToken)) {
            return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        } else {
            res.put("Error!", "Wrong user or token is missing!");
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON).body(res.toString());
        }
    }

    /* +++++++++++ */
    /* update user */
    /* send message */
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
