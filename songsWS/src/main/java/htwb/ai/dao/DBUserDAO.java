package htwb.ai.dao;

import htwb.ai.model.Song;
import htwb.ai.model.User;

import javax.persistence.*;
import java.util.List;

public class DBUserDAO implements IUserDAO {

    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager = null;

    public DBUserDAO(String persistenceUnitName) {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Override
    public User getUserById(String userId) {
        this.resetEntityManager();

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
    public User getUserByToken(String token) {
        this.resetEntityManager();

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

    @Override
    public void setToken(String userId, String token) {
        this.resetEntityManager();
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
        this.resetEntityManager();

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

    private void resetEntityManager() {
        this.entityManager = null;
    }
}
