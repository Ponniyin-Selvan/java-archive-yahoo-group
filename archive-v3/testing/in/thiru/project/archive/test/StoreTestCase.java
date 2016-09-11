package in.thiru.project.archive.test;

import in.thiru.project.archive.mail.ArchiveMessage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import junit.framework.TestCase;

public abstract class StoreTestCase extends TestCase {

    public StoreTestCase() {
        super();
    }

    public StoreTestCase(String name) {
        super(name);
    }

    protected ArchiveMessage getMimeMessage(String fromFile)
            throws UnsupportedEncodingException, MessagingException {
        String message = getMessage(fromFile);
        return toMimeMessage(message);
    }

    protected ArchiveMessage toMimeMessage(String message)
            throws UnsupportedEncodingException, MessagingException {
        Session session = Session.getInstance(new Properties());
        InputStream stream =
                new ByteArrayInputStream(message.getBytes("UTF-8"));
        return new ArchiveMessage(session, stream);
    }

    protected String getMessage(String fromFile) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(
                            new FileInputStream(fromFile), "UTF-8"));
            while (true) {
                String string = reader.readLine();
                if (string != null)
                    stringBuffer.append(string).append("\n");
                else
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

}