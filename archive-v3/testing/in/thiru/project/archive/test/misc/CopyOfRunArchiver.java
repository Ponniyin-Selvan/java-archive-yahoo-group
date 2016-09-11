/**
 * 
 */
package in.thiru.project.archive.test.misc;

import in.thiru.project.archive.ArchiveBean;
import in.thiru.project.archive.listner.ArchiveListener;
import in.thiru.project.archive.store.Store;
import in.thiru.project.archive.store.impl.db.DerbyDbStoreImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Humble
 * 
 */
public class CopyOfRunArchiver {

    Logger log = Logger.getLogger(CopyOfRunArchiver.class.getName());

    private ArchiveBean archiveBean = new ArchiveBean();

    private HttpClient httpClient;

    public CopyOfRunArchiver() {
    }

    public ArchiveBean getArchiveBean() {
        return archiveBean;
    }

    public void setArchiveBean(ArchiveBean archiveBean) {
        this.archiveBean = archiveBean;
    }

    private boolean login() {

        boolean successful = false;
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();

        postParams.add(new BasicNameValuePair("login",
                this.archiveBean.getUserId()));
        postParams.add(new BasicNameValuePair("passwd",
                this.archiveBean.getPassword()));
        postParams.add(new BasicNameValuePair(".src", "ygrp"));
        postParams.add(new BasicNameValuePair(".done", "http://my.yahoo.com"));

        try {
            UrlEncodedFormEntity entity =
                    new UrlEncodedFormEntity(postParams, "UTF-8");
            HttpPost httpPost =
                    new HttpPost("https://login.yahoo.com/config/login");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            log.info("Login Response Status "
                     + response.getStatusLine().getStatusCode());
            successful = (response.getStatusLine().getStatusCode() == 302);

        } catch (UnsupportedEncodingException usee) {
            log.logp(Level.SEVERE, "", "",
                    "UnsupportedEncodingException Occurred while logging in",
                    usee);
        } catch (ClientProtocolException e) {
            log.logp(Level.SEVERE, "", "",
                    "ClientProtocolException Occurred while logging in", e);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "", "",
                    "IOException Occurred while logging in", e);
        }
        return successful;
    }

    private boolean logout() {
        try {
            HttpGet httpGet =
                    new HttpGet(
                            "http://login.yahoo.com/config/login?logout=1&&.partner=&.intl=us&.done=http%3a%2f%2fmy.yahoo.com%2findex.html&.src=my");

            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            log.logp(Level.SEVERE, "", "",
                    "ClientProtocolException Occurred while logging out", e);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "", "",
                    "IOException Occurred while logging out", e);
        }
        return true;
    }

    public void archive(ArchiveListener listener) {

        HttpParams params = new BasicHttpParams();
        // Increase max total connection to 200
        ConnManagerParams.setMaxTotalConnections(params, 10);
        // Increase default max connection per route to 20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(10);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm =
                new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);

        if (null != this.archiveBean.getProxyHost()) {
            HttpHost proxy =
                    new HttpHost(this.archiveBean.getProxyHost(),
                            this.archiveBean.getProxyPort());
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
        }

        httpClient.getParams().setParameter("http.protocol.version",
                HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter("http.socket.timeout",
                new Integer(30000));
        httpClient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");

        boolean loggedIn = false;
        if (this.archiveBean.getUserId() != null) {
            listener.beforeLogin();
            loggedIn = login();
        }

        if (this.archiveBean.getUserId() != null && loggedIn) {
            listener.afterLogin();
        }

        archiveMessages(listener);

        if (this.archiveBean.getUserId() != null && loggedIn) {
            listener.beforeLogout();
            logout();
            listener.logout();
        }
    }

    private void archiveMessages(ArchiveListener listener) {
        long totalMessagesArchived = 0;
        long messageNo = Long.parseLong(this.archiveBean.getStartMessageKey());

        HTMLConfiguration settings = new HTMLConfiguration();
        settings.setProperty(
                "http://cyberneko.org/html/properties/names/elems", "lower");
        settings.setProperty(
                "http://cyberneko.org/html/properties/default-encoding",
                "UTF-8");

        DOMParser parser = new DOMParser(settings);
        final String YG_MESSAGE_TD = "*//td[@class=\"source fixed\"]";
        final String CURRENT_MESSAGE_NO_XPATH_QUERY =
                "*//table[@class=\"footaction\"]//td[@align=\"right\"]//span[@style=\"font-weight: bold\"]";
        final String NEXT_MESSAGE_NO_XPATH_QUERY =
                "*//table[@class=\"footaction\" and @cellpadding=\"2\"]//td[@align=\"right\"]//noscript/a";
        XPath xpath = XPathFactory.newInstance().newXPath();

        long nextMessageNo = 0;
        long noOfMessagesToArchive =
                this.archiveBean.getNoOfMessagesToArchive();

        while ((noOfMessagesToArchive == -1 && messageNo != -1)
               || (noOfMessagesToArchive != -1 && totalMessagesArchived < noOfMessagesToArchive)) {

            String page = listener.readMessagePage(Long.toString(messageNo));
            if (page == null) {
                String uri =
                        "http://" + this.archiveBean.getDomain() + "/group/"
                                + this.archiveBean.getGroupName() + "/message/"
                                + messageNo + "?source=1&unwrap=1&var=0&l=1";
                try {
                    HttpGet httpGet = new HttpGet(uri);

                    HttpResponse response = httpClient.execute(httpGet);
                    page = EntityUtils.toString(response.getEntity());

                } catch (ClientProtocolException e) {
                    log.logp(
                            Level.SEVERE,
                            "",
                            "",
                            "ClientProtocolException Occurred while logging out",
                            e);
                } catch (IOException e) {
                    log.logp(Level.SEVERE, "", "",
                            "IOException Occurred while logging out", e);
                }
            }

            if (page != null) {
                listener.afterReadMessagePage(Long.toString(messageNo), page);

                String messageContent = null;
                nextMessageNo = 0;
                try {
                    parser.parse(new InputSource(new StringReader(page)));

                    Node resultNode =
                            (Node)xpath.evaluate(YG_MESSAGE_TD,
                                    parser.getDocument(), XPathConstants.NODE);
                    if (resultNode == null) {
                        log.logp(Level.SEVERE, "", "",
                                "Not able to get the Message Id, probably bandwidth exceeded");
                    } else {
                        messageContent = resultNode.getTextContent();
                    }
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

                    resultNode =
                            (Node)xpath.evaluate(
                                    CURRENT_MESSAGE_NO_XPATH_QUERY,
                                    parser.getDocument(), XPathConstants.NODE);
                    String currentMessageNoString =
                            resultNode.getTextContent().split("#")[1];
                    long currentMessageNo =
                            Long.parseLong(currentMessageNoString);
                    messageNo = currentMessageNo;

                    NodeList resultNodeList =
                            (NodeList)xpath.evaluate(
                                    NEXT_MESSAGE_NO_XPATH_QUERY,
                                    parser.getDocument(),
                                    XPathConstants.NODESET);
                    Node node = null;
                    if (resultNodeList.getLength() > 1) {
                        node = resultNodeList.item(1); // a
                    } else {
                        node = resultNodeList.item(0); // a
                    }
                    if ("Next >".equals(node.getFirstChild().getTextContent())) {
                        String nextUrl =
                                node.getAttributes()
                                        .getNamedItem("href")
                                        .getNodeValue();
                        nextUrl = nextUrl.split("\\?")[0];
                        String nextMessageNoString = nextUrl.split("/")[4];
                        nextMessageNo = Long.parseLong(nextMessageNoString);
                    } else {
                        nextMessageNo = -1;
                        // Reached end of messages
                        log.info("Couldn't find next message no, Reached end of messages link text "
                                 + node.getFirstChild().getTextContent());
                    }

                } catch (XPathExpressionException xee) {
                    log.logp(Level.SEVERE, "", "", "XPath Expression", xee);
                } catch (SAXException e) {
                    log.logp(Level.SEVERE, "", "", "SAXException ", e);
                } catch (IOException e) {
                    log.logp(Level.SEVERE, "", "", "IOException", e);
                } catch (Exception e) {
                    log.logp(Level.SEVERE, "", "", "Exception", e);
                }

                if (messageContent == null
                    || !listener.archiveMessage(Long.toString(messageNo),
                            messageContent)) {
                    break;
                }
                totalMessagesArchived++;
                messageNo = nextMessageNo;
                if (messageNo == -1) {
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
        options.addOption(OptionBuilder.withArgName("startfrom").withLongOpt(
                "start-from").hasOptionalArg().withDescription(
                "Archive messages from this no, default is 0").withType(
                Long.class).create('s'));
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

    /**
     * @param args
     */
    public static void main(String[] args) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        ArchiveBean archiveBean = new ArchiveBean();

        try {
            // parse the command line arguments
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
        if (commandLine != null) {
            CopyOfRunArchiver archive = new CopyOfRunArchiver();

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
            // Store mboxStore = new MboxStoreImpl("./" +
            // archive.getGroupName());
            Store store =
                    new DerbyDbStoreImpl("org.apache.derby.jdbc.ClientDriver",
                            "jdbc:derby://localhost:1527/"
                                    + archiveBean.getGroupName()
                                    + ";create=true;user="
                                    + archiveBean.getGroupName()
                                    + ";password=password;",
                            archiveBean.getGroupName() + "_");
            store.open();
            if (archiveBean.getStartMessageKey() == null) {
                archiveBean.setStartMessageKey(store.getLastMessageKey());
            }
            archive.setArchiveBean(archiveBean);

            // archive.archive(new YahooGroupArchiveListener(
            // archiveBean.getGroupName(),
            // archiveBean.getSleepBetweenMessages(), store));
            // store.close();
        }
    }

}
