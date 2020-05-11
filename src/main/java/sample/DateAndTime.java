/* OLD RestController
package sample;

import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class DateAndTime {

    List<User> list = new ArrayList<User>();
    List<String> logList = new ArrayList<String>();
    List<String> messages = new ArrayList<String>();

    public DateAndTime() {
        list.add(new User("Roman", "Simko", "roman", "heslo"));
        list.add(new User("Artem", "Kozyr", "artem", "113"));
        list.add(new User("admin", "root", "admin", "root"));
    }

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

    @RequestMapping("/primenumber/{number}")
    public Boolean isPrimeNumber(@PathVariable int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i < Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    @RequestMapping("/hello")
    public String getHello() {
        return "Hello, how are you?";
    }

    @RequestMapping("/hello/{name}")
    public String getHelloWithName(@PathVariable String name) {
        return "Hello " + name + ". How are you? ";
    }

    @RequestMapping("/hi")
    public String getHiWithName(@RequestParam(value = "firstName") String fname, @RequestParam(value = "age") String age) { // if there is some amount of different variables in the link
        return "Hello! How are you? Your name is: " + fname + " and your age is: " + age;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String credential) {
        JSONObject obj = new JSONObject(credential);

        if (obj.has("login") && obj.has("password")) {
            JSONObject res = new JSONObject();

            if (obj.getString("password").isEmpty() || obj.getString("login").isEmpty()) {
                res.put("Error!", "Password and login are mandatory fields!");
                return ResponseEntity.status(400).body(res.toString());
            }

            if (findLogin(obj.getString("login")) && checkUserPass(obj.getString("login"), obj.getString("password"))) {
                //User loggedUser = getUser(obj.getString("login"));
                User user = getInfo(obj.getString("login"));

                if (user == null) {
                    return ResponseEntity.status(400).body("{}");
                }

                res.put("fname", user.getFname());
                res.put("lname", user.getLname());
                res.put("login", user.getLogin());

                String token = generateToken();
                res.put("token", token);
                user.setToken(token);

                JSONObject log = new JSONObject();
                log.put("User", "Logged into the system");
                log.put("UserLogin", res.getString("login"));
                log.put("fname", res.getString("fname"));
                log.put("When", getTime(res.getString("token")));
                logList.add(log.toString());
                //myList.add(logList.toString());
                System.out.println(logList);

                return ResponseEntity.status(201).body(res.toString());
            } else {
                res.put("Error!", "Invalid login or password!");
                return ResponseEntity.status(400).body(res.toString());
            }

        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "Invalid body request!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    private String generateToken() {
        int length = 27;
        return RandomStringUtils.random(length, true, true); // Generate random string with Apache Commons Lang. Use letters and numbers - true.
    }

    public User getInfo(String login) {
        for (User st : list) {
            if (st.getLogin().equalsIgnoreCase(login))
                return st;
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {
        System.out.println(data);
        JSONObject obj = new JSONObject(data);

        if (!obj.getString("fname").isEmpty() && !obj.getString("lname").isEmpty() && !obj.getString("login").isEmpty() && !obj.getString("password").isEmpty()) { // vstup je ok, mame vsetky kluce
            if (findLogin(obj.getString("login"))) {
                JSONObject res = new JSONObject();
                res.put("Error!", "User with the same login already exists!");
                return ResponseEntity.status(400).body(res.toString());
            }

            String password = obj.getString("password");

            if (password.isEmpty()) {
                JSONObject res = new JSONObject();
                res.put("Error!", "Password is a mandatory field!");
                return ResponseEntity.status(400).body(res.toString());
            }

            String hashPass = BCrypt.hashpw(obj.getString("password"), BCrypt.gensalt(12));
            User user = new User(obj.getString("fname"), obj.getString("lname"), obj.getString("login"), hashPass);
            list.add(user);

            JSONObject res = new JSONObject();
            res.put("fname", obj.getString("fname"));
            res.put("lname", obj.getString("lname"));
            res.put("login", obj.getString("login"));

            return ResponseEntity.status(201).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "Check the input - there should be no empty fields!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    private boolean findLogin(String login) {
        for (User user : list) {
            if (user.getLogin().equalsIgnoreCase(login))
                return true;
        }
        return false;
    }

    private boolean checkUserPass(String login, String password) {
        for (User user : list) {
            if (user != null && user.getLogin().equalsIgnoreCase(login)) {
                if (BCrypt.checkpw(password, user.getPassword()))
                    //if (user.getPassword().equalsIgnoreCase(password))
                    return true;
            }
        }
        return false;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users/{login}")
    public ResponseEntity<String> getUserRembrand(@PathVariable String login, @RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User user : list) {
                if (user != null && user.getLogin().equalsIgnoreCase(login) && user.getToken() != null && user.getToken().equals(userToken)) {
                    JSONObject res = new JSONObject(user);
                    res.put("fname", user.getFname());
                    res.put("lname", user.getLname());
                    res.put("login", user.getLogin());
                    return ResponseEntity.status(201).body(res.toString());
                }
            }
            return ResponseEntity.status(400).body("Error! User is supposed to be signed up first!");
        } else {
            return ResponseEntity.status(400).body("Error! User's token or login is empty!");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users")
    public ResponseEntity<String> getUsers(@RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User user : list) {
                if (user != null && user.getToken() != null && user.getToken().equals(userToken)) {
                    JSONObject res = new JSONObject(user);
                    res.put("fname", user.getFname());
                    res.put("lname", user.getLname());
                    res.put("login", user.getLogin());
                    return ResponseEntity.status(201).body(res.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User is supposed to be signed up first!");
            return ResponseEntity.status(400).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "User's token is empty!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> logout(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject obj = new JSONObject(data);
        User user = getInfo(obj.getString("login"));

        if (user != null && user.getToken() != null && user.getToken().equals(userToken)) {
            JSONObject log = new JSONObject();
            log.put("User", "Logged out from the system");
            log.put("UserLogin", user.getLogin());
            log.put("fname", user.getFname());
            log.put("When", getTime(user.getToken()));
            logList.add(log.toString());
            System.out.println(logList);

            user.setToken(null);
            return ResponseEntity.status(201).body("User has logged out.");
        }

        JSONObject res = new JSONObject();
        res.put("Error!", "Incorrect login or token!");
        return ResponseEntity.status(400).body(res.toString());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/log")
    public ResponseEntity<String> showLog(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        if (!data.isEmpty()) {
            JSONObject objj = new JSONObject(data);
            JSONObject full = new JSONObject();
            for (User user : list) {
                if (user != null && user.getLogin().equals(objj.getString("login")) && user.getToken() != null && user.getToken().equals(userToken)) {
                    int i = 0;
                    for (String st : logList) {
                        //JSONObject res = new JSONObject(st);
                        //if (user != null && user.getLogin().equals(objj.getString("login")) && user.getToken() != null && user.getToken().equals(userToken)) {
                        System.out.println(st);
                        full.put(String.valueOf(i), st);
                        i++;
                    }
                    System.out.println(" ");
                    return ResponseEntity.status(201).body(full.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User hasn't been found!");
            return ResponseEntity.status(400).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "User's login is empty!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages/new")
    public ResponseEntity<String> makeMessages(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        JSONObject objj = new JSONObject(data);
        if (objj.has("from") && objj.has("to") && objj.has("message")) {
            for (User user : list) {
                if (user != null && user.getToken() != null && user.getToken().equals(userToken)) {
                    JSONObject message = new JSONObject();
                    message.put("From", objj.getString("from"));
                    message.put("To", objj.getString("to"));
                    message.put("Message", objj.getString("message"));
                    message.put("When", getTime(user.getToken()));
                    messages.add(message.toString());
                    return ResponseEntity.status(201).body(message.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User hasn't been found!");
            return ResponseEntity.status(400).body(res.toString());

        } else {
            JSONObject res = new JSONObject();
            res.put("Error!", "Something from inputs values is missing!");
            return ResponseEntity.status(400).body(res.toString());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages")
    public ResponseEntity<String> showMessages(@RequestBody String data, @RequestParam(value = "token") String userToken) {
        if (!data.isEmpty()) {
            JSONObject objj = new JSONObject(data);
            JSONObject full = new JSONObject();
            for (User user : list) {
                if (user != null && user.getLogin().equals(objj.getString("login")) && user.getToken() != null && user.getToken().equals(userToken)) {
                    int i = 0;
                    for (String st : messages) {
                        JSONObject res = new JSONObject(st);
                        //if (user != null && user.getLogin().equals(objj.getString("login")) && user.getToken() != null && user.getToken().equals(userToken)) {
                        System.out.println(st);
                        full.put("Message " + String.valueOf(i), res.getString("Message"));
                        full.put("From", res.getString("From"));
                        full.put("To", res.getString("To"));
                        full.put("Time", res.getString("When"));
                        i++;
                    }
                    System.out.println(" ");
                    return ResponseEntity.status(201).body(full.toString());
                }
            }
            JSONObject res = new JSONObject();
            res.put("Error!", "User hasn't been found!");
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
}
*/