package in.thiru.project.archive.store.iterator.impl;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.iterator.AbstractMessageIterator;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * <p>
 * Wraps a <code>ResultSet</code> in an <code>Iterator</code>. This is useful
 * when you want to present a non-database application layer with domain neutral
 * data.
 * </p>
 * 
 * <p>
 * This implementation requires the <code>ResultSet.isLast()</code> method to be
 * implemented.
 * </p>
 */
public class DbStoreIteratorImpl extends AbstractMessageIterator {

    /**
     * The wrapped <code>ResultSet</code>.
     */
    private ResultSet rs = null;

    private final Connection conn;

    private final String sql;

    private String lastMessageKey;

    private int maxRows = -1;
    /**
     * The processor to use when converting a row into an Object[].
     */
    private final RowProcessor convert;

    /**
     * Constructor for ResultSetIterator.
     * 
     * @param rs
     *        Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     */
    public DbStoreIteratorImpl(Connection conn, String sql) {
        this.conn = conn;
        this.sql = sql;
        this.convert = new BasicRowProcessor();

    }

    /**
     * Constructor for ResultSetIterator.
     * 
     * @param rs
     *        Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     */
    public DbStoreIteratorImpl(Connection conn, String sql, int maxRows) {
        this(conn, sql);
        this.maxRows = maxRows;
    }

    /**
     * Constructor for ResultSetIterator.
     * 
     * @param rs
     *        Wrap this <code>ResultSet</code> in an <code>Iterator</code>.
     */
    public DbStoreIteratorImpl(Connection conn, String sql, int maxRows,
            String lastMessageKey) {
        this(conn, sql, maxRows);
        this.lastMessageKey = lastMessageKey;
    }

    /**
     * Returns true if there are more rows in the ResultSet.
     * 
     * @return boolean <code>true</code> if there are more rows
     * @throws RuntimeException
     *         if an SQLException occurs.
     */
    public boolean hasNext() {
        boolean hasNext = false;
        try {
            if (rs == null) {
                String pageSql = sql;
                if (null != this.lastMessageKey) {
                    pageSql =
                            pageSql + " WHERE message_id > "
                                    + this.lastMessageKey;
                    pageSql = pageSql + " ORDER BY message_id";
                }
                Statement stmt = conn.createStatement();
                if (this.maxRows != -1) {
                    pageSql = pageSql + " LIMIT " + this.maxRows;
                    // stmt.setMaxRows(this.maxRows);
                }
                rs = stmt.executeQuery(pageSql);
            }
            hasNext = rs.next();
            if (hasNext == false) {
                String pageSql =
                        sql + " WHERE message_id > " + lastMessageKey
                                + " ORDER BY message_id";
                Statement stmt = conn.createStatement();
                if (this.maxRows != -1) {
                    stmt.setMaxRows(this.maxRows);
                }
                rs = stmt.executeQuery(pageSql);
                hasNext = rs.next();
            }
        } catch (SQLException e) {
            rethrow(e);
            hasNext = false;
        }
        return hasNext;
    }

    /**
     * Returns the next row as an <code>Object[]</code>.
     * 
     * @return An <code>Object[]</code> with the same number of elements as
     *         columns in the <code>ResultSet</code>.
     * @see java.util.Iterator#next()
     * @throws RuntimeException
     *         if an SQLException occurs.
     */
    public MessageDetails next() {
        MessageDetails message = null;
        try {
            Map<String, Object> values = this.convert.toMap(rs);

            String messageKey = values.get("message_id").toString();
            lastMessageKey = messageKey;
            Object messageData = values.get("message_source");
            String messageSource = null;
            if (messageData instanceof Clob) {
                Clob messageClob = (Clob)values.get("message_source");
                Reader reader = messageClob.getCharacterStream();

                char[] content = new char[(int)messageClob.length()];
                reader.read(content, 0, (int)messageClob.length());
                messageSource = new String(content);
            } else if (messageData instanceof String) {
                messageSource = (String)messageData;
            }
            message = new MessageDetails(messageKey, messageSource);
        } catch (SQLException e) {
            rethrow(e);
            message = null;
        } catch (IOException e) {
            e.printStackTrace();
            message = null;
        }
        return message;
    }

    /**
     * Deletes the current row from the <code>ResultSet</code>.
     * 
     * @see java.util.Iterator#remove()
     * @throws RuntimeException
     *         if an SQLException occurs.
     */
    public void remove() {
        try {
            this.rs.deleteRow();
        } catch (SQLException e) {
            rethrow(e);
        }
    }

    /**
     * Rethrow the SQLException as a RuntimeException. This implementation
     * creates a new RuntimeException with the SQLException's error message.
     * 
     * @param e
     *        SQLException to rethrow
     * @since DbUtils 1.1
     */
    protected void rethrow(SQLException e) {
        throw new RuntimeException(e.getMessage());
    }

}