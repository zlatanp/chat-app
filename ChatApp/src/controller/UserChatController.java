package controller;

import java.util.ArrayList;


import exceptions.AliasExistsException;
import model.Host;
import model.Message;
import model.User;

public interface UserChatController {

	public String initialise();
	String register(String address, String alias) throws AliasExistsException;
	String unregister();
	String registerUser(String username, String password, String passwordRepeat);
	void addUser(User user);
	void removeUser(User user);
	void publish(Message message);
	public ArrayList<User> getAllUsers();
	void updateCvorove(ArrayList<Host> u);
	
}
