package in.thiru.project.archive.test.store;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.Store;
import in.thiru.project.archive.store.impl.db.DerbyDbStoreImpl;
import in.thiru.project.archive.test.StoreTestCase;

public class TestDbStoreImpl extends StoreTestCase {

    Store store = null;

    protected void setUp() throws Exception {
        super.setUp();
        String groupName = "ponniyinselvan";
        store =
                new DerbyDbStoreImpl("org.apache.derby.jdbc.EmbeddedDriver",
                        "jdbc:derby:" + groupName + ";create=true;user="
                                + groupName + ";password=password;", groupName
                                                                     + "_");
        store.open();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        store.close();
    }

    public void testIterator() {
        for (MessageDetails message : store) {
            System.out.println(message.getMessageKey());
        }
    }
}
