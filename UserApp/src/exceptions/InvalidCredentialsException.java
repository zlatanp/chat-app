package exceptions;

@SuppressWarnings("serial")
public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException(){
		System.err.println("You entered invalid credentials!");
	}
}
