/**
 * 
 */
package in.thiru.project.archive.cmd;

import in.thiru.project.archive.AbstractArchiver;
import in.thiru.project.archive.ArchiveBean;
import in.thiru.project.archive.Archiver;
import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.html.impl.NekoHtmlParserImpl;
import in.thiru.project.archive.http.HttpClient;
import in.thiru.project.archive.http.impl.CommonsHttpClient;
import in.thiru.project.archive.impl.YahooGroupArchiver;
import in.thiru.project.archive.listner.ArchiveListener;
import in.thiru.project.archive.listner.impl.YahooGroupArchiveListener;
import in.thiru.project.archive.store.Store;
import in.thiru.project.archive.store.impl.db.MysqlDbStoreImpl;

import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author Humble
 * 
 */
public class RunArchiver {

    Logger log = Logger.getLogger(RunArchiver.class.getName());
    boolean loggedIn = false;

    private ArchiveBean archiveBean = new ArchiveBean();

    public RunArchiver() {
    }

    public ArchiveBean getArchiveBean() {
        return archiveBean;
    }

    public void setArchiveBean(ArchiveBean archiveBean) {
        this.archiveBean = archiveBean;
    }

    public void archive(Archiver archiver, ArchiveListener listener)
            throws Exception {

        boolean loggedIn = login(archiver, listener);

        archiveMessages(archiver, listener);

        if (this.archiveBean.getUserId() != null && loggedIn) {
            listener.beforeLogout();
            archiver.logout();
            listener.logout();
        }
    }

    private boolean login(Archiver archiver, ArchiveListener listener) {

        if (!loggedIn && this.archiveBean.getUserId() != null) {
            listener.beforeLogin();
            loggedIn =
                    archiver.login(archiveBean.getUserId(),
                            archiveBean.getPassword());
            if (this.archiveBean.getUserId() != null && loggedIn) {
                listener.afterLogin();
            }
        }

        return loggedIn;
    }

    private void archiveMessages(Archiver archiver, ArchiveListener listener)
            throws Exception {
        long totalMessagesArchived = 0;
        String messageKey = this.archiveBean.getStartMessageKey();

        String nextMessageKey = null;
        long noOfMessagesToArchive =
                this.archiveBean.getNoOfMessagesToArchive();

        while ((noOfMessagesToArchive == -1 && null != messageKey)
               || (noOfMessagesToArchive != -1 && totalMessagesArchived < noOfMessagesToArchive)) {

            String page = listener.readMessagePage(messageKey);
            if (page == null) {
                page = archiver.getMessagePage(messageKey);
            }

            if (page != null) {
                listener.afterReadMessagePage(messageKey, page);

                MessageDetails messageDetails =
                        archiver.getMessageDetails(page);
                String messageContent = messageDetails.getMessageSource();
                messageKey = messageDetails.getMessageKey();
                nextMessageKey = null;

                if (messageContent == null) {
                    log.logp(Level.SEVERE, "", "",
                            "Couldn't Extract the message");
                    FileWriter file =
                            new FileWriter("coredump"
                                           + System.currentTimeMillis());
                    file.write(page);
                    file.close();
                    break;
                }

                if (messageContent == null
                    || !listener.archiveMessage(messageKey, messageContent)) {
                    break;
                }
                nextMessageKey = messageDetails.getNextMessageKey();
                totalMessagesArchived++;
                messageKey = nextMessageKey;
                if (nextMessageKey == null) {
                    break;
                }
            } else {
                log.logp(Level.SEVERE, "", "", "Couldn't get Page");
                break;
            }
        }
    }

    @SuppressWarnings("static-access")
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("u", "user", true,
                "user id to login to yahoo group if the group is private");
        options.addOption("p", "password", true,
                "password to login to yahoo group if the group is private");
        options.addOption("g", "group", true, "Yahoo group name");
        options.addOption(OptionBuilder.withArgName("startfrom")
                .withLongOpt("start-from")
                .hasOptionalArg()
                .withDescription("Archive messages from this no, default is 0")
                .withType(Long.class)
                .create('s'));
        options.addOption(
                "n",
                "start-from-next",
                true,
                "Archive messages by locate the number and skip to next message and start archiving");
        options.addOption(OptionBuilder.withArgName("noofmessages")
                .withLongOpt("no-of-messages")
                .hasOptionalArg()
                .withDescription(
                        "No of messages to archive, default is everything.")
                .create('n'));
        options.addOption("S", "sleep", true,
                "Sleep between messages in milliseconds");
        options.addOption("d", "domain", true,
                "Yahoo group domain default groups.yahoo.com");
        options.addOption("p", "proxy-host", true, "Proxy Host to use");
        options.addOption("t", "proxy-port", true, "Proxy Post to use");
        options.addOption("h", "help", false, "print this message");
        return options;
    }

    private static ArchiveBean getOptions(String[] args) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        ArchiveBean archiveBean = null;

        try {
            // parse the command line arguments
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
        if (commandLine != null) {

            archiveBean = new ArchiveBean();
            if (commandLine.hasOption("user")) {
                archiveBean.setUserId(commandLine.getOptionValue("user"));
                if (commandLine.hasOption("password")) {
                    archiveBean.setPassword(commandLine.getOptionValue("password"));
                } else {
                    System.out.println("Password required");
                }
            }
            if (commandLine.hasOption("group")) {
                archiveBean.setGroupName(commandLine.getOptionValue("group"));
            }
            if (commandLine.hasOption("start-from")) {
                String startFrom =
                        commandLine.getOptionValue("start-from", "0");
                try {
                    archiveBean.setStartMessageKey(startFrom);
                } catch (NumberFormatException nfe) {
                    System.out.println("startfrom should be a number");
                }
            }
            if (commandLine.hasOption("no-of-messages")) {
                String noOfMessages =
                        commandLine.getOptionValue("no-of-messages", "-1");
                try {
                    archiveBean.setNoOfMessagesToArchive(Integer.parseInt(noOfMessages));
                } catch (NumberFormatException nfe) {
                    System.out.println("noofmessages should be a number");
                }
            }
            if (commandLine.hasOption("sleep")) {
                String sleep = commandLine.getOptionValue("sleep", "0");
                try {
                    archiveBean.setSleepBetweenMessages(Integer.parseInt(sleep));
                } catch (NumberFormatException nfe) {
                    System.out.println("sleep should be a number");
                }
            }
            if (commandLine.hasOption("domain")) {
                archiveBean.setDomain(commandLine.getOptionValue("domain",
                        "groups.yahoo.com"));
            }
            if (commandLine.hasOption("proxy-host")) {
                archiveBean.setProxyHost(commandLine.getOptionValue("proxy-host"));
            }
            if (commandLine.hasOption("proxy-port")) {
                archiveBean.setProxyPort(Integer.parseInt(commandLine.getOptionValue("proxy-port")));
            }
        }
        return archiveBean;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        Logger log = Logger.getLogger(RunArchiver.class.getName());
        ArchiveBean archiveBean = getOptions(args);

        if (archiveBean != null) {
            RunArchiver archive = new RunArchiver();
            archive.setArchiveBean(archiveBean);

            // Store mBoxStore =
            // new MboxStoreImpl("./" + archiveBean.getGroupName());
            // mBoxStore.open();

            // Store googleGroupsStore =
            // new GoogleGroupsImpl();
            // googleGroupsStore.open();
            // com.mysql.jdbc.Driver
            // jdbc:mysql://[host][,failoverhost...][:port]/[database] »
            // [?propertyName1][=propertyValue1][&propertyName2][=propertyValue2].
            Store dbStore =
                    new MysqlDbStoreImpl(
                            "jdbc:mysql://thiru.in/archives?user=root&password=Humble123$&useCompression=true",
                            archiveBean.getGroupName() + "_");
            // new DerbyDbStoreImpl(
            // "org.apache.derby.jdbc.EmbeddedDriver",
            // "jdbc:derby:" + archiveBean.getGroupName()
            // + ";create=true;user="
            // + archiveBean.getGroupName()
            // + ";password=password;",
            // archiveBean.getGroupName() + "_");
            // Store dbStore =
            // new DbStoreImpl("org.apache.derby.jdbc.ClientDriver",
            // "jdbc:derby://localhost:1527/"
            // + archiveBean.getGroupName()
            // + ";create=true;user="
            // + archiveBean.getGroupName()
            // + ";password=password;",
            // archiveBean.getGroupName() + "_");
            dbStore.open();

            // Store zipStore =
            // new ZipStoreImpl(archiveBean.getGroupName() + ".zip");
            //
            // zipStore.open();
            AbstractArchiver archiver = new YahooGroupArchiver(archiveBean);
            archiver.setHtmlParser(new NekoHtmlParserImpl());

            HttpClient httpClient = new CommonsHttpClient();

            if (archiveBean.getProxyHost() != null) {
                httpClient.initialize(archiveBean.getProxyHost(),
                        archiveBean.getProxyPort());
            } else {
                httpClient.initialize();
            }
            archiver.setHttpClient(httpClient);

            YahooGroupArchiveListener archiveListener =
                    new YahooGroupArchiveListener(archiveBean.getGroupName(),
                            archiveBean.getSleepBetweenMessages());

            if (archiveBean.getStartMessageKey() == null) {
                archiveBean.setStartMessageKey(dbStore.getLastMessageKey());
                log.info("Last Message Key " + archiveBean.getStartMessageKey());
                if (archiveBean.getStartMessageKey() != null) {
                    archive.login(archiver, archiveListener);
                    String page =
                            archiver.getMessagePage(archiveBean.getStartMessageKey());
                    if (page != null) {
                        String messageKey = archiver.getNextMessageKey(page);
                        if (messageKey != null) {
                            archiveBean.setStartMessageKey(messageKey);
                        }
                    } else {
                        log.logp(Level.SEVERE, "", "",
                                "Couldn't get Page for Last Message Key"
                                        + archiveBean.getStartMessageKey());
                    }
                } else {
                    archiveBean.setStartMessageKey("1");
                }
            }
            // archiveListener.addStore(mBoxStore);
            archiveListener.addStore(dbStore);
            // archiveListener.addStore(googleGroupsStore);

            archive.archive(archiver, archiveListener);

            // mBoxStore.close();
            dbStore.close();
            // zipStore.close();
        }
    }
}
