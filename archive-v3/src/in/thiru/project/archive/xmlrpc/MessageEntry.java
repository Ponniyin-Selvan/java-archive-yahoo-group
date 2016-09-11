package in.thiru.project.archive.xmlrpc;

import java.util.Map;

import org.panteleyev.blogapi.Blog;
import org.panteleyev.blogapi.MetaWeblogEntry;

public class MessageEntry extends MetaWeblogEntry {

    private String author = "";

    private boolean createAuthor;

    private boolean appendThread;

    private String appendDelimiter = "<!--page-break-->";

    private boolean promote;

    private boolean sticky;

    private String messageKey;
    
    private long postedOn;

    public MessageEntry(Blog blog) {
        super(blog);
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isCreateAuthor() {
        return createAuthor;
    }

    public void setCreateAuthor(boolean createAuthor) {
        this.createAuthor = createAuthor;
    }

    public boolean isAppendThread() {
        return appendThread;
    }

    public void setAppendThread(boolean appendThread) {
        this.appendThread = appendThread;
    }

    public String getAppendDelimiter() {
        return appendDelimiter;
    }

    public void setAppendDelimiter(String appendDelimiter) {
        this.appendDelimiter = appendDelimiter;
    }

    public boolean isPromote() {
        return promote;
    }

    public void setPromote(boolean promote) {
        this.promote = promote;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageNo) {
        this.messageKey = messageNo;
    }

    
    public long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getContent() {
        Map<String, Object> content = (Map<String, Object>)super.getContent();
        
        content.put("author", getAuthor());
        content.put("createAuthor", isCreateAuthor());
        content.put("appendThread", isAppendThread());
        content.put("appendDelimiter", getAppendDelimiter());
        content.put("promote", isPromote());
        content.put("sticky", isSticky());
        content.put("message_key", getMessageKey());
        content.put("posted_on", getPostedOn());
        // FIXME
        content.put("forum_id", getBlog().getCategories().get(0).getId());
        
        return content;
    }
}
