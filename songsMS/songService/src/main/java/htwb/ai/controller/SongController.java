package htwb.ai.controller;

import htwb.ai.dao.DBSongDAO;
import htwb.ai.dao.ISongDAO;
import htwb.ai.model.Song;
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
@RequestMapping(value = "/songs")
public class SongController {

    final ISongDAO dbSongDAO;
    final RestTemplate restTemplate;

    @Autowired
    public SongController(DBSongDAO dbSongDAO, RestTemplate restTemplate) {
        this.dbSongDAO = dbSongDAO;
        this.restTemplate = restTemplate;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Song>> getSongs(@RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Song> songList = this.dbSongDAO.getSongs();
            return new ResponseEntity<>(songList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Song> getSongById(@PathVariable("id") int songId, @RequestHeader("Authorization") String authorization) {
        try {

            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Song songById = this.dbSongDAO.getSongById(songId);

            if (songById != null) {
                return new ResponseEntity<>(songById, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<String> createSong(@RequestBody Song song, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (!contentType.equals("application/json")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (song.getTitle() == null || song.getTitle().equals("") || song.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            int songId = this.dbSongDAO.addSong(song);
            HttpHeaders header = new HttpHeaders();
            header.set("Location", "/songs/" + songId);
            return ResponseEntity.status(204).headers(header).body("");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateSong(@RequestBody Song song, @PathVariable("id") int songId, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (!contentType.equals("application/json")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (songId != song.getId() || song.getTitle() == null || song.getTitle().equals("") || song.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            this.dbSongDAO.updateSong(song);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable("id") int songId, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.getUserByToken(authorization).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            this.dbSongDAO.deleteSong(songId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
}
