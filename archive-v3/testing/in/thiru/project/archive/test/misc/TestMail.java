package in.thiru.project.archive.test.misc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMail {

    public static void main(String[] argv) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "gmr-smtp-in.l.google.com");

        Session session = Session.getDefaultInstance(props, null);
        // session.addProvider(new Provider(Provider.Type.TRANSPORT, "smtp",
        // "com.sun.mail.smtp.SMTPTransport", "Sun Microsystems, Inc.", "1"));
        // Transport transport = session.getTransport("smtp");
        FileInputStream stream = new FileInputStream(new File(argv[0]));
        MimeMessage message = new MimeMessage(session, stream);
        message.setFrom(new InternetAddress("me@thiru.in", "SPS"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(
                "archivetool@googlegroups.com"));

        // transport.connect();

        Transport.send(message);
        // transport.close();

        // String mailer = getHeader(message, "X-Mailer");
        // String agent = getHeader(message, "User-Agent");
        // String subject = message.getSubject();
        //
        // if (X_MAILER_YAHOO_GROUPS.equals(mailer) &&
        // USER_AGENT_EGROUPS.equals(agent)) {
        // subject = new String(subject.getBytes("iso-8859-1"), "UTF-8");
        // }
        // System.out.println(subject);
    }
}
