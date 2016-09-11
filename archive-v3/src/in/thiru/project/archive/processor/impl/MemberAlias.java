package in.thiru.project.archive.processor.impl;

import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.AbstractMessageProcessor;

public class MemberAlias extends AbstractMessageProcessor {

    private Properties alias = new Properties();

    public MemberAlias(Properties alias) {
        this.alias = alias;
    }
    
    @Override
    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException {
        
        String member = (String)details.get("member");
        String memberAlias = alias.getProperty(member);
        if (memberAlias != null) {
            details.put("member", memberAlias);
        }
    }

}
