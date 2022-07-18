/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class EmailText implements Runnable{

    private static final String SMTP_HOST_NAME = ServerEnvironmentVariables.EMAIL_SMTP_HOST_NAME;
    private static final int SMTP_HOST_PORT = ServerEnvironmentVariables.EMAIL_SMTP_HOST_PORT;
    private static final String SMTP_AUTH_USER = ServerEnvironmentVariables.EMAIL_SMTP_AUTH_USER;
    private static final String SMTP_AUTH_PASSWORD = ServerEnvironmentVariables.EMAIL_SMTP_AUTH_PASSWORD;
    
    private final JSONObject emailData;
            
    protected EmailText(JSONObject data){
        this.emailData = data;
    }
    
    @Override
    public void run() {
        try{
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "outlook.office365.com");
            props.put("mail.smtp.port", SMTP_HOST_PORT + "");
            
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(false);
            try (Transport transport = mailSession.getTransport()){
                MimeMessage message = new MimeMessage(mailSession);
                message.setSubject((String) emailData.get("subject"));
                message.setContent((String) emailData.get("message"), "text/html");
                Address[] from = InternetAddress.parse(SMTP_AUTH_USER);
                message.addFrom(from);
                message.addRecipient(Message.RecipientType.TO, new InternetAddress((String) emailData.get("email")));
                transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PASSWORD);
                transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            }
        } catch(MessagingException e){
            e.printStackTrace(System.out);
        }
    }
    
}

