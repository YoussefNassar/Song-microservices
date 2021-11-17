package htwb.ai.dao;

import htwb.ai.model.User;

public interface IUserDAO {
    public User getUserById(String userId);
    public User getUserByToken(String token);

    public void setToken(String userId, String token);

    public boolean isAuthenticated(String userId, String token);

}
