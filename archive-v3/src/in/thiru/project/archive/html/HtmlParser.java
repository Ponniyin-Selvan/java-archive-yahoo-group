package in.thiru.project.archive.html;


import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface HtmlParser {

    public Node findNode(String page, String expression)
            throws HtmlParserException;

    public NodeList findNodes(String page, String expression)
            throws HtmlParserException;

    public Node findNode(DOMParser parser, String page, String expression)
            throws HtmlParserException;

    public NodeList findNodes(DOMParser parser, String page, String expression)
            throws HtmlParserException;

    public DOMParser parse(String page);

}