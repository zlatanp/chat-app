package exceptions;

@SuppressWarnings("serial")
public class UsernameExistsException extends RuntimeException{
	
	public UsernameExistsException(){
		System.err.println("Choosen username already exist!");
	}

}
