package htwb.ai.dao;

import htwb.ai.exception.ForbiddenException;
import htwb.ai.model.SongList;

import java.util.List;

public interface ISongListDAO {

    public List<SongList> getSongListsByUserId(String userId, String realUserId);

    public SongList getSongListByListId(int songListId, String userId) throws ForbiddenException;

    public void deleteSongList(int songListId);

    public int addSongList(SongList songlist);
}
