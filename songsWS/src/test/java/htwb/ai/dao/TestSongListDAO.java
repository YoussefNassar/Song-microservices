package htwb.ai.dao;

import htwb.ai.exception.ForbiddenException;
import htwb.ai.model.Song;
import htwb.ai.model.SongList;

import javax.persistence.*;
import java.util.List;

public class TestSongListDAO implements ISongListDAO {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager = null;

    public TestSongListDAO(String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<SongList> getSongListsByUserId(String userId, String realUserId) {

        List<SongList> songLists;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            // https://stackoverflow.com/a/28354604/9977775
            if (userId.equals(realUserId)) {
                songLists = entityManager.createQuery("select e from SongList e where e.ownerId = :ownerId", SongList.class) //JPQL
                        .setParameter("ownerId", userId).getResultList();
            } else {
                songLists = entityManager.createQuery("select e from SongList e where e.ownerId = :ownerId and e.isPrivate = false", SongList.class) //JPQL
                        .setParameter("ownerId", userId).getResultList();
            }
            return songLists;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public SongList getSongListByListId(int songListId, String userId) throws ForbiddenException {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            SongList songList = entityManager.find(SongList.class, songListId);
            if (songList.getOwnerId().equals(userId) || !(songList.getIsPrivate())) {
                return songList;
            } else {
                throw new ForbiddenException();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void deleteSongList(int songListId) {
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();

            SongList songListToDelete = entityManager.find(SongList.class, songListId);
            transaction.begin();
            entityManager.remove(songListToDelete);
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

    @Override
    public int addSongList(SongList songlist) {
        //https://www.objectdb.com/java/jpa/persistence/delete
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(songlist);
            transaction.commit();
            return songlist.getId();
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

}
