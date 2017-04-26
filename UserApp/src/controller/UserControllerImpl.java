package controller;

import java.util.List;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

public class UserControllerImpl implements UserController {

	@Override
	public User register(String username, String password) throws UsernameExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean login(String username, String password) throws InvalidCredentialsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
