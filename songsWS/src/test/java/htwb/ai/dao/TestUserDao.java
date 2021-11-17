package htwb.ai.dao;

import htwb.ai.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


public class TestUserDao implements IUserDAO {

    List<User> users = new ArrayList<>();

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager = null;

    public TestUserDao(String persistenceUnitName) {
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

    public void disableForeignKeyCheck() {
        // https://stackoverflow.com/questions/5452760/how-to-truncate-a-foreign-key-constrained-table
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();

            Query query = session.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0");
            query.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void enableForeignKeyCheck() {
        // https://stackoverflow.com/questions/5452760/how-to-truncate-a-foreign-key-constrained-table
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();

            Query query = session.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1");
            query.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Transactional
    public void resetSongsInSonglistsTable() {
        // https://www.baeldung.com/jpa-transaction-required-exception
        try {
            disableForeignKeyCheck();
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();

            Query truncateSongsInSonglistsQuery = session.createNativeQuery("TRUNCATE TABLE beleg3_test.songs_in_songlists ");
            truncateSongsInSonglistsQuery.executeUpdate();
            transaction.commit();

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Transactional
    public void resetSonglistsTable() {
        try {
            disableForeignKeyCheck();
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query truncateSongsInSonglistsQuery = session.createNativeQuery("TRUNCATE TABLE songlists");
            truncateSongsInSonglistsQuery.executeUpdate();
            transaction.commit();

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Transactional
    public void resetSongsTable() {
        try {
            disableForeignKeyCheck();
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query truncateSongsInSonglistsQuery = session.createNativeQuery("TRUNCATE TABLE songs");
            truncateSongsInSonglistsQuery.executeUpdate();
            transaction.commit();

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Transactional
    public void resetUserTable() {
        try {
            disableForeignKeyCheck();
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query truncateSongsInSonglistsQuery = session.createNativeQuery("TRUNCATE TABLE user");
            truncateSongsInSonglistsQuery.executeUpdate();
            transaction.commit();

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }


    public void populateUserTable() {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query insertIntoUserQuery = session.createNativeQuery("INSERT INTO beleg3_test.user (userId, password, firstName, lastName, token) VALUES " +
                    " ('eschuler', 'pass1234', 'Elena', 'Schuler', 'eschuler123456'), " +
                    " ('mmuster', 'pass1234', 'Maxime', 'Muster', 'mmuster123456'),  " +
                    " ('youssef', '123', 'first', 'last', 'youssef123456');");
            insertIntoUserQuery.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void populateSongsTable() {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query insertIntoSongsQuery = session.createNativeQuery("INSERT INTO beleg3_test.songs (title,artist,label,released) VALUES" +
                    " ('Wrecking Balddls','Hallo','RCA',0), "+
                    " ('Sussudio2','youssef Cyrus','123',2013), " +
                    " ('Sussudio2','youssef Cyruasdas','asdasd',2013), " +
                    " ('Wrecking Balddls','Hallo','RCA',2015), " +
                    " ('Sussudio2','youssef Cyrus','123',2013), " +
                    " ('Wrecking ','Hallo','RCA',0), " +
                    " ('Wrecking ','','',0), " +
                    " ('777 title','artist 777','label 777',2017), " +
                    " ('777 title','artist 777','label 777',2017);");
            insertIntoSongsQuery.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void populateSongslistsTable() {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query insertIntosonglistsQuery = session.createNativeQuery("INSERT INTO beleg3_test.songlists (ownerId,isPrivate,name) VALUES " +
                    " ('youssef',0,'ylist'), " +
                    " ('youssef',1,'ylist2'), " +
                    " ('mmuster',1,'MaximesPrivate'), " +
                    " ('mmuster',1,'MaximesPrivate');");
            insertIntosonglistsQuery.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void populateSongsInSonglistsTable() {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            Transaction transaction = session.beginTransaction();
            Query insertIntoSongsInSonglistsQuery = session.createNativeQuery("INSERT INTO beleg3_test.songs_in_songlists (songId,songListId) VALUES " +
                    " (1,1), " +
                    " (2,1), " +
                    " (3,2); ");
            insertIntoSongsInSonglistsQuery.executeUpdate();
            transaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public User getUserById(String userId) {

        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.userId = :id"); //JPQL
            query.setParameter("id", userId);
            List<User> userList = query.getResultList();
            return userList.get(0);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void setToken(String userId, String token) {
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();

            User updatedUser = entityManager.find(User.class, userId);
            transaction.begin();
            updatedUser.setToken(token);
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
    public boolean isAuthenticated(String userId, String token) {
        try {
            User user =  this.getUserById(userId);

            if(user.getToken().equals(token)) {
                return true;
            }

            return false;

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
    public User getUserByToken(String token) {

        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.token = :token"); //JPQL
            query.setParameter("token", token);
            List<User> userList = query.getResultList();

            if(userList.isEmpty()) {
                return null;
            }

            return userList.get(0);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
