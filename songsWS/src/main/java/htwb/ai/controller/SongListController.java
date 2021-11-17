package htwb.ai.controller;

import htwb.ai.dao.ISongDAO;
import htwb.ai.dao.ISongListDAO;
import htwb.ai.dao.IUserDAO;
import htwb.ai.exception.ForbiddenException;
import htwb.ai.model.Song;
import htwb.ai.model.SongList;
import htwb.ai.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import java.util.List;

@RestController
@RequestMapping(value = "/songLists")
public class SongListController {

    private final ISongListDAO dbSongsListDAO;
    private final IUserDAO dbUserDAO;
    private final ISongDAO dbSongDAO;

    public SongListController(ISongListDAO dbSongsListDAO, IUserDAO dbUserDAO, ISongDAO dbSongDAO) {
        this.dbSongsListDAO = dbSongsListDAO;
        this.dbUserDAO = dbUserDAO;
        this.dbSongDAO = dbSongDAO;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<SongList>> getSongListWithUserId(@RequestParam String userId, @RequestHeader("Authorization") String authorization) {
        List<SongList> foundSongList;
        User realUser = dbUserDAO.getUserByToken(authorization);

        if (realUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            foundSongList = dbSongsListDAO.getSongListsByUserId(userId, realUser.getUserId());

            if (foundSongList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(foundSongList, HttpStatus.OK);
    }


    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongList> getSongListWithSongId(@PathVariable("id") int songListId, @RequestHeader("Authorization") String authorization) {
        SongList foundSongList = new SongList();
        User user = dbUserDAO.getUserByToken(authorization);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            foundSongList = dbSongsListDAO.getSongListByListId(songListId, user.getUserId());

        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(foundSongList, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteSongList(@PathVariable("id") int songListId, @RequestHeader("Authorization") String authorization) {

        User user = dbUserDAO.getUserByToken(authorization);

        if (this.dbUserDAO.getUserByToken(authorization) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            SongList songlist = dbSongsListDAO.getSongListByListId(songListId, user.getUserId());
            dbSongsListDAO.deleteSongList(songListId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<String> createSongList(@RequestBody SongList songlist, @RequestHeader("Content-Type") String contentType, @RequestHeader("Authorization") String authorization) {

        User user = dbUserDAO.getUserByToken(authorization);

        if (this.dbUserDAO.getUserByToken(authorization) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (songlist.getName() == null || songlist.getName().equals("") || songlist.getName().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        songlist.setOwnerId(user.getUserId());

        try {

            for (Song song : songlist.getSongList()) {

                Song dbSong = null;

                try {
                    dbSong = dbSongDAO.getSongById(song.getId()).get(0);
                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Song not found
                }

                if (!(song.getTitle().equals(dbSong.getTitle()))
                || !(song.getLabel().equals(dbSong.getLabel()))
                || !(song.getArtist().equals(dbSong.getArtist()))
                || !(song.getReleased() == dbSong.getReleased())) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

            }

            int songListId = dbSongsListDAO.addSongList(songlist);

            HttpHeaders header = new HttpHeaders();
            header.set("Location", "/songsWS-Marin/rest/songLists/" + songListId);
            return ResponseEntity.status(204).headers(header).body("");

        } catch (PersistenceException persistenceException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
