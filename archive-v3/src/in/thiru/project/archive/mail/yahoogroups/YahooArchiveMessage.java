package in.thiru.project.archive.mail.yahoogroups;

import in.thiru.project.archive.mail.ArchiveMessage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

public class YahooArchiveMessage extends ArchiveMessage {

    private static final String X_MAILER_YAHOO_GROUPS =
            "Yahoo Groups Message Poster";
    private static final String USER_AGENT_EGROUPS = "eGroups-EW/0.82";

    public YahooArchiveMessage(Folder folder, InputStream is, int msgnum)
            throws MessagingException {
        super(folder, is, msgnum);
    }

    public YahooArchiveMessage(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    public YahooArchiveMessage(Folder folder, InternetHeaders headers,
            byte[] content, int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }

    public YahooArchiveMessage(MimeMessage message) throws MessagingException {
        super(message);
    }

    public YahooArchiveMessage(Session session, InputStream is)
            throws MessagingException {
        super(session, is);
    }

    public YahooArchiveMessage(Session session) {
        super(session);
    }

    @Override
    public String getSubject() throws MessagingException {
        String mailer = getFirstHeader("X-Mailer");
        String agent = getFirstHeader("User-Agent");
        String subject = super.getSubject();

        if (X_MAILER_YAHOO_GROUPS.equals(mailer)
            && USER_AGENT_EGROUPS.equals(agent)) {
            try {
                subject = new String(subject.getBytes("iso-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return subject;
    }

}
