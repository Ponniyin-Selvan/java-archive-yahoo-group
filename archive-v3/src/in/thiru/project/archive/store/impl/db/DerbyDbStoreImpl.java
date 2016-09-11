package in.thiru.project.archive.store.impl.db;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DerbyDbStoreImpl extends DbStoreImpl {

    Logger log = Logger.getLogger(DerbyDbStoreImpl.class.getName());
    
    public DerbyDbStoreImpl() {
        super();
    }

    public DerbyDbStoreImpl(String jdbcDriver, String jdbcString,
            String tablePrefix) {
        super(jdbcDriver, jdbcString, tablePrefix);
    }

    public void initalize() {
        StringBuffer createTableSql = new StringBuffer("CREATE TABLE ");
        createTableSql.append(this.getTablePrefix()).append(
                "messages (message_id");
        createTableSql.append(" NUMERIC(7) PRIMARY KEY, message_source CLOB(1024 K))");
        Statement stmt;
        try {
            stmt = this.getConn().createStatement();
            stmt.execute(createTableSql.toString());
        } catch (SQLException e) {
            if (e.getMessage().indexOf("already exists in Schema") == 0) {
                log.logp(Level.SEVERE, "", "", "Couldn't create table", e);
            }
        }
    }

}
