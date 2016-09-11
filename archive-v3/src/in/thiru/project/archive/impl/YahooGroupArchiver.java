/**
 * 
 */
package in.thiru.project.archive.impl;

import in.thiru.project.archive.AbstractArchiver;
import in.thiru.project.archive.ArchiveBean;
import in.thiru.project.archive.MessageDetails;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author vthirum3
 * 
 */
public class YahooGroupArchiver extends AbstractArchiver {

    Logger log = Logger.getLogger(YahooGroupArchiver.class.getName());

    private String MESSAGE_URI =
            "http://${domain}/group/${groupName}/message/${messageKey}?source=1&unwrap=1&var=0&l=1";
    final String YG_MESSAGE_TD = "*//td[@class=\"source fixed\"]";
    final String CURRENT_MESSAGE_NO_XPATH_QUERY =
            "*//table[@class=\"footaction\"]//td[@align=\"right\"]//span[@style=\"font-weight: bold\"]";
    final String NEXT_MESSAGE_NO_XPATH_QUERY =
            "*//table[@class=\"footaction\" and @cellpadding=\"2\"]//td[@align=\"right\"]//noscript/a";

    public YahooGroupArchiver() {
        super();
    }

    public YahooGroupArchiver(ArchiveBean archiveBean) {
        super(archiveBean);
        // if (archiveBean.getStartMessageKey() == null) {
        // archiveBean.setStartMessageKey("0");
        // }
    }

    @Override
    public String getMessageUri(String messageKey, String uri) {
        Map<String, String> values = new Hashtable<String, String>();
        values.put("domain", archiveBean.getDomain());
        values.put("groupName", archiveBean.getGroupName());
        values.put("messageKey", (messageKey == null ? "" : messageKey));
        return super.getMessageUri(uri, values);
    }

    /*
     * @see in.thiru.project.archive.Archiver#getMessagePage(java.lang.String)
     */
    @Override
    public String getMessagePage(String messageKey) throws IOException {

        return getPage(getMessageUri(messageKey, MESSAGE_URI));
    }

    /*
     * @see in.thiru.project.archive.Archiver#getMessageSource(java.lang.String)
     */
    @Override
    public String getMessageSource(String messagePage) {

        Node resultNode = htmlParser.findNode(messagePage, YG_MESSAGE_TD);
        return getMessageSource(resultNode);
    }

    private String getMessageSource(Node resultNode) {
        String messageSource = null;

        if (resultNode == null) {
            log.logp(Level.SEVERE, "", "",
                    "Not able to get the Message Id, probably bandwidth exceeded");
        } else {
            messageSource = resultNode.getTextContent();
        }
        return messageSource;
    }

    /*
     * @see
     * in.thiru.project.archive.Archiver#getMessageSource(org.xml.sax.InputSource
     * )
     */
    @Override
    public String getMessageSource(InputSource messagePage) {

        return null;
    }

    /*
     * @see
     * in.thiru.project.archive.Archiver#getNextMessageKey(java.lang.String)
     */
    @Override
    public String getNextMessageKey(String messagePage) {

        NodeList resultNodeList =
                htmlParser.findNodes(messagePage, NEXT_MESSAGE_NO_XPATH_QUERY);
        return getNextMessageKey(resultNodeList);
    }

    private String getNextMessageKey(NodeList resultNodeList) {

        String nextMessageKey = null;
        Node node = null;
        if (resultNodeList.getLength() > 1) {
            node = resultNodeList.item(1); // a
        } else {
            node = resultNodeList.item(0); // a
        }
        if (node != null
            && "Next >".equals(node.getFirstChild().getTextContent())) {
            String nextUrl =
                    node.getAttributes().getNamedItem("href").getNodeValue();
            nextUrl = nextUrl.split("\\?")[0];
            String nextMessageNoString = nextUrl.split("/")[4];
            nextMessageKey = nextMessageNoString;
        } else {
            // Reached end of messages
            log.info("Couldn't find next message no, Reached end of messages link text "
                     + node);
        }
        return nextMessageKey;
    }

    @Override
    public MessageDetails getMessageDetails(String page) {

        MessageDetails messageDetails = new MessageDetails();
        DOMParser parser = htmlParser.parse(page);

        Node resultNode =
                htmlParser.findNode(parser, page,
                        CURRENT_MESSAGE_NO_XPATH_QUERY);
        Node contentNode = htmlParser.findNode(parser, page, YG_MESSAGE_TD);

        NodeList nodeList =
                htmlParser.findNodes(parser, page, NEXT_MESSAGE_NO_XPATH_QUERY);

        if (resultNode == null || null == contentNode || null == nodeList) {
            log.logp(Level.SEVERE, "", "", "Couldn't XPath the message");
            FileWriter file;
            try {
                file =
                        new FileWriter("coredump-" + archiveBean.getGroupName()
                                       + System.currentTimeMillis());
                file.write(page);
                file.close();
            } catch (IOException e) {
                log.logp(Level.SEVERE, "", "", "Couldnt create coredump file",
                        e);
            }
        } else {
            messageDetails.setMessageKey(resultNode.getTextContent()
                    .split("#")[1]);
            messageDetails.setMessageSource(getMessageSource(contentNode));
            messageDetails.setNextMessageKey(getNextMessageKey(nodeList));
        }
        parser.reset();
        parser = null;
        resultNode = null;
        contentNode = null;
        nodeList = null;
        return messageDetails;
    }

    /*
     * @see
     * in.thiru.project.archive.Archiver#getNextMessageKey(org.xml.sax.InputSource
     * )
     */
    @Override
    public String getNextMessageKey(InputSource messagePage) {

        return null;
    }

    /*
     * @see in.thiru.project.archive.Archiver#getTotalMessages()
     */
    @Override
    public long getTotalMessages() {

        return 0;
    }

    /*
     * @see in.thiru.project.archive.Archiver#login(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean login(String userId, String password) {

        Map<String, String> values = new Hashtable<String, String>();
        values.put("login", archiveBean.getUserId());
        values.put("passwd", archiveBean.getPassword());
        values.put(".src", "ygrp");
        values.put(".done", "http://my.yahoo.com");
        httpClient.postPage("https://login.yahoo.com/config/login", values);
        return true;
    }

    /*
     * @see in.thiru.project.archive.Archiver#logout()
     */
    @Override
    public boolean logout() {

        return false;
    }

}
