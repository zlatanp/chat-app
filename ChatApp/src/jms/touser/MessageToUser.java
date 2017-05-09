package jms.touser;

import javax.ejb.Local;

import model.User;

@Local
public interface MessageToUser {
	
	public void registerMessage(String username, String password);
    public void loginMessage(String username, String password, String sessionId);
    public void logoutMessage(User user, String sessionId);
    public void getRegisteredUsers();
    public void getActiveUsers();
}
