package in.thiru.project.archive.store.impl.db;

import java.util.logging.Logger;

public class DerbyClientDbStoreImpl extends DerbyDbStoreImpl {

    Logger log = Logger.getLogger(DerbyClientDbStoreImpl.class.getName());
    
    public DerbyClientDbStoreImpl() {
        super();
        setJdbcDriver("org.apache.derby.jdbc.ClientDriver");
    }

    public DerbyClientDbStoreImpl(String jdbcString, String tablePrefix) {
        super("org.apache.derby.jdbc.ClientDriver", jdbcString, tablePrefix);
    }
}
