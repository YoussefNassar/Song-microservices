package com.example.lyricsservice.dao;

import com.example.lyricsservice.model.SongLyrics;
import com.example.lyricsservice.repo.LyricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DBSongLyricsDAO implements ISongLyricsDAO {

    final LyricsRepository lyricsRepository;

    @Autowired
    public DBSongLyricsDAO(LyricsRepository lyricsRepository) {
        this.lyricsRepository = lyricsRepository;
    }

    @Override
    public List<SongLyrics> getAllSongsLyrics() {
        return this.lyricsRepository.findAll();
    }

    @Override
    public SongLyrics getSongLyricsById(String songLyricsId) {
        Optional<SongLyrics> songLyricsById = this.lyricsRepository.findById(songLyricsId);
        return songLyricsById.orElse(null);
    }

    @Override
    public SongLyrics getSongLyricsBySongId(int songId) {
        return this.lyricsRepository.findBySongId(songId);
    }

    @Override
    public boolean addSongLyrics(SongLyrics songLyrics) {
        SongLyrics songLyricsBySongId = this.getSongLyricsBySongId(songLyrics.getSongId());

        if (songLyricsBySongId == null) {
            this.lyricsRepository.save(songLyrics);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteSongLyrics(String songLyricsId) {
        Optional<SongLyrics> songLyricsById = this.lyricsRepository.findById(songLyricsId);
        if (songLyricsById.isPresent()) {
            this.lyricsRepository.deleteById(songLyricsId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateSongLyrics(SongLyrics songLyrics) {
        SongLyrics songLyrics1 = getSongLyricsBySongId(songLyrics.getSongId());

        if (songLyrics1 != null) {
            songLyrics1.setSongLyrics(songLyrics.getSongLyrics());
            this.lyricsRepository.save(songLyrics1);
            return true;
        } else {
            return false;
        }
    }
}
