package htwb.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.dao.IUserDAO;
import htwb.ai.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth")
public class UserController {

    private final IUserDAO dbUserDAO;

    public UserController(IUserDAO dbUserDAO) {
        this.dbUserDAO = dbUserDAO;
    }

    @PostMapping()
    public ResponseEntity<String> authenticateUser(@RequestBody String user, @RequestHeader("Content-Type") String contentType) {
        //https://stackoverflow.com/a/31647669/9977775
        if (!contentType.equals("application/json")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> body = mapper.readValue(user, Map.class);

            if (body.get("userId") == null || body.get("userId").equals("") || body.get("userId").trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (body.get("password") == null || body.get("password").equals("") || body.get("password").trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            User user_from_db = dbUserDAO.getUserById(body.get("userId"));

            if (user_from_db.getPassword().equals(body.get("password"))) {
                String token = body.get("userId") + Long.toHexString(Double.doubleToLongBits(Math.random())); // make it unique with userId in front
                dbUserDAO.setToken(body.get("userId"), token);
                return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN).body(token);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }


        } catch (Exception exception) {
            System.out.println("VERY BAD");
            return ResponseEntity.status(404).body(exception.toString());
        }

    }

}