package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import jms.UserJMS;
import jms.tochat.MessageToChatImpl;
import model.User;

@Path("/userController")
public class UserControllerImpl implements UserController{
	
	@Context
	ServletConfig config;
	
	@Context
	private UriInfo uriInfo;

	@Override
	@GET
	@Path("/register/{username}/{password}")
	public String register(@PathParam("username") String username, @PathParam("password") String password) throws UsernameExistsException {
		
		String s = config.getServletContext().getRealPath("");
		String databasePath = s.substring(0, 42);
		ArrayList<User> allUsers = getAllUsersFromFile(databasePath);
		
		if(username.isEmpty() || password.isEmpty())
			return null;
		
		for(int i=0;i<allUsers.size(); i++){
			if((allUsers.get(i).getUsername()).equals(username) && (allUsers.get(i).getPassword()).equals(password)){
				throw new UsernameExistsException();
			}
		}
		addUserInFile(databasePath, username, password);
		
		User u = new User();
		u.setUsername(username);
		u.setPassword(password);
		return "added";
	}

	@Override
	@GET
	@Path("/login/{username}/{password}")
	public Boolean login(@PathParam("username") String username, @PathParam("password") String password) throws InvalidCredentialsException {
		String s = config.getServletContext().getRealPath("");
		String databasePath = s.substring(0, 42);
		ArrayList<User> allUsers = getAllUsersFromFile(databasePath);
		
		if(username.isEmpty() || password.isEmpty()){
			throw new InvalidCredentialsException();
		}
		
		
		for(int i=0;i<allUsers.size(); i++){
			if((allUsers.get(i).getUsername()).equals(username) && (allUsers.get(i).getPassword()).equals(password)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	@GET
	@Path("/logout/{username}")
	public Boolean logout(String username) {
		String s = config.getServletContext().getRealPath("");
		String databasePath = s.substring(0, 42);
		ArrayList<User> allUsers = getAllUsersFromFile(databasePath);
		
		if(username.isEmpty())
			return false;
		
		for(int i=0;i<allUsers.size(); i++){
			if((allUsers.get(i).getUsername()).equals(username)){
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	@GET
	@Path("/allUsers")
	public List<User> getAllUsers() {
		String s = config.getServletContext().getRealPath("");
		String databasePath = s.substring(0, 42);
		ArrayList<User> allUsers = getAllUsersFromFile(databasePath);
		MessageToChatImpl m = new MessageToChatImpl();
		m.addUser(new User());
		return allUsers;
	}

	//JSON read/write
	public static synchronized ArrayList<User> getAllUsersFromFile(String path){
		ArrayList<User> allUsers = new ArrayList<User>();
		String generatedPath = FilePaths.getPath(path).getPath();
		String FileName = generatedPath + "//RegisteredUsers.txt";
		
		ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
		try {
			jsons = ReadJSON(new File(FileName), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < jsons.size(); i++) {
			String iusername = (String) jsons.get(i).get("username");
			String ipassword = (String) jsons.get(i).get("password");
			
			User u = new User();
			u.setUsername(iusername);
			u.setPassword(ipassword);
			allUsers.add(u);
		}
		return allUsers;
	}
	
	@SuppressWarnings("resource")
	public static synchronized ArrayList<JSONObject> ReadJSON(File MyFile, String Encoding) throws FileNotFoundException, ParseException {
		Scanner scn = new Scanner(MyFile, Encoding);
		ArrayList<JSONObject> json = new ArrayList<JSONObject>();
		// Reading and Parsing Strings to Json
		while (scn.hasNext()) {
			JSONObject obj = (JSONObject) new JSONParser().parse(scn.nextLine());
			json.add(obj);
		}
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized void addUserInFile(String path, String username, String password) {
		String generatedPath = FilePaths.getPath(path).getPath();
		
		JSONObject userObj = new JSONObject();
		userObj.put("username", username);
		userObj.put("password", password);
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(generatedPath + "//RegisteredUsers.txt", true));
			writer.write(userObj.toJSONString());
			writer.newLine();
			writer.close();
		} catch (Exception e) {
			javax.ws.rs.core.Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path("/getMasterHost")
	public String getMasterHost(){
		return uriInfo.getBaseUri().toString();
	}

	
}
