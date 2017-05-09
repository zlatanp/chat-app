package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import exceptions.AliasExistsException;
import jms.touser.MessageToUser;
import jms.touser.MessageToUserImpl;
import model.Host;
import model.Message;
import model.User;

@Startup
@Singleton
@Path("/userChatController")
public class UserChatControllerImpl implements UserChatController {

	@Context
	private UriInfo uriInfo;

	private String MyAdress;
	private Host masterHost;
	private ArrayList<Host> MasterCvorovi = new ArrayList<Host>();
	//private ArrayList<Host> mojiCvorovi = new ArrayList<Host>();
	private ArrayList<User> allUsers = new ArrayList<User>();
	private boolean masterAdded = false;
	private boolean slaveAdded = false;
	private String alias = randomIdentifier();

	public UserChatControllerImpl() {
	}

	@Override
	@GET
	@Path("/init")
	public String initialise() {
		masterHost = new Host();
		masterHost.setAdress("http://localhost:8080/ChatApp/rest/");
		masterHost.setAlias("MasterChat");

		MyAdress = uriInfo.getBaseUri().toString();
		String[] pom = MyAdress.split(":");
		String[] pom2 = pom[2].split("/");
		String myHost = pom2[0];
		
		for(int j=0;j<MasterCvorovi.size();j++){
			if(MasterCvorovi.get(j).getAlias().equals(alias))
				slaveAdded = true;
		}

		if (!(MyAdress.equals("http://localhost:8080/ChatApp/rest/")) && !slaveAdded) {
			System.out.println("Call Master for registration");
			String url = "http://localhost:8080/ChatApp/rest/userChatController/registerNewChat/" + myHost + "/" + alias;

			try {

				URL url2 = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
					MasterCvorovi= (ArrayList<Host>) fromJson(output,new TypeToken<ArrayList<Host>>() {}.getType());
					
				}

				conn.disconnect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			//2. ako sam slave gadjam 8080/chatapp koji gadja userapp i dobijam sve
			
			try {

				URL url2 = new URL("http://localhost:8080/ChatApp/rest/userChatController/getAllUsers");
				HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
					allUsers= (ArrayList<User>) fromJsonUsers(output,new TypeToken<ArrayList<User>>() {}.getType());
					
				}

				conn.disconnect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			
			System.out.println(MasterCvorovi.size());
		} else {
			if(!masterAdded && !slaveAdded){
				System.out.println("I am master");
				MasterCvorovi.add(masterHost);
				masterAdded = true;
				
				//1. ako sam master gadjam 8080/userapp/getall i stavljam u listu
				allUsers = getAllUsers();
				System.out.println(allUsers.size());
			}
		}
		
		
		
		
		
		

		return MyAdress;
	}

	//Zatim šalje dodatni zahtev getAllUsers koji obrađuje User app master čvora i beleži listu korisnika kod sebe.
	@Override
	@GET
	@Path("/getAllUsers")
	public ArrayList<User> getAllUsers(){
		ArrayList<User> allUsers = new ArrayList<User>();
		
		try {

			URL url = new URL("http://localhost:8080/UserApp/rest/userController/allUsers");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				allUsers= (ArrayList<User>) fromJsonUsers(output,new TypeToken<ArrayList<User>>() {}.getType());
				
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		return allUsers;
	}

	@Override
	@GET
	@Path("/registerNewChat/{adress}/{alias}")
	public String register(@PathParam("adress") String address, @PathParam("alias") String alias)
			throws AliasExistsException {
		Host newOne = new Host();
		String realAdress = "http://localhost:" + address + "/ChatApp/rest/";
		newOne.setAdress(realAdress);
		newOne.setAlias(alias);
		MasterCvorovi.add(newOne);
		System.err.println(MyAdress + "dodao sam u listu hostova, a velicina je:  " + MasterCvorovi.size());
		
		//Treba da prodje kroz masterCvorove i da svima do poslednjeg posalje novu update-ovanu listu masterCvorova
		for(int i=1;i<MasterCvorovi.size()-1;i++) //Nulti je master cvor, a poslednjem listu vraca return, znaci svima izmedju treba update liste
			updateAllNodes(MasterCvorovi.get(i).getAdress());
		
		return  new Gson().toJson(MasterCvorovi);
	}
	
	
	private void updateAllNodes(String adress){
		try {

			URL url = new URL(adress + "userChatController/updateCvorove");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = new Gson().toJson(MasterCvorovi);

			System.out.println("INPUT JE: " + input);
			
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

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
	}
	
	@Override
	@POST
	@Path("/updateCvorove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateCvorove(ArrayList<Host> u){
		MasterCvorovi = u;
	}

	@Override
	@GET
	@Path("/poz")
	public String unregister() {
		System.out.println("SVI MOJI CVOROVI: ");
		for (int i = 0; i < MasterCvorovi.size(); i++)
			System.out.println(MasterCvorovi.get(i).getAlias());
		System.out.println(" ");
		System.out.println("SVI MOJI USERI: ");
		for (int i = 0; i < allUsers.size(); i++)
			System.out.println(allUsers.get(i).getUsername());
		
		return "cao";

	}
	
	//2. tacka
	@Override
	@POST
	@Path("/registerUser")
	public String registerUser(@FormParam("username") String username, @FormParam("password") String password, @FormParam("passwordRepeat") String passwordRepeat){
		if(username.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()){
			return "null";
		}else if(!password.equals(passwordRepeat)){
			return "null";
		}else{
			if(!MyAdress.contains("8080")){ //ako nisu na istom cvoru
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
			return "REST Done";
			}else{
				//jms queue jer su na istom portu...
				
				
				MessageToUserImpl m = new MessageToUserImpl();
				System.out.println("saljem");
				m.registerMessage(username, password);
				
				return "JMS Done";
			}
		}
	}

	@Override
	@POST
	@Path("/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publish(Message message) {
		// TODO Auto-generated method stub

	}
	
	//Generate random Alias for non-master chat app
	
	final static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

	final static java.util.Random rand = new java.util.Random();

	// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final static Set<String> identifiers = new HashSet<String>();

	public static String randomIdentifier() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(5)+5;
	        for(int i = 0; i < length; i++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	    }
	    return builder.toString();
	}
	
	private ArrayList<Host> fromJson(String output, Type type) {
		return new Gson().fromJson(output, type);
	}
	
	private ArrayList<User> fromJsonUsers(String output, Type type) {
		return new Gson().fromJson(output, type);
	}


}
