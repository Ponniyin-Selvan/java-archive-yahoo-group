package in.thiru.project.archive.processor.impl;

import in.thiru.project.archive.formatter.Formatter;
import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.AbstractMessageProcessor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Part;

public class ProcessContent extends AbstractMessageProcessor {

    private static final String X_MAILER_YAHOO_GROUPS =
            "Yahoo Groups Message Poster";
    private static final String USER_AGENT_EGROUPS = "eGroups-EW/0.82";

    private Formatter htmlContentFormatter;

    private Formatter plainContentFormatter;

    public Formatter getHtmlContentFormatter() {
        return htmlContentFormatter;
    }

    public void setHtmlContentFormatter(Formatter formatter) {
        this.htmlContentFormatter = formatter;
    }

    public Formatter getPlainContentFormatter() {
        return plainContentFormatter;
    }

    public void setPlainContentFormatter(Formatter plainContentFormatter) {
        this.plainContentFormatter = plainContentFormatter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException {

        List<Part> parts = (ArrayList<Part>)details.get("content");

        StringBuffer formattedContent = new StringBuffer("");
        for (Part part : parts) {

            // FIXME - Yahoo Group Specific
            String mailer = message.getFirstHeader("X-Mailer");
            String agent = message.getFirstHeader("User-Agent");

            String content = null;
            try {
                content = part.getContent().toString();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (X_MAILER_YAHOO_GROUPS.equals(mailer)
                && USER_AGENT_EGROUPS.equals(agent)) {
                try {
                    content =
                            new String(content.getBytes("iso-8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if ("text/html".equals(details.get("content.type"))) {
                formattedContent.append(htmlContentFormatter.format(content));
            } else {
                formattedContent.append(plainContentFormatter.format(content));
            }
        }
        details.put("formatted-content", formattedContent.toString());
    }

}
