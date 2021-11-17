package com.example.lyricsservice.repo;

import com.example.lyricsservice.model.SongLyrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LyricsRepository extends MongoRepository<SongLyrics, String> {

    SongLyrics findBySongId(int songId);
}
