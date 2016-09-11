/**
 * 
 */
package in.thiru.project.archive.listner.impl;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.listner.AbstractArchiveListener;
import in.thiru.project.archive.store.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Humble
 * 
 */
public class YahooGroupArchiveListener extends AbstractArchiveListener {

    Logger log = Logger.getLogger(YahooGroupArchiveListener.class.getName());

    private int sleepTime;
    private List<Store> stores = null;

    public YahooGroupArchiveListener(String groupName, int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void addStore(Store store) {
        if (stores == null) {
            stores = new ArrayList<Store>();
        }
        stores.add(store);
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#afterLogin()
     */
    @Override
    public void afterLogin() {

        super.afterLogin();
        log.info("Logged In");

    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#afterLogout()
     */
    @Override
    public void afterLogout() {

        super.afterLogout();
        log.info("Logged Out");
    }

    /*
     * @see
     * in.thiru.project.archive.AbstractArchiveListener#afterReadMessagePage
     * (long, java.lang.String)
     */
    @Override
    public void afterReadMessagePage(String messageKey, String pageContent) {

        super.afterReadMessagePage(messageKey, pageContent);
        log.info("Read Message Page " + messageKey);
    }

    @Override
    public boolean archiveMessage(String messageKey, String message) {
        log.info("Archive Message " + messageKey);
        // FIXME - x-user-defined fix
        message  = message.replaceAll("\"x-user-defined\"", "\"uf-8\"");
        try {
            for (Store store : stores) {
                MessageDetails messageDetails =
                        new MessageDetails(messageKey, message);
                store.addMessage(messageDetails);
            }
            Thread.sleep(this.sleepTime);
        } catch (InterruptedException e) {
            log.logp(Level.SEVERE, "", "", "", e);
        } catch (Exception e) {
            log.logp(Level.SEVERE, "", "", "", e);
        }
        return true;
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#bandwidthExceeded()
     */
    @Override
    public boolean bandwidthExceeded() {

        log.log(Level.SEVERE, "Bandwidth Exceede");
        return super.bandwidthExceeded();
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#beforeLogin()
     */
    @Override
    public void beforeLogin() {

        log.info("Logging in...");
        super.beforeLogin();
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#beforeLogout()
     */
    @Override
    public void beforeLogout() {

        log.info("Logging out...");
        super.beforeLogout();
    }

    /*
     * @see
     * in.thiru.project.archive.AbstractArchiveListener#extractMessage(long,
     * java.lang.String)
     */
    @Override
    public String extractMessage(String messageKey, String pageContent) {

        log.info("Extract Message " + messageKey);
        return super.extractMessage(messageKey, pageContent);
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#login()
     */
    @Override
    public boolean login() {

        return super.login();
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#logout()
     */
    @Override
    public boolean logout() {

        return super.logout();
    }

    /*
     * @see
     * in.thiru.project.archive.AbstractArchiveListener#readMessagePage(long)
     */
    @Override
    public String readMessagePage(String messageKey) {

        log.info("Downloading Message Page " + messageKey);
        return super.readMessagePage(messageKey);
    }

    /*
     * @see in.thiru.project.archive.AbstractArchiveListener#retry(long)
     */
    @Override
    public boolean retry(String messageKey) {

        return super.retry(messageKey);
    }

}
