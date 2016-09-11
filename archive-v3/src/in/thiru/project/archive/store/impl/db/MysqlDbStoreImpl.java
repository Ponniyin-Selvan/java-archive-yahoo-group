package in.thiru.project.archive.store.impl.db;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.StoreException;
import in.thiru.project.archive.store.iterator.impl.DbStoreIteratorImpl;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class MysqlDbStoreImpl extends DbStoreImpl {

    public MysqlDbStoreImpl() {
        super();
        setJdbcDriver("com.mysql.jdbc.Driver");
    }

    Logger log = Logger.getLogger(MysqlDbStoreImpl.class.getName());

    public MysqlDbStoreImpl(String jdbcString, String tablePrefix) {
        super("com.mysql.jdbc.Driver", jdbcString, tablePrefix);
    }

    public void initalize() {
        StringBuffer createTableSql = new StringBuffer("CREATE TABLE ");
        createTableSql.append(this.getTablePrefix()).append(
                "messages (message_id");
        createTableSql.append(" NUMERIC(7) PRIMARY KEY, message_source MEDIUMBLOB)");
        Statement stmt;
        try {
            stmt = this.getConn().createStatement();
            stmt.execute(createTableSql.toString());
        } catch (SQLException e) {
            if (e instanceof MySQLSyntaxErrorException
                && e.getMessage().indexOf("already exists") > 0) {
                // ignore
            } else {   
                log.logp(Level.SEVERE, "", "", "Couldn't create table", e);
            }
        }
    }

    @Override
    public void addMessage(String messageKey, String message)
            throws StoreException {

        StringBuffer insertMessageSql = new StringBuffer("INSERT INTO ");
        insertMessageSql.append(getTablePrefix())
                .append("messages (message_id");
        insertMessageSql.append(", message_source) VALUES(?, COMPRESS(?))");
        log.info("Inserting Message  " + messageKey);
        PreparedStatement pstStatement = null;
        try {
            pstStatement =
                    getConn().prepareStatement(insertMessageSql.toString());
            pstStatement.setLong(1, Long.parseLong(messageKey));
            pstStatement.setClob(2, new StringReader(message));
            pstStatement.execute();
        } catch (SQLException e) {
            throw new StoreException("Couldn't INSERT message", e);
        } finally {
            try {
                if (pstStatement != null) {
                    pstStatement.close();
                    pstStatement = null;
                }
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public Iterator<MessageDetails> iterator() {
        String sql =
                "SELECT message_id, UNCOMPRESS(message_source) message_source FROM "
                        + getTablePrefix() + "messages";
        Statement stmt = null;

        try {
            stmt = this.getConn().createStatement();
            //rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "", "", "Couldn't select table", e);
        }
        return new DbStoreIteratorImpl(getConn(), sql, getMaxRows(), getFromMessageKey());
    }
}
