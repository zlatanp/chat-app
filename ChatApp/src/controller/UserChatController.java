package controller;

import java.util.List;

import exceptions.AliasExistsException;
import model.Host;

public interface UserChatController {

	List<Host> register(String address, String alias) throws AliasExistsException;
	void unregister(Host host);
	
}
