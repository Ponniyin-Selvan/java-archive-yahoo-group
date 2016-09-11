package in.thiru.project.archive.test.format;

import in.thiru.project.archive.test.StoreTestCase;
import in.thiru.project.archive.util.MimeTypeUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;

public class MimeTypeTestCase extends StoreTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testFindMimeTypes() throws MessagingException, IOException {
        Message message = getMimeMessage("test/tamil-utf-8.msg");
        String[] mimeTypes = new String[] {"text/plain", "text/html"};
        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, mimeTypes);
        Part plainPart = parts.get("text/html").get(0);
        System.out.println(plainPart.getContent().toString());
        System.out.println(parts);
    }

}
