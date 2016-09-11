package in.thiru.project.archive.test.misc;

import in.thiru.project.archive.html.HtmlParserException;
import in.thiru.project.archive.html.filters.impl.HtmlCompactFilter;
import in.thiru.project.archive.util.MimeTypeUtil;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Purifier;
import org.xml.sax.InputSource;

public class TestFindHtmlMessage extends TestFindMessage {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public String sanitizeHtml(String htmlContent) {
        String sanitizedHtml = htmlContent;

        String[] acceptedTags =
                {"b", "p", "i", "s", "a", "table", "thead", "tbody", "tfoot",
                 "tr", "th", "td", "dd", "dl", "dt", "em", "h1", "h2", "h3",
                 "h4", "h5", "h6", "li", "ul", "ol", "span", "div", "strike",
                 "strong", "sub", "sup", "pre", "del", "code", "blockquote",
                 "strike", "kbd", "br", "hr", "area", "map", "small", "big"};

        String[] removeTags =
                {"head", "img", "style", "hr", "meta", "script", "object",
                 "param", "embed"};

        ElementRemover remover = new ElementRemover();
        for (String tag : acceptedTags) {
            remover.acceptElement(tag, null);
        }
        remover.acceptElement("a", new String[] {"href"});

        for (String tag : removeTags) {
            remover.removeElement(tag);
        }

        StringWriter stringWriter = new StringWriter();
        XMLDocumentFilter writer = new HtmlCompactFilter(stringWriter, "UTF-8");

        XMLDocumentFilter[] filters = {remover, new Purifier(), writer};

        HTMLConfiguration settings = new HTMLConfiguration();
        settings.setProperty("http://cyberneko.org/html/properties/filters",
                filters);

        settings.setProperty(
                "http://cyberneko.org/html/properties/names/elems", "lower");
        settings.setProperty(
                "http://cyberneko.org/html/properties/default-encoding",
                "UTF-8");

        DOMParser parser = new DOMParser(settings);
        try {
            parser.parse(new InputSource(new StringReader(htmlContent)));
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't Parse page expression", e);
        }
        sanitizedHtml = stringWriter.toString();
        return sanitizedHtml;
    }

    public boolean testfoundMessage(Message message) {
        boolean keepFinding = true;
        String[] mimeTypes = new String[] {"text/html"};
        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, mimeTypes);
        if (null != parts && parts.size() > 0) {
            Part htmlPart = parts.get("text/html").get(0);
            try {
                String beforeHtmlContent = htmlPart.getContent().toString();

                // System.out.println(beforeHtmlContent);
                String afterHtmlContent = sanitizeHtml(beforeHtmlContent);
                // System.out.println(afterHtmlContent);
                if (!beforeHtmlContent.equals(afterHtmlContent)) {
                    System.out.println("*** ----- Before ---- *****\n"
                                       + beforeHtmlContent);
                    System.out.println("*** ----- After ---- *****\n"
                                       + afterHtmlContent);
                }
                // afterHtmlContent = linkify(afterHtmlContent);
                // keepFinding = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return keepFinding;
    }
}
