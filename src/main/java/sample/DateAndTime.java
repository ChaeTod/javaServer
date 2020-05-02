package sample;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.bind.annotation.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class DateAndTime {

    List<User> list = new ArrayList<User>();

    public DateAndTime() {
        list.add(new User("Roman", "Simko", "roman", "heslo"));
        list.add(new User("Artem", "Kozyr", "artem", "113"));
        list.add(new User("admin", "root", "admin", "root"));
    }

    @RequestMapping("/time")
    public ResponseEntity<String> getTime(@RequestParam(value = "login") String userLogin) {
        if (!userLogin.isEmpty()) {
            for (User st : list) {
                if (st.getLogin().equalsIgnoreCase(userLogin)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    return ResponseEntity.status(201).body(formatter.format(date));
                }
            }
            return ResponseEntity.status(400).body("Error! User is supposed to be signed up first!");
        } else {
            return ResponseEntity.status(400).body("Error! User's login is empty!");
        }
    }

    @RequestMapping("/time/hours")
    public ResponseEntity<String> getTimeHour(@RequestParam(value = "login") String userLogin) {
        if (!userLogin.isEmpty()) {
            for (User st : list) {
                if (st.getLogin().equalsIgnoreCase(userLogin)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH");
                    Date date = new Date();
                    return ResponseEntity.status(201).body(formatter.format(date));
                }
            }
            return ResponseEntity.status(400).body("Error! User is supposed to be signed up first!");
        } else {
            return ResponseEntity.status(400).body("Error! User's login is empty!");
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
    public String login(@RequestBody String credential) {
        System.out.println(credential);
        return "{\"Error\":\"Login already exists!\"}";
    }

/*
    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signUp(@RequestBody String data) {
        System.out.println(data);
        return ResponseEntity.status(201).body(data);
    }
*/

    @RequestMapping(method = RequestMethod.POST, value = "/signup")
    public ResponseEntity<String> signup(@RequestBody String data) {
        System.out.println(data);
        JSONObject obj = new JSONObject(data);
        if (obj.has("fname") && obj.has("lname") && obj.has("login") && obj.has("password")) { // vstup je ok, mame vsetky kluce
            if (findLogin(obj.getString("login"))) {
                JSONObject res = new JSONObject();
                res.put("error", "user already exists");
                return ResponseEntity.status(400).body(res.toString());
            }
            String password = obj.getString("password");
            if (password.isEmpty()) {
                JSONObject res = new JSONObject();
                res.put("error", "password is a mandatory field");
                return ResponseEntity.status(400).body(res.toString());
            }
            User user = new User(obj.getString("fname"), obj.getString("lname"), obj.getString("login"), obj.getString("password"));
            list.add(user);
            JSONObject res = new JSONObject();
            res.put("fname", obj.getString("fname"));
            res.put("lname", obj.getString("lname"));
            res.put("login", obj.getString("login"));
            for (User st : list) {
                if (st.getLogin().equalsIgnoreCase(obj.getString("login"))) {
                    st.setToken(BCrypt.hashpw(obj.getString("password"), BCrypt.gensalt(12)));
                }
            }
            return ResponseEntity.status(201).body(res.toString());
        } else {
            JSONObject res = new JSONObject();
            res.put("error", "invalid input");
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

    private String findPassword(String password) {
        for (User user : list) {
            if (user.getPassword().equalsIgnoreCase(password))
                return user.getPassword();
        }
        return "Haven't find any match!";
    }

    /*
        public void makeToken(){
            ResponseEntity<String> getTimeHour(@RequestParam(value = "login") String userLogin) {
                if (!userLogin.isEmpty()) {
                    for (User st : list) {
                        if (st.getLogin().equalsIgnoreCase(userLogin)) {
            BCrypt.hashpw("tajneheslo", BCrypt.gensalt(12));
        }
    */
    @RequestMapping(method = RequestMethod.POST, value = "/users/rembrand")
    public ResponseEntity<String> getUserRembrand(@RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User st : list) {
                if (st.getToken().equalsIgnoreCase(userToken)) {
                    JSONObject res = new JSONObject(st);
                    res.put("fname", st.getFname());
                    res.put("lname", st.getLname());
                    res.put("login", st.getLogin());
                    return ResponseEntity.status(200).body(res.toString());
                }
            }
            return ResponseEntity.status(401).body("Error! User is supposed to be signed up first!");
        } else {
            return ResponseEntity.status(401).body("Error! User's token is empty!");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users")
    public ResponseEntity<String> getUsers(@RequestParam(value = "token") String userToken) {
        if (!userToken.isEmpty()) {
            for (User st : list) {
                if (st.getToken().equalsIgnoreCase(userToken)) {
                    JSONObject res = new JSONObject(st);
                    res.put("fname", st.getFname());
                    res.put("lname", st.getLname());
                    res.put("login", st.getLogin());
                    return ResponseEntity.status(200).body(res.toString());
                }
            }
            return ResponseEntity.status(401).body("Error! User is supposed to be signed up first!");
        } else {
            return ResponseEntity.status(401).body("Error! User's token is empty!");
        }
    }

    /*
    private boolean findPassword(String password) {
        for (User user : list) {
            if (user.getPassword().equalsIgnoreCase(password))
                return true;
        }
        return false;
    }

    private User findInformation(String login) {
        for (User user : list) {
            if (user.getLogin().equalsIgnoreCase(login))
                return user;
        }
        return null;
    }
*/

    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    public ResponseEntity<String> parseJSON(@RequestBody String data) {
        JSONObject obj = new JSONObject(data);
        JSONObject res = new JSONObject();
        System.out.println(obj.getString("login"));
        System.out.println(data);
        res.put("message", "Logouot succesful");
        res.put("login", "kral");
        return ResponseEntity.status(200).body(res.toString());
    }
}
