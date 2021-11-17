package htwb.ai.dao;

import htwb.ai.model.Song;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import java.util.List;

public class DBSongDAO implements ISongDAO {

    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager = null;

    public DBSongDAO(String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Override
    public List<Song> getSongs() {
        this.resetEntityManager();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT u FROM Song u"); //JPQL
            List<Song> songList = query.getResultList();
            return songList;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

    }

    @Override
    public List<Song> getSongById(int songId) {
        this.resetEntityManager();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT u FROM Song u WHERE u.id = :id"); //JPQL
            query.setParameter("id", songId);
            List<Song> songList = query.getResultList();
            return songList;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public int addSong(Song song) {
        this.resetEntityManager();
        EntityTransaction transaction = null;

        if (song.getArtist() == null) {
            song.setArtist("");
        }
        if (song.getLabel() == null) {
            song.setLabel("");
        }

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(song);
            transaction.commit();
            return song.getId();
        } catch (IllegalStateException | EntityExistsException | RollbackException exception) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new PersistenceException(exception.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void updateSong(Song song) {
        //https://www.objectdb.com/java/jpa/persistence/update
        this.resetEntityManager();
        EntityTransaction transaction = null;

        if (song.getArtist() == null) {
            song.setArtist("");
        }
        if (song.getLabel() == null) {
            song.setLabel("");
        }

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();

            Song updatedSong = entityManager.find(Song.class, song.getId());
            transaction.begin();
            updatedSong.setTitle(song.getTitle());
            updatedSong.setArtist(song.getArtist());
            updatedSong.setLabel(song.getLabel());
            updatedSong.setReleased(song.getReleased());
            transaction.commit();

        } catch (IllegalStateException | EntityExistsException | RollbackException exception) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new PersistenceException(exception.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

    }

    @Override
    public void deleteSong(int songId) {
        //https://www.objectdb.com/java/jpa/persistence/delete
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();

            Song songToDelete = entityManager.find(Song.class, songId);
            transaction.begin();
            entityManager.remove(songToDelete);
            transaction.commit();

        } catch (IllegalStateException | EntityExistsException | RollbackException | IllegalArgumentException exception) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new PersistenceException(exception.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

    }

    private void resetEntityManager() {
        this.entityManager = null;
    }
}
