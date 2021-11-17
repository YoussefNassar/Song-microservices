package htwb.ai.dao;

import htwb.ai.exception.ForbiddenException;
import htwb.ai.model.SongList;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import java.util.List;

public class DBSongListDAO implements ISongListDAO {

    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager = null;

    public DBSongListDAO(String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Override
    public List<SongList> getSongListsByUserId(String userId, String realUserId) {
        this.resetEntityManager();
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
        this.resetEntityManager();

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
        //https://www.objectdb.com/java/jpa/persistence/delete
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
        this.resetEntityManager();
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

    private void resetEntityManager() {
        this.entityManager = null;
    }
}
