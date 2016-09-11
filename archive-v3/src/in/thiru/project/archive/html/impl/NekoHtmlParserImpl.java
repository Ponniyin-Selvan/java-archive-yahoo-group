package in.thiru.project.archive.html.impl;

import in.thiru.project.archive.html.HtmlParser;
import in.thiru.project.archive.html.HtmlParserException;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class NekoHtmlParserImpl implements HtmlParser {

    Logger log = Logger.getLogger(NekoHtmlParserImpl.class.getName());

    public DOMParser getParser() {
        HTMLConfiguration settings = new HTMLConfiguration();
        settings.setProperty(
                "http://cyberneko.org/html/properties/names/elems", "lower");
        settings.setProperty(
                "http://cyberneko.org/html/properties/default-encoding",
                "UTF-8");

        return new DOMParser(settings);
    }
    
    public XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
    }

    /* 
     * @see in.thiru.project.archive.html.impl.HtmlParser#findNode(java.lang.String, java.lang.String)
     */
    public Node findNode(String page, String expression)
            throws HtmlParserException {
        Node node = null;
        try {
            DOMParser parser = parse(page);
            node =
                    (Node)getXPath().evaluate(expression, parser.getDocument(),
                            XPathConstants.NODE);
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't find expression", e);
        }
        return node;
    }

    /* 
     * @see in.thiru.project.archive.html.impl.HtmlParser#findNodes(java.lang.String, java.lang.String)
     */
    public NodeList findNodes(String page, String expression)
            throws HtmlParserException {
        NodeList nodes = null;
        try {
            DOMParser parser = parse(page);
            nodes =
                    (NodeList)getXPath().evaluate(expression, parser.getDocument(),
                            XPathConstants.NODESET);
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't find expression", e);
        }
        return nodes;
    }

    /* 
     * @see in.thiru.project.archive.html.impl.HtmlParser#findNode(java.lang.String, java.lang.String)
     */
    public Node findNode(DOMParser parser, String page, String expression)
            throws HtmlParserException {
        Node node = null;
        try {
            node =
                    (Node)getXPath().evaluate(expression, parser.getDocument(),
                            XPathConstants.NODE);
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't find expression", e);
        }
        return node;
    }

    /* 
     * @see in.thiru.project.archive.html.impl.HtmlParser#findNodes(java.lang.String, java.lang.String)
     */
    public NodeList findNodes(DOMParser parser, String page, String expression)
            throws HtmlParserException {
        NodeList nodes = null;
        try {
            nodes =
                    (NodeList)getXPath().evaluate(expression, parser.getDocument(),
                            XPathConstants.NODESET);
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't find expression", e);
        }
        return nodes;
    }
    
    public DOMParser parse(String page) {
        DOMParser parser = null;
        try {
            parser = getParser();
            parser.parse(new InputSource(new StringReader(page)));
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't Parse page expression", e);
        }
        return parser;
    }
}
