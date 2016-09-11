package in.thiru.project.archive.store.impl.db;

import java.util.logging.Logger;

public class DerbyEmbeddedDbStoreImpl extends DerbyDbStoreImpl {

    Logger log = Logger.getLogger(DerbyEmbeddedDbStoreImpl.class.getName());

    public DerbyEmbeddedDbStoreImpl() {
        super();
        setJdbcDriver("org.apache.derby.jdbc.EmbeddedDriver");
    }

    public DerbyEmbeddedDbStoreImpl(String jdbcString, String tablePrefix) {
        super("org.apache.derby.jdbc.EmbeddedDriver", jdbcString, tablePrefix);
    }
}
