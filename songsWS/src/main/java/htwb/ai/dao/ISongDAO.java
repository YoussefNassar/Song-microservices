package htwb.ai.dao;

import htwb.ai.model.Song;

import java.util.List;

public interface ISongDAO {
    public List<Song> getSongs();

    public List<Song> getSongById(int songId);

    public int addSong(Song song);

    public void updateSong(Song song);

    public void deleteSong(int songId);
}
