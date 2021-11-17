package htwb.ai.controller;

import htwb.ai.dao.ISongDAO;
import htwb.ai.dao.IUserDAO;
import htwb.ai.model.Song;
import org.springframework.http.HttpHeaders;
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

import javax.persistence.PersistenceException;
import java.util.List;

@RestController
@RequestMapping(value = "/songs")
public class SongController {

    private final ISongDAO dbSongDAO;
    private final IUserDAO dbUserDAO;

    public SongController(ISongDAO dbSongDAO, IUserDAO dbUserDAO) {
        this.dbSongDAO = dbSongDAO;
        this.dbUserDAO = dbUserDAO;
    }


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Song>> getSongs(@RequestHeader("Authorization") String authorization) {
        try {

            if (this.dbUserDAO.getUserByToken(authorization) == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Song> songList = this.dbSongDAO.getSongs();
            System.out.println("VERY GOOD");

            return new ResponseEntity<>(songList, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Song> getSongsById(@PathVariable("id") int songId, @RequestHeader("Authorization") String authorization) {
        try {

            if (this.dbUserDAO.getUserByToken(authorization) == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Song> songList = this.dbSongDAO.getSongById(songId);

            return new ResponseEntity<>(songList.get(0), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<String> createSong(@RequestBody Song song, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {

        try {
            if (this.dbUserDAO.getUserByToken(authorization) == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            //https://stackoverflow.com/a/31647669/9977775
            if (!contentType.equals("application/json")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (song.getTitle() == null || song.getTitle().equals("") || song.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            int songId = dbSongDAO.addSong(song);
            HttpHeaders header = new HttpHeaders();
            header.set("Location", "/songsWS-Marin/rest/songs/" + songId);
            return ResponseEntity.status(204).headers(header).body("");

        } catch (PersistenceException persistenceException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateSong(@RequestBody Song song, @PathVariable("id") int songId, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {

        try {
            if (this.dbUserDAO.getUserByToken(authorization) == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (!contentType.equals("application/json")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (songId != song.getId() || song.getTitle() == null || song.getTitle().equals("") || song.getTitle().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            dbSongDAO.updateSong(song);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (PersistenceException persistenceException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable("id") int songId, @RequestHeader("Authorization") String authorization) {
        try {
            if (this.dbUserDAO.getUserByToken(authorization) == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            dbSongDAO.deleteSong(songId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (PersistenceException persistenceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
