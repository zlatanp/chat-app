package controller;

import java.util.List;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

public interface UserController {
	
	User register(String username, String password) throws UsernameExistsException;
	Boolean login(String username, String password) throws InvalidCredentialsException;
	Boolean logout(User logout);
	List<User> getAllUsers();
	
}
