package in.thiru.project.archive.test.store.yahoo;

import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.mail.yahoogroups.YahooArchiveMessage;
import in.thiru.project.archive.test.StoreTestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

public class YahooTestCase extends StoreTestCase {

    protected ArchiveMessage toMimeMessage(String message)
            throws UnsupportedEncodingException, MessagingException {
        Session session = Session.getInstance(new Properties());
        InputStream stream =
                new ByteArrayInputStream(message.getBytes("UTF-8"));
        return new YahooArchiveMessage(session, stream);
    }

}
