package com.example.lyricsservice.controller;

import com.example.lyricsservice.dao.ISongLyricsDAO;
import com.example.lyricsservice.model.Song;
import com.example.lyricsservice.model.SongLyrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/lyrics")
public class SongLyricsController {

    final ISongLyricsDAO dbSongLyricsDAO;
    final RestTemplate restTemplate;

    @Autowired
    public SongLyricsController(ISongLyricsDAO dbSongLyricsDAO, RestTemplate restTemplate) {
        this.dbSongLyricsDAO = dbSongLyricsDAO;
        this.restTemplate = restTemplate;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SongLyrics>> getAllLyrics(@RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<SongLyrics> allSongsLyrics = this.dbSongLyricsDAO.getAllSongsLyrics();

            if (allSongsLyrics.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(allSongsLyrics, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongLyrics> getSongLyricsById(@PathVariable("id") String id, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            SongLyrics songLyricsById = this.dbSongLyricsDAO.getSongLyricsById(id);

            if (songLyricsById == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(songLyricsById, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/songId/{id}")
    public ResponseEntity<?> getSongLyricsBySongId(@PathVariable("id") int songId, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (this.getSongBySongId(authorization, songId) == null) {
                return new ResponseEntity<>("song with id " + songId + " does not exist", HttpStatus.BAD_REQUEST);
            }

            SongLyrics songLyricsBySongId = this.dbSongLyricsDAO.getSongLyricsBySongId(songId);

            if (songLyricsBySongId == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(songLyricsBySongId, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> createSongLyrics(@RequestBody SongLyrics songLyrics, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (this.getSongBySongId(authorization, songLyrics.getSongId()) == null) {
                return new ResponseEntity<>("song with id " + songLyrics.getSongId() + " does not exist", HttpStatus.BAD_REQUEST);
            }

            if (this.dbSongLyricsDAO.addSongLyrics(songLyrics)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("song already has lyrics", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSongLyricsById(@PathVariable("id") String songLyricsId, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (this.dbSongLyricsDAO.deleteSongLyrics(songLyricsId)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("song lyrics with id " + songLyricsId + "does not exist", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateSongLyrics(@RequestBody SongLyrics songLyrics, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (this.getSongBySongId(authorization, songLyrics.getSongId()) == null) {
                return new ResponseEntity<>("song with id " + songLyrics.getSongId() + " does not exist", HttpStatus.BAD_REQUEST);
            }

            if (this.dbSongLyricsDAO.updateSongLyrics(songLyrics)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String getUserByToken(String token) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity httpEntity = new HttpEntity(headers);
        JSONObject jsonObject;

        ResponseEntity<String> result = restTemplate.exchange("http://user-service/auth/check", HttpMethod.GET, httpEntity, String.class, new HashMap<String, String>());

        String body = result.getBody();

        try {
            jsonObject = new JSONObject(body);
        } catch (Exception exception) {
            return "";
        }

        return (String) jsonObject.get("userId");
    }

    private Song getSongBySongId(String token, int songId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity httpEntity = new HttpEntity(headers);
        JSONObject jsonObject;

        ResponseEntity<String> result = restTemplate.exchange("http://song-service/songs/" + songId, HttpMethod.GET, httpEntity, String.class, new HashMap<String, String>());

        String body = result.getBody();

        try {
            jsonObject = new JSONObject(body);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonObject.toString(), Song.class);
        } catch (Exception exception) {
            return null;
        }
    }
}
