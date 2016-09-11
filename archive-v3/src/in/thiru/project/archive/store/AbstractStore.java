package in.thiru.project.archive.store;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.mail.ArchiveMessage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

public abstract class AbstractStore implements Store {

    @Override
    public void addMessage(MessageDetails message) throws StoreException {
        InputStream stream;
        Session session = Session.getInstance(new Properties());

        try {
            stream =
                    new ByteArrayInputStream(message.getMessageSource()
                            .trim()
                            .getBytes("UTF-8"));
            ArchiveMessage mimeMessage = new ArchiveMessage(session, stream);
            modifyMessage(message, mimeMessage);
            addMessage(message.getMessageKey(), mimeMessage);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // FIXME - Override - Decouple
    private void modifyMessage(MessageDetails message,
            ArchiveMessage mimeMessage) {
        DateFormat dateFormat =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        try {
            mimeMessage.setHeader("X-Archive-Message-Key",
                    message.getMessageKey());
            mimeMessage.setHeader("X-Archived-By", "http://thiru.in/archive");
            mimeMessage.setHeader(
                    "X-Archive-On",
                    dateFormat.format(new Timestamp(System.currentTimeMillis())));
        } catch (MessagingException e) {
            throw new StoreException("Couldn't modify headers", e);
        }
    }

    public void addMessage(String messageKey, ArchiveMessage message)
            throws StoreException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStream out = new BufferedOutputStream(byteStream);
        try {
            message.writeTo(out);
        } catch (Exception e) {
            throw new StoreException("Could create String of MimeMessage", e);
        }
        addMessage(messageKey, new String(byteStream.toByteArray()));
    }

    // FIXME - Not elegant
    public abstract void addMessage(String messageKey, String message);

    @Override
    public Iterator<MessageDetails> iterator() {
        return null;
    }
}
