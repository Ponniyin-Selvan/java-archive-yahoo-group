package in.thiru.project.archive.processor;

import in.thiru.project.archive.mail.ArchiveMessage;

import java.util.Map;

import javax.mail.MessagingException;

public interface MessageProcessor {

    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException;
}
