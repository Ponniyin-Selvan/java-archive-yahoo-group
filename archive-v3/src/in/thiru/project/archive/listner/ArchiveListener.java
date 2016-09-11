package in.thiru.project.archive.listner;


public interface ArchiveListener {

    void beforeLogin();

    boolean login();

    void afterLogin();

    String readMessagePage(String messageKey);

    void afterReadMessagePage(String messageKey, String pageContent);

    String extractMessage(String messageKey, String pageContent);

    boolean archiveMessage(String messageKey, String message);

    void beforeLogout();

    boolean logout();

    void afterLogout();

    boolean bandwidthExceeded();

    boolean retry(String messageNo);
}
