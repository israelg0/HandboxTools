package es.handbox.tools.pojo;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class EmailSenderService {
	private final Properties properties = new Properties();
	
	private String password;
 
	private Session session;
 
	public EmailSenderService() {
 
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port",587);
		properties.put("mail.smtp.mail.sender","israel@lanoa.com");
		properties.put("mail.smtp.user", "israel@lanoa.com");
		properties.put("mail.smtp.auth", "true");
 
		session = Session.getDefaultInstance(properties);
	}
 
	public void sendEmail(String asunto, String texto){
 
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress((String)properties.get("mail.smtp.mail.sender")));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("israel@handbox.es"));
			message.setSubject(asunto);
			message.setText(texto);
			Transport t = session.getTransport("smtp");
			t.connect((String)properties.get("mail.smtp.user"), "re3ta1100");
			t.sendMessage(message, message.getAllRecipients());
			t.close();
		}catch (MessagingException me){
                        System.out.println(me);
			return;
		}
		
	}
 
 
    public static void main(String[] args) {
        EmailSenderService test = new EmailSenderService();
        test.sendEmail("prueba de envío", "texto a enviar");
    }
}
