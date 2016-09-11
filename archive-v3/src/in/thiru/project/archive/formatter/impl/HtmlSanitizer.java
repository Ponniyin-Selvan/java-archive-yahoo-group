package in.thiru.project.archive.formatter.impl;

import in.thiru.project.archive.formatter.AbstractFormatter;
import in.thiru.project.archive.html.HtmlParserException;
import in.thiru.project.archive.html.filters.impl.HtmlCompactFilter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Purifier;
import org.xml.sax.InputSource;

public class HtmlSanitizer extends AbstractFormatter {

    private Properties settings = new Properties();
    private List<AllowedTag> allowedTags = new ArrayList<AllowedTag>();
    private String[] removedTags;

    private class AllowedTag {
        private String tag;
        private String[] attributes;

        public AllowedTag(String tag, String[] attributes) {
            this.tag = tag;
            this.attributes = attributes;
        }

        public String getTag() {
            return tag;
        }

        public String[] getAttributes() {
            return attributes;
        }
        
        public String toString() {
            StringBuffer toStringTag = new StringBuffer(tag);
            toStringTag.append("@"); 
            if (null != attributes) {
                for(String attribute : attributes) {
                    toStringTag.append(attribute).append(",");
                }
            }
            return toStringTag.toString();
        }
    }

    public HtmlSanitizer() {
    }

    public HtmlSanitizer(Properties settings) {
        this.settings = settings;
    }

    public Properties getSettings() {
        return settings;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    private void initialize() {
        String allowedTagList = settings.getProperty("allowed.tags");
        String[] allowedTagAndAttributes = allowedTagList.split(";");
        for (String allowedTag : allowedTagAndAttributes) {
            allowedTag = allowedTag.trim();
            String[] allowedTagAndAttribute = allowedTag.split("@");
            String[] allowedAttributes =
                    (allowedTagAndAttribute.length > 1 ? allowedTagAndAttribute[1].split(",")
                                                      : null);
            this.allowedTags.add(new AllowedTag(allowedTagAndAttribute[0].trim(),
                    allowedAttributes));
        }

        String removedTagList = settings.getProperty("removed.tags");
        this.removedTags = removedTagList.split(";");
    }

    @Override
    public String format(String source) {
        String sanitizedHtml = source;

        if (allowedTags.size() == 0 && null == removedTags) {
            initialize();
        }
        ElementRemover remover = new ElementRemover();
        for (AllowedTag allowedTag : this.allowedTags) {
            remover.acceptElement(allowedTag.getTag(),
                    allowedTag.getAttributes());
        }

        for (String tag : this.removedTags) {
            remover.removeElement(tag.trim());
        }

        StringWriter stringWriter = new StringWriter();
        XMLDocumentFilter writer = new HtmlCompactFilter(stringWriter, "UTF-8");

        XMLDocumentFilter[] filters = {new Purifier(), remover, writer};

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
            parser.parse(new InputSource(new StringReader(source)));
        } catch (Exception e) {
            throw new HtmlParserException("Couldn't Parse page expression", e);
        }
        sanitizedHtml = stringWriter.toString();
        return sanitizedHtml.trim();
    }
}
