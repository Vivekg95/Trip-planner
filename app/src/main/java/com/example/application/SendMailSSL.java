package com.example.application;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailSSL {

    public static void sendEmailWithAttachments(String host, String port,
                                                final String userName, final String password, String toAddress,
                                                String subject, String message, String[] attachFiles)
            throws AddressException, MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();

                try {
                    attachPart.attachFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                multipart.addBodyPart(attachPart);
            }
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
        try {
            // sends the e-mail
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

/*******  SSL:- (Secure Sockets Layer) ********/
/*public class SendMailSSL {
    
    Session session;
    String To;
   
public int EmailSending(String To, String Sub, String Msg, File fl) {
      System.out.println("----------To-----------"+To);
        int flag;
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("mynewjava@gmail.com","javarocks");
                        }
                    });
        
            String From = "mynewjava@gmail.com";
                
    
            Message message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(From));
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(To));
            // Set Subject
            message.setSubject(Sub);
            
            // Now set the actual message
            *//* message.setText(Msg); *//*
            
            // For set msg As HTML coding "Use Either .setText Or This Method to Send Msg"

            MimeBodyPart attach = new MimeBodyPart();

            try{
                attach.attachFile(fl);
            }catch(Exception e){}

            MimeBodyPart meesagepart = new MimeBodyPart();
            meesagepart.setContent(Msg,"text/html");

            Multipart mpart = new MimeMultipart();
            mpart.addBodyPart(meesagepart);

            mpart.addBodyPart(attach);

            message.setContent(mpart);

            Transport.send(message);
            System.out.println("Sent message successfully....");
            
            flag = 1;
        } catch (MessagingException ex) {
            System.out.println("Exception "+ex);
            return -1;
            
        }
      return flag;
    }//SEND USER MAIL END
   
    public static void main(String[] args) {
        
       
            SendMailSSL s = new SendMailSSL();
        s.EmailSending("tejas.srccode@gmail.com", "Test", "Test", null);
       //kumawat.swapnil@gmail.com
    }
}//CLOSE MAIN CLASS*/





/**
     * Test sending e-mail with attachments
     *//*

    public static void main(String[] args) {
        // SMTP info
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "loginsystem2751@gmail.com";
        String password = "Ritesh1432";

        // message info
        String mailTo = "rahul.srccode@gmail.com";
        String subject = "New email with attachments";
        String message = "I have some attachments for you.";

        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "D:\\workspace\\Visual_keypad_new1_changes\\WebContent\\qr\\25.png";


        try {
            sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }
}*/
