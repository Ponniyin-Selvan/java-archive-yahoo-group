/**
 * 
 */
package in.thiru.project.archive.listner;


/**
 * @author Humble
 * 
 */
public abstract class AbstractArchiveListener implements ArchiveListener {

    /*
     * @see in.thiru.project.archive.ArchiveListener#afterLogin()
     */
    @Override
    public void afterLogin() {
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#afterLogout()
     */
    @Override
    public void afterLogout() {
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#afterReadMessagePage(long,
     * java.lang.String)
     */
    @Override
    public void afterReadMessagePage(String messageKey, String pageContent) {
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#archiveMessage(long,
     * java.lang.String)
     * 
     * @Override public boolean archiveMessage(String messageKey, String
     * message) { InputStream stream; Session session = Session.getInstance(new
     * Properties()); boolean continueArchive = true;
     * 
     * try { stream = new
     * ByteArrayInputStream(message.trim().getBytes("UTF-8")); MimeMessage
     * mimeMessage = new MimeMessage(session, stream); continueArchive =
     * archiveMessage(messageKey, mimeMessage); } catch
     * (UnsupportedEncodingException e) { e.printStackTrace(); } catch
     * (MessagingException e) { e.printStackTrace(); } return continueArchive; }
     * 
     * public boolean archiveMessage(String messageKey, Message message) throws
     * StoreException { DateFormat dateFormat = new
     * SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US); try {
     * message.setHeader("X-Archive-Message-Key", messageKey);
     * message.setHeader("X-Archived-By", "http://thiru.in/archive");
     * message.setHeader("X-Archive-On", dateFormat.format(new Timestamp(
     * System.currentTimeMillis()))); } catch (MessagingException e) { throw new
     * StoreException("Couldn't modify headers", e); } return true; }
     * 
     * /*
     * 
     * @see in.thiru.project.archive.ArchiveListener#bandwidthExceeded()
     */
    @Override
    public boolean bandwidthExceeded() {
        return true;
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#beforeLogin()
     */
    @Override
    public void beforeLogin() {
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#beforeLogout()
     */
    @Override
    public void beforeLogout() {
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#extractMessage(long,
     * java.lang.String)
     */
    @Override
    public String extractMessage(String messageKey, String pageContent) {
        return null;
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#login()
     */
    @Override
    public boolean login() {
        return true;
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#logout()
     */
    @Override
    public boolean logout() {
        return true;
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#readMessagePage(long)
     */
    @Override
    public String readMessagePage(String messageKey) {
        return null;
    }

    /*
     * @see in.thiru.project.archive.ArchiveListener#retry(long)
     */
    @Override
    public boolean retry(String messageKey) {
        return true;
    }
}
