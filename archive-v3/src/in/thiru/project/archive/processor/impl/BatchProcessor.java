package in.thiru.project.archive.processor.impl;

import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.AbstractMessageProcessor;
import in.thiru.project.archive.processor.MessageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

public class BatchProcessor extends AbstractMessageProcessor {

    List<MessageProcessor> batchList = new ArrayList<MessageProcessor>();

    public BatchProcessor() {
    }

    public BatchProcessor(List<MessageProcessor> batchList) {
        this.batchList = batchList;
    }

    public void addProcessor(MessageProcessor messageProcessor) {
        batchList.add(messageProcessor);

    }

    @Override
    public void processMessage(ArchiveMessage message,
            Map<String, Object> details) throws MessagingException {
        for (MessageProcessor messageProcessor : batchList) {
            messageProcessor.processMessage(message, details);
        }
    }
}
