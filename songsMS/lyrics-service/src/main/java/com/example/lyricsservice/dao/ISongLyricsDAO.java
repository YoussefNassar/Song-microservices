package com.example.lyricsservice.dao;

import com.example.lyricsservice.model.SongLyrics;

import java.util.List;

public interface ISongLyricsDAO {
    public List<SongLyrics> getAllSongsLyrics();

    public SongLyrics getSongLyricsById(String songLyricsId);

    public SongLyrics getSongLyricsBySongId(int songId);

    public boolean addSongLyrics(SongLyrics songLyrics);

    public boolean deleteSongLyrics(String songLyricsId);

    public boolean updateSongLyrics(SongLyrics songLyrics);
}
