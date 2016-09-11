package in.thiru.project.archive.processor.impl;

import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.AbstractMessageProcessor;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Part;

public class PickPlainTextContent extends AbstractMessageProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException {
        Map<String, List<Part>> parts =
                (Map<String, List<Part>>)details.get("parts");
        if (parts != null) {
            List<Part> contentParts = parts.get("text/plain");
            if (contentParts == null) {
                contentParts = parts.get("text/plain"); // fallback to plain text
                
            }
            details.put("content", contentParts);
        }
    }
}
