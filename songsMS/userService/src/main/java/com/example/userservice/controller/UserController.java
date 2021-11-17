package com.example.userservice.controller;

import com.example.userservice.dao.IUserDAO;
import com.example.userservice.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth")
public class UserController {

    final IUserDAO dbUserDAO;

    @Autowired
    public UserController(IUserDAO dbUserDAO) {
        this.dbUserDAO = dbUserDAO;
    }

    @GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<User> checkToken(@RequestHeader("Authorization") String authorization) {
        if (authorization.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User userByToken = this.dbUserDAO.getUserByToken(authorization);

        if (userByToken == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            userByToken.setPassword(null);
            userByToken.setToken(null);
            return new ResponseEntity<>(userByToken, HttpStatus.OK);
        }

    }

    @PostMapping()
    public ResponseEntity<String> authenticateUser(@RequestBody String user, @RequestHeader("Content-Type") String contentType) {
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

            User user_from_db = dbUserDAO.getUserByUserId(body.get("userId"));

            if (user_from_db.getPassword().equals(body.get("password"))) {
                String token = body.get("userId") + Long.toHexString(Double.doubleToLongBits(Math.random())); // make it unique with userId in front
                dbUserDAO.setToken(body.get("userId"), token);
                return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN).body(token);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception exception) {
            System.out.println("VERY BAD");
            return ResponseEntity.status(404).body("");
        }
    }
}
