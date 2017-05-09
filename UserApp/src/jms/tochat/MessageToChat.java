package jms.tochat;

import model.User;

public interface MessageToChat {

	public void addUser(User user);
    public void removeUser(User user);
}
