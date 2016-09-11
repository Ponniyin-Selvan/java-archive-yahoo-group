package in.thiru.project.archive.processor.impl;

import in.thiru.project.archive.formatter.Formatter;
import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.AbstractMessageProcessor;
import in.thiru.project.archive.util.MimeTypeUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

public class ExtractMessageDetails extends AbstractMessageProcessor {

    private Formatter subjectFormatter;

    public Formatter getSubjectFormatter() {
        return subjectFormatter;
    }

    public void setSubjectFormatter(Formatter subjectFormatter) {
        this.subjectFormatter = subjectFormatter;
    }

    @Override
    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException {

        InternetAddress[] froms = (InternetAddress[])message.getFrom();
        InternetAddress from = null;
        if (froms.length > 0) {
            from = froms[0];
        }

        // FIXME - yahoo group specific
        String memberId = message.getFirstHeader("X-Yahoo-Profile");

        String subject = message.getSubject();
        
        subject = (subject == null ? "(no subject)" : subjectFormatter.format(message.getSubject()));
        subject = ("".equals(subject) ? "(no subject)" : subject);

        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, "text/plain", "text/html");

        Date sentOn = message.getSentDate();

        details.put("from.email", (from == null ? memberId : from.getAddress()));
        details.put("from.name", (from == null ? memberId : from.getPersonal()));
        memberId = (memberId == null ? from.getPersonal() : memberId);
        memberId = (memberId == null ? from.getAddress() : memberId);
       
        details.put("member", memberId);
        details.put("subject", subject);
        details.put("parts", parts);
        details.put("sentOn", sentOn);
    }
}
