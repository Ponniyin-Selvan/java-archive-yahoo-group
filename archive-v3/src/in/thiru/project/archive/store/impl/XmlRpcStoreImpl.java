package in.thiru.project.archive.store.impl;

import in.thiru.project.archive.formatter.Formatter;
import in.thiru.project.archive.formatter.impl.AcronymFormatter;
import in.thiru.project.archive.formatter.impl.EmailObfuscator;
import in.thiru.project.archive.formatter.impl.HtmlSanitizer;
import in.thiru.project.archive.formatter.impl.MultiFormatter;
import in.thiru.project.archive.formatter.impl.NewLineFormatter;
import in.thiru.project.archive.formatter.impl.SubjectFormatter;
import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.mail.yahoogroups.YahooArchiveMessage;
import in.thiru.project.archive.processor.impl.BatchProcessor;
import in.thiru.project.archive.processor.impl.ExtractMessageDetails;
import in.thiru.project.archive.processor.impl.MemberAlias;
import in.thiru.project.archive.processor.impl.PickHtmlContent;
import in.thiru.project.archive.processor.impl.ProcessContent;
import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.StoreException;
import in.thiru.project.archive.xmlrpc.MessageEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.panteleyev.blogapi.Blog;
import org.panteleyev.blogapi.WordPressAccount;

public class XmlRpcStoreImpl extends AbstractStore {

    private String xmlRpcUrl;
    private String userId;
    private String password;
    private Blog blog;
    private WordPressAccount wordPressAccount;

    public XmlRpcStoreImpl() {
    }

    public XmlRpcStoreImpl(String xmlRpcUrl, String userId, String password) {
        this();
        this.xmlRpcUrl = xmlRpcUrl;
        this.userId = userId;
        this.password = password;
    }

    public String getXmlRpcUrl() {
        return xmlRpcUrl;
    }

    public void setXmlRpcUrl(String xmlRpcUrl) {
        this.xmlRpcUrl = xmlRpcUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void close() throws StoreException {
    }

    @Override
    public String getLastMessageKey() {
        return null;
    }

    @Override
    public void open() throws StoreException {

        wordPressAccount = new WordPressAccount(this.xmlRpcUrl);

        try {
            wordPressAccount.login(this.userId, this.password);
        } catch (Exception e) {
            throw new StoreException(
                    "Not able to login with id/password provided", e);
        }

        // FIXME - Which one to take?
        blog = wordPressAccount.getBlog(0);
    }

    @Override
    public void addMessage(String messageKey, ArchiveMessage message)
            throws StoreException {

        super.addMessage(messageKey, message);
        try {
            ArchiveMessage yahooMessage = new YahooArchiveMessage(message);

            SubjectFormatter subjectFormatter = new SubjectFormatter();
            subjectFormatter.setRemoveWords(new String[] {
                                                          "re:",
                                                          "fw:",
                                                          "fwd:",
                                                          "\\[ponniyinselvan\\]",
                                                          "\\[[0-9]+ Attachments\\]"});
            Map<String, Object> details = new HashMap<String, Object>();
            ExtractMessageDetails extractProcessor =
                    new ExtractMessageDetails();
            extractProcessor.setSubjectFormatter(subjectFormatter);

            MultiFormatter htmlFormatter = new MultiFormatter();
            MultiFormatter plainFormatter = new MultiFormatter();

            Formatter emailObfuscator = new EmailObfuscator();

            htmlFormatter.addFormatter(emailObfuscator);

            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream(
                    "acronym-ponniyinselvan.properties"));
            Formatter acronymFormatter = new AcronymFormatter(properties);

            htmlFormatter.addFormatter(acronymFormatter);

            Properties htmlProperties = new Properties();
            htmlProperties.load(getClass().getClassLoader()
                    .getResourceAsStream("html-sanitizer.properties"));
            Formatter htmlSanitizer = new HtmlSanitizer(htmlProperties);
            htmlFormatter.addFormatter(htmlSanitizer);

            plainFormatter.addFormatter(new NewLineFormatter());
            plainFormatter.addFormatter(emailObfuscator);
            plainFormatter.addFormatter(acronymFormatter);
            plainFormatter.addFormatter(htmlSanitizer);

            ProcessContent processContent = new ProcessContent();
            processContent.setHtmlContentFormatter(htmlFormatter);

            ProcessContent processMessage = new ProcessContent();
            processMessage.setHtmlContentFormatter(htmlFormatter);
            processMessage.setPlainContentFormatter(plainFormatter);

            Properties memberAlias = new Properties();
            memberAlias.load(getClass().getClassLoader().getResourceAsStream(
                    "alias.properties"));
            MemberAlias alias = new MemberAlias(memberAlias);
            BatchProcessor processor = new BatchProcessor();
            processor.addProcessor(extractProcessor);
            processor.addProcessor(alias);
            processor.addProcessor(new PickHtmlContent());
            processor.addProcessor(processMessage);

            processor.processMessage(yahooMessage, details);
            String formattedContent = (String)details.get("formatted-content");

            MessageEntry blogEntry = new MessageEntry(blog);

            blogEntry.setBody(formattedContent);
            blogEntry.setSubject(details.get("subject").toString());
            blogEntry.setAuthor(details.get("member").toString());
            blogEntry.setCreateAuthor(true);
            blogEntry.setMessageKey(messageKey);
            blogEntry.setDateCreated(yahooMessage.getSentDate());
            wordPressAccount.createEntry(blogEntry);
        } catch (Exception e) {
            throw new StoreException("Not able to add Message through xmlrpc",
                    e);
        }
    }

    @Override
    public void addMessage(String messageKey, String message) {
    }

}
