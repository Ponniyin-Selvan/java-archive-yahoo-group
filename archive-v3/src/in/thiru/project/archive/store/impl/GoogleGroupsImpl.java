package in.thiru.project.archive.store.impl;

import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.StoreException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GoogleGroupsImpl extends AbstractStore {

    private Session session = null;

    @Override
    public void addMessage(String messageKey, String message)
            throws StoreException {
        try {
            InputStream stream =
                    new ByteArrayInputStream(message.getBytes("UTF-8"));
            Message mimeMessage = new MimeMessage(this.session, stream);
            InternetAddress from = (InternetAddress)mimeMessage.getFrom()[0];
            mimeMessage.setFrom(new InternetAddress("me@thiru.in", from.getPersonal()));
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress("archivetool@googlegroups.com"));
            Transport.send(mimeMessage);
        } catch (UnsupportedEncodingException e) {
            throw new StoreException("UnsupportedEncodingException", e);
        } catch (MessagingException e) {
            throw new StoreException("MessagingException", e);
        }
    }

    @Override
    public void close() throws StoreException {
        this.session = null;
    }

    @Override
    public String getLastMessageKey() {

        return null;
    }

    @Override
    public void open() throws StoreException {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "gmr-smtp-in.l.google.com");

        this.session = Session.getInstance(props, null);
    }

}
