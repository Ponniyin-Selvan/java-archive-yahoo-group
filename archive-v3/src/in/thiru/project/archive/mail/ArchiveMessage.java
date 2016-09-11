package in.thiru.project.archive.mail;

import java.io.InputStream;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

public class ArchiveMessage extends MimeMessage {

    public ArchiveMessage(Folder folder, InputStream is, int msgnum)
            throws MessagingException {
        super(folder, is, msgnum);
    }

    public ArchiveMessage(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    public ArchiveMessage(Folder folder, InternetHeaders headers,
            byte[] content, int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }

    public ArchiveMessage(MimeMessage message) throws MessagingException {
        super(message);
    }

    public ArchiveMessage(Session session, InputStream is)
            throws MessagingException {
        super(session, is);
    }

    public ArchiveMessage(Session session) {
        super(session);
    }

    public String getFirstHeader(String headerName) {

        String[] values = null;

        try {
            values = getHeader(headerName);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        String headerValue = null;

        if (values != null) {
            if (values.length >= 1) {
                headerValue = values[0];
            }
        }
        return headerValue;
    }
}
