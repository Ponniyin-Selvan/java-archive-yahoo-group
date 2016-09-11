package in.thiru.project.archive;

import java.io.IOException;

import org.xml.sax.InputSource;

public interface Archiver {

    boolean login(String userId, String password);
    
    boolean logout();
    
    String getMessageUri(String messageKey, String uri);
    
    String getMessagePage(String messageKey) throws IOException;
    
    String getNextMessageKey(String messagePage);
    
    String getNextMessageKey(InputSource messagePage);
    
    String getMessageSource(String messagePage);
    
    String getMessageSource(InputSource messagePage);
    
    MessageDetails getMessageDetails(String page);
    
    long getTotalMessages();
}
