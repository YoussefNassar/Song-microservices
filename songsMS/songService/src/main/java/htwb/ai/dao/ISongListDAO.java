package htwb.ai.dao;

import htwb.ai.Exception.ForbiddenException;
import htwb.ai.model.SongList;

import java.util.List;

public interface ISongListDAO {

    public List<SongList> getSongListsByUserId(String userId, String realUserId);

    public SongList getSongListByListId(int songListId, String userId) throws ForbiddenException;

    public void deleteSongList(int songListId, String userIdByToken) throws Exception;

    public int addSongList(SongList songlist);

    public void updateSongList(SongList dbSongList, SongList songList, String userId) throws Exception;
}
