package jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/chatQueue") })
public class ChatJMS implements MessageListener{

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		System.out.println("Chat primljeno");
	}

}
