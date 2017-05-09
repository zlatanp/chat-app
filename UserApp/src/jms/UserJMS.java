package jms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controller.FilePaths;
import controller.UserControllerImpl;
import exceptions.UsernameExistsException;
import model.User;

@MessageDriven(
		activationConfig = {
		        @ActivationConfigProperty(propertyName  = "destinationType", 
		                                  propertyValue = "javax.jms.Queue"),
		        @ActivationConfigProperty(propertyName  = "destination",
		                                  propertyValue = "java:/jms/queue/userQueue")
		})
public class UserJMS implements MessageListener{

	@Context
	ServletConfig config;
	
	public UserJMS() { }
	
	@Override
	public void onMessage(Message msg) {
		// TODO Auto-generated method stub
		TextMessage tmsg = (TextMessage) msg;
		String text;
		try {
			text = tmsg.getText();
			System.out.println(text);
			String[] data = text.split("=");
			String username = data[0];
			String password = data[1];
			
			try {
				System.out.println(username + " " + password);
				URL url = new URL("http://localhost:8080/UserApp/rest/userController/register/" + username + "/" + password);
				
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Accept", "application/json");

					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

					String output;
					System.out.println("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						System.out.println(output);
					}
					conn.disconnect();
			  } catch (MalformedURLException e) {
				e.printStackTrace();
			  } catch (IOException e) {
				e.printStackTrace();
			 }
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	

}
