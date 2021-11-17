package htwb.ai.dao;

import htwb.ai.Exception.ForbiddenException;
import htwb.ai.model.SongList;
import htwb.ai.repo.SongListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DBSongListDAO implements ISongListDAO {

    final SongListRepository songListRepository;

    @Autowired
    public DBSongListDAO(SongListRepository songListRepository) {
        this.songListRepository = songListRepository;
    }

    /**
     * realUserId is the one from the token
     * (songList = null) when song list is not found
     */
    @Override
    public List<SongList> getSongListsByUserId(String userId, String realUserId) {
        List<SongList> songList;

        if (userId.equals(realUserId)) {
            songList = this.songListRepository.findByOwnerId(userId);
        } else {
            songList = this.songListRepository.findByOwnerIdAndNotPrivate(userId);
        }
        return songList;
    }

    @Override
    public SongList getSongListByListId(int songListId, String userId) throws ForbiddenException {
        Optional<SongList> songList;
        songList = this.songListRepository.findById(songListId);

        if (songList.isPresent()) {
            if (songList.get().getOwnerId().equals(userId) || !songList.get().getIsPrivate()) {
                return songList.get();
            } else {
                throw new ForbiddenException();
            }
        } else {
            return null;
        }
    }

    @Override
    public void deleteSongList(int songListId, String userIdByToken) throws Exception {
        Optional<SongList> songListById = this.songListRepository.findById(songListId);

        if (songListById.isPresent()) {
            if (songListById.get().getOwnerId().equals(userIdByToken)) {
                this.songListRepository.delete(songListById.get());
            } else {
                throw new ForbiddenException();
            }
        } else {
            throw new Exception();
        }
    }

    @Override
    public int addSongList(SongList songlist) {
        songlist = this.songListRepository.save(songlist);
        return songlist.getId();
    }

    @Override
    public void updateSongList(SongList dbSongList, SongList songList, String userId) throws ForbiddenException {
        if (dbSongList.getOwnerId().equals(userId)) {
            dbSongList.setName(songList.getName());
            dbSongList.setIsPrivate(songList.getIsPrivate());
            dbSongList.setSongList(songList.getSongList());
            this.songListRepository.save(dbSongList);
        } else {
            throw new ForbiddenException();
        }
    }
}
