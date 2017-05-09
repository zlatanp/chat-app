package jms;

import java.io.Serializable;

public class UserMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4085178134013924849L;
	
	public static enum types {
		REGISTER, LOGIN, LOGOUT
	}
	private String username;
	private String password;
	private types type;
	
	public UserMessage(){}

	public UserMessage(String username, String password, types type) {
		super();
		this.username = username;
		this.password = password;
		this.type = type;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public types getType() {
		return type;
	}

	public void setType(types type) {
		this.type = type;
	}
	
	

}
