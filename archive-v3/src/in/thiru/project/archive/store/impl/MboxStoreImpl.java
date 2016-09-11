package in.thiru.project.archive.store.impl;

import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.StoreException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;

public class MboxStoreImpl extends AbstractStore {

    Logger log = Logger.getLogger(MboxStoreImpl.class.getName());

    private Session session;
    private javax.mail.Store store;
    private Folder inbox;
    private java.lang.String fileName;

    public MboxStoreImpl() {

        // mstor expects from system property instead of Session properties
        System.setProperty("mstor.cache.disabled", "true");
    }

    public MboxStoreImpl(String fileName) {
        this();
        this.fileName = fileName;
    }

    @Override
    public void close() {
        try {
            this.inbox.close(false);
            this.store.close();
        } catch (MessagingException e) {
            log.logp(Level.SEVERE, "", "", "", e);
        }

    }

    @Override
    public void open() {
        if (this.session == null) {
            Properties props = new Properties();
            props.setProperty("mstor.mbox.bufferStrategy", "direct");
            props.setProperty("mstor.mbox.cacheBuffers", "false");
            props.setProperty("mstor.mbox.metadataStrategy", "none");
            props.setProperty("mstor.mbox.mozillaCompatibility", "false");
            props.setProperty("mstor.mbox.parsing.relaxed", "true");
            props.setProperty("mstor.cache.disabled", "true");
            this.session = Session.getDefaultInstance(props, null);
        }
        if (this.fileName == null) {
            throw new StoreException("MBox File name is not provided");
        }
        try {
            String mboxFileName = "mstor:" + this.fileName + ".mbox";
            log.info("Mbox File Name " + mboxFileName);
            this.store = this.session.getStore(new URLName(mboxFileName));
            this.store.connect();

            this.inbox = this.store.getDefaultFolder();
            if (!this.inbox.exists()) {
                log.info("Create Inbox Folder");
                this.inbox.create(Folder.HOLDS_MESSAGES);
            } else {
                this.inbox.open(Folder.READ_WRITE);
            }
        } catch (Exception e) {
            throw new StoreException("Couldn't get Store ", e);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void addMessage(String messageKey, String message)
            throws StoreException {
        try {
            InputStream stream =
                    new ByteArrayInputStream(message.getBytes("UTF-8"));
            MimeMessage mimeMessage = new MimeMessage(session, stream);

            SimpleDateFormat dateFormat = new MailDateFormat();
            mimeMessage.setHeader(
                    "X-Archived-On",
                    dateFormat.format(GregorianCalendar.getInstance().getTime()));
            mimeMessage.setHeader("X-Archived-By",
                    "Yahoo Group Archiver http://thiru.in");
            mimeMessage.setHeader("X-Archived-No", messageKey);

            log.info("Adding Message " + messageKey + " to Mbox");

            this.inbox.appendMessages(new Message[] {mimeMessage});
        } catch (Exception e) {
            throw new StoreException("Not able to Add Message to MBox", e);
        }
    }

    @Override
    public String getLastMessageKey() {
        String lastMessageKey = null;

        try {
            if (this.inbox.getMessageCount() > 0) {
                Message message =
                        this.inbox.getMessage(this.inbox.getMessageCount());
                String messageNoString[] = message.getHeader("X-Archived-No");
                if (messageNoString != null) {
                    lastMessageKey = messageNoString[0];
                }
            }
        } catch (NumberFormatException e) {
            throw new StoreException("Invalid Message No found ", e);
        } catch (MessagingException e) {
            throw new StoreException(
                    "Not able to Get Last Message from MBox Archive", e);
        }
        return lastMessageKey;
    }
}
