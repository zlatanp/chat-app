package jms.tochat;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.User;

@Stateless
@Local(MessageToChatImpl.class)
public class MessageToChatImpl implements MessageToChat{

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory factory;

	@Resource(mappedName = "java:/jms/queue/chatQueue")
	private Queue chatQueue;

	private Connection connection;
	private QueueSender sender;
	private QueueSession session;
	
	
	
	//Metoda koja se poziva kada je aktiviran (ulogovan) novi user
	@Override
	public void addUser(User user) {
		try {
			initialise();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try{
			 System.out.println("***********************************Saljem poruku");
			 TextMessage msg = session.createTextMessage("add=" + user.getUsername() +"="+ user.getPassword());
	         sender.send(msg);
	         
	         destroy();
	        }
	        catch(JMSException e) { }
		
	}

	//Metoda koja se poziva kada se odjavi user
	@Override
	public void removeUser(User user) {
		try {
			initialise();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try{

			 System.out.println("***********************************Saljem poruku deleteeeeeeeeeee");
			 TextMessage msg = session.createTextMessage("delete=" + user.getUsername() +"="+ user.getPassword());
	         sender.send(msg);
	         
	         destroy();
	        }
	        catch(JMSException e) { }
	}
	
	public void initialise() throws NamingException {
		System.out.println("jebem ti mater");
		try {
			Context context = new InitialContext();
			this.factory = (ConnectionFactory) context
			.lookup("java:/ConnectionFactory");
			this.connection = factory.createConnection();
			connection.start();
			this.session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			chatQueue = (Queue) context
					.lookup("java:/jms/queue/chatQueue");
			
			this.sender = session.createSender(chatQueue);
		} catch (JMSException e) {
			return;
		}

	}

	public void destroy() {
		try {
			connection.stop();
			sender.close();
		} catch (JMSException e) {
		}
	}

}
