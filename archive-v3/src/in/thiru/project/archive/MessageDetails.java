package in.thiru.project.archive;

public class MessageDetails {

    String messageKey;
    
    String nextMessageKey;
    
    String messageSource;

    public MessageDetails() {
    }

    public MessageDetails(String messageKey, String messageSource) {
        super();
        this.messageKey = messageKey;
        this.messageSource = messageSource;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String currentMessageKey) {
        this.messageKey = currentMessageKey;
    }

    public String getNextMessageKey() {
        return nextMessageKey;
    }

    public void setNextMessageKey(String nextMessageKey) {
        this.nextMessageKey = nextMessageKey;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }
}
