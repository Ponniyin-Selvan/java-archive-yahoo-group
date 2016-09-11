package in.thiru.project.archive.test.xmlrpc;

import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.impl.XmlRpcStoreImpl;
import in.thiru.project.archive.test.StoreTestCase;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

public class TestXmlRpcStore extends StoreTestCase {

    private AbstractStore xmlRpcStore;

    protected void setUp() throws Exception {
        super.setUp();
        xmlRpcStore =
                new XmlRpcStoreImpl("http://localhost/xmlrpc.php",
                        "thirumalaikv", "humble1234");
        xmlRpcStore.open();
    }

    public void testAddMessageStringMessage()
            throws UnsupportedEncodingException, MessagingException {
        ArchiveMessage message = getMimeMessage("test-data/test-001.msg");

        xmlRpcStore.addMessage("1", message);
    }
}
