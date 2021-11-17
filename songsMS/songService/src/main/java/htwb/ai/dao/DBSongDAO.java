package htwb.ai.dao;

import htwb.ai.model.Song;
import htwb.ai.repo.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DBSongDAO implements ISongDAO {

    final SongRepository songRepository;

    @Autowired
    public DBSongDAO(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> getSongs() {
        return songRepository.findAll();
    }

    @Override
    public Song getSongById(int songId) {
        Optional<Song> songById = this.songRepository.findById(songId);
        return songById.orElse(null);
    }

    @Override
    public int addSong(Song song) {
        Song savedSong = this.songRepository.save(song);
        return savedSong.getId();
    }

    @Override
    public void updateSong(Song song) {
        Optional<Song> songById = this.songRepository.findById(song.getId());

        if (songById.isPresent()) {
            songById.get().setTitle(song.getTitle());
            songById.get().setArtist(song.getArtist());
            songById.get().setLabel(song.getLabel());
            songById.get().setReleased(song.getReleased());
            this.songRepository.save(songById.get());
        }
    }

    @Override
    public void deleteSong(int songId) {
        Optional<Song> songById = this.songRepository.findById(songId);
        songById.ifPresent(this.songRepository::delete);
    }
}
