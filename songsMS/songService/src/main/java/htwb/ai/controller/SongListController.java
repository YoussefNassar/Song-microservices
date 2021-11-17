package htwb.ai.controller;

import htwb.ai.Exception.ForbiddenException;
import htwb.ai.dao.ISongDAO;
import htwb.ai.dao.ISongListDAO;
import htwb.ai.model.Song;
import htwb.ai.model.SongList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/songLists")
public class SongListController {

    final ISongDAO dbSongDAO;
    final ISongListDAO dbSongListDAO;
    final RestTemplate restTemplate;


    public SongListController(ISongDAO dbSongDAO, ISongListDAO dbSongListDAO, RestTemplate restTemplate) {
        this.dbSongDAO = dbSongDAO;
        this.dbSongListDAO = dbSongListDAO;
        this.restTemplate = restTemplate;
    }

    // tested
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SongList>> getSongListWithUserId(@RequestParam String userId, @RequestHeader("Authorization") String authorization) {
        List<SongList> songListsByUserId;
        try {
            String userIdByToken = this.getUserByToken(authorization);

            if (userIdByToken.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            songListsByUserId = this.dbSongListDAO.getSongListsByUserId(userId, userIdByToken);

            if (songListsByUserId.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(songListsByUserId, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //tested
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongList> getSongListWithSongListId(@PathVariable("id") int songListId, @RequestHeader("Authorization") String authorization) {
        SongList foundSongList = new SongList();
        try {
            String userIdByToken = this.getUserByToken(authorization);

            if (userIdByToken.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            foundSongList = dbSongListDAO.getSongListByListId(songListId, userIdByToken);

            if (foundSongList == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(foundSongList, HttpStatus.OK);
    }

    //tested
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<String> createSongList(@RequestBody SongList songlist, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {
        try {
            String userIdByToken = this.getUserByToken(authorization);

            if (userIdByToken.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            songlist.setOwnerId(userIdByToken);

            if (songlist.getName() == null || songlist.getName().equals("") || songlist.getName().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            for (Song song : songlist.getSongList()) {
                Song dbSong = null;


                dbSong = this.dbSongDAO.getSongById(song.getId());

                if (dbSong == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Song not found
                }

                if (!(song.getTitle().equals(dbSong.getTitle()))
                        || !(song.getLabel().equals(dbSong.getLabel()))
                        || !(song.getArtist().equals(dbSong.getArtist()))
                        || !(song.getReleased() == dbSong.getReleased())) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            int songListId = this.dbSongListDAO.addSongList(songlist);

            HttpHeaders header = new HttpHeaders();
            header.set("Location", "/song-service/songLists/" + songListId);
            return ResponseEntity.status(204).headers(header).body("");

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //tested
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSongList(@PathVariable("id") int songListId, @RequestHeader("Authorization") String authorization) {
        try {
            String userIdByToken = this.getUserByToken(authorization);

            if (userIdByToken.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            this.dbSongListDAO.deleteSongList(songListId, userIdByToken);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //tested
    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateSongList(@PathVariable("id") int songListId, @RequestBody SongList songlist, @RequestHeader("Authorization") String authorization) {
        try {
            String userIdByToken = this.getUserByToken(authorization);

            if (userIdByToken.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            SongList songListByListId = this.dbSongListDAO.getSongListByListId(songListId, userIdByToken);

            if (songListByListId == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            this.dbSongListDAO.updateSongList(songListByListId, songlist, userIdByToken);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
