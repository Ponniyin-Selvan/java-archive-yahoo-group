package in.thiru.project.archive.test.misc;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.impl.db.DbStoreImpl;
import in.thiru.project.archive.test.StoreTestCase;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

public abstract class TestFindMessage extends StoreTestCase {

    DbStoreImpl fromStore = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fromStore =
                (DbStoreImpl)Class.forName(
                        "in.thiru.project.archive.store.impl.db.MysqlDbStoreImpl")
                        .newInstance();
        fromStore.setJdbcString("jdbc:mysql://localhost/test?user=root&password=&useCompression=true");
        fromStore.setTablePrefix("psvp_");
        fromStore.open();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        fromStore.close();
    }

    public void testFindMessageByHtmlContent() throws MessagingException,
            IOException {
        for (MessageDetails message : fromStore) {
            System.out.println("Finding message " + message.getMessageKey());
            Message mimeMessage =
                    super.toMimeMessage(message.getMessageSource());
            if (testfoundMessage(mimeMessage) == false) {
                break;
            }
        }
    }

    public abstract boolean testfoundMessage(Message message);
}
