package in.thiru.project.archive;

import in.thiru.project.archive.html.HtmlParser;
import in.thiru.project.archive.http.HttpClient;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractArchiver implements Archiver {

    protected HttpClient httpClient;
    protected ArchiveBean archiveBean;
    protected HtmlParser htmlParser;
    
    public AbstractArchiver() {
    }

    public AbstractArchiver(ArchiveBean archiveBean) {
        this();
        this.archiveBean = archiveBean;
    }
    
    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HtmlParser getHtmlParser() {
        return htmlParser;
    }

    public void setHtmlParser(HtmlParser htmlParser) {
        this.htmlParser = htmlParser;
    }

    protected String getMessageUri(String uri, Map<String, String> values) {
        String updatedUri = uri;

        Set<String> set = values.keySet();

        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = values.get(key);
            updatedUri = updatedUri.replace("${" + key + "}", value);
        }
        return updatedUri;
    }
    
    protected String getPage(String uri) throws IOException {
        return httpClient.getPage(uri);
    }
}
