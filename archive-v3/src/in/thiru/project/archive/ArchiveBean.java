package in.thiru.project.archive;

import in.thiru.project.archive.store.Store;

public class ArchiveBean {

    private String userId;

    private String password;

    private String groupName;

    private String startMessageKey;

    private long noOfMessagesToArchive = -1;

    private int sleepBetweenMessages = 12000;

    private String domain = "groups.yahoo.com";

    private String proxyHost;
    
    private int proxyPort;

    private Store store;
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStartMessageKey() {
        return startMessageKey;
    }

    public void setStartMessageKey(String startMessageKey) {
        this.startMessageKey = startMessageKey;
    }

    public long getNoOfMessagesToArchive() {
        return noOfMessagesToArchive;
    }

    public void setNoOfMessagesToArchive(long noOfMessagesToArchive) {
        this.noOfMessagesToArchive = noOfMessagesToArchive;
    }

    public int getSleepBetweenMessages() {
        return sleepBetweenMessages;
    }

    public void setSleepBetweenMessages(int sleepBetweenMessages) {
        this.sleepBetweenMessages = sleepBetweenMessages;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String yahooGroupDomain) {
        this.domain = yahooGroupDomain;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int port) {
        this.proxyPort = port;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
