package in.thiru.project.archive.store.impl.db;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.StoreException;
import in.thiru.project.archive.store.iterator.impl.DbStoreIteratorImpl;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DbStoreImpl extends AbstractStore {

    Logger log = Logger.getLogger(DbStoreImpl.class.getName());

    private String jdbcString;
    private String jdbcDriver;
    private Connection conn;
    private String tablePrefix;
    ResultSet rs = null;
    private int maxRows = -1;
    private String fromMessageKey;

    public DbStoreImpl() {
    }

    public DbStoreImpl(String jdbcDriver, String jdbcString, String tablePrefix) {
        this.jdbcString = jdbcString;
        this.tablePrefix = tablePrefix;
        this.jdbcDriver = jdbcDriver;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public String getJdbcString() {
        return jdbcString;
    }

    public void setJdbcString(String jdbcString) {
        this.jdbcString = jdbcString;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getFromMessageKey() {
        return fromMessageKey;
    }

    public void setFromMessageKey(String fromMessageKey) {
        this.fromMessageKey = fromMessageKey;
    }

    @Override
    public void close() throws StoreException {
        try {
            if (this.rs != null) {
                this.rs.close();
            }
            if (this.conn != null & this.conn.isClosed() == false) {
                this.conn.close();
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "", "", "Couldn't close Connection", e);
        }
    }

    public abstract void initalize();

    @Override
    public void open() throws StoreException {
        try {
            log.info("Using JDBC Driver " + this.jdbcDriver);
            Class.forName(this.jdbcDriver);
            log.info("Using JDBC String " + this.jdbcString);
            this.conn = DriverManager.getConnection(this.jdbcString);
            initalize();
        } catch (ClassNotFoundException e) {
            throw new StoreException("Couldn't register JDBC Driver", e);
        } catch (SQLException e) {
            throw new StoreException("Couldn't Open JDBC Connection", e);
        }
    }

    @Override
    public void addMessage(String messageKey, String message)
            throws StoreException {

        StringBuffer insertMessageSql = new StringBuffer("INSERT INTO ");
        insertMessageSql.append(this.tablePrefix)
                .append("messages (message_id");
        insertMessageSql.append(", message_source) VALUES(?, ?)");
        log.info("Inserting Message  " + messageKey);
        PreparedStatement pstStatement;
        try {
            pstStatement = conn.prepareStatement(insertMessageSql.toString());
            pstStatement.setLong(1, Long.parseLong(messageKey));
            pstStatement.setClob(2, new StringReader(message));
            pstStatement.execute();
        } catch (SQLException e) {
            throw new StoreException("Couldn't INSERT message", e);
        }
    }

    @Override
    public String getLastMessageKey() {
        String lastMessageKey = null;

        StringBuffer maxMessageSql =
                new StringBuffer("SELECT max(message_id) message_id FROM ");
        maxMessageSql.append(this.tablePrefix).append("messages");
        log.info("Getting max message " + maxMessageSql);
        PreparedStatement pstStatement;
        try {
            pstStatement = conn.prepareStatement(maxMessageSql.toString());
            ResultSet resultSet = pstStatement.executeQuery();
            if (resultSet.next()) {
                lastMessageKey = resultSet.getString("message_id");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "", "", "Couldn't Get Last Message", e);
        }
        return lastMessageKey;
    }

    @Override
    public Iterator<MessageDetails> iterator() {
        return new DbStoreIteratorImpl(this.conn, getSelectSql(), this.maxRows,
                this.fromMessageKey);
    }

    public String getSelectSql() {
        return "SELECT * FROM " + this.tablePrefix + "messages";
    }
}
