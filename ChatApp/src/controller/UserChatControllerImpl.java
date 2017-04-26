package controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import exceptions.AliasExistsException;
import model.Host;

@Path("/userChatController")
public class UserChatControllerImpl implements UserChatController {

	@Override
	@POST
	@Path("/register")
	public List<Host> register(String address, String alias) throws AliasExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("/unregister")
	public void unregister(Host host) {
		// TODO Auto-generated method stub
		
	}

}
