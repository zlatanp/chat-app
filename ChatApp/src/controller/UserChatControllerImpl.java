package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import exceptions.AliasExistsException;
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
	private ArrayList<Host> mojiCvorovi = new ArrayList<Host>();
	private boolean masterAdded = false;

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

		if (!(MyAdress.equals("http://localhost:8080/ChatApp/rest/"))) {
			String alias = randomIdentifier();
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
					mojiCvorovi= (ArrayList<Host>) fromJson(output,new TypeToken<ArrayList<Host>>() {}.getType());
					
				}

				conn.disconnect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			System.out.println(mojiCvorovi.size());
		} else {
			System.out.println("I am master");
			if(!masterAdded){
				MasterCvorovi.add(masterHost);
				masterAdded = true;
			}
		}

		return MyAdress;
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
		return  new Gson().toJson(MasterCvorovi);
	}

	@Override
	@GET
	@Path("/poz")
	public String unregister() {
		System.out.println("SVI MASTER CVOROVI: ");
		for (int i = 0; i < MasterCvorovi.size(); i++)
			System.out.println(MasterCvorovi.get(i).getAlias());
		return "cao";

	}

	@Override
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
	
	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

	final java.util.Random rand = new java.util.Random();

	// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final Set<String> identifiers = new HashSet<String>();

	public String randomIdentifier() {
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

}
