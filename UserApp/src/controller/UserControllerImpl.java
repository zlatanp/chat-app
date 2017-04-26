package controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

@Path("/user")
public class UserControllerImpl implements UserController {

	@Override
	@POST
	@Path("/register")
	public User register(String username, String password) throws UsernameExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("/login")
	public Boolean login(String username, String password) throws InvalidCredentialsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("/logout")
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("/allUsers")
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
