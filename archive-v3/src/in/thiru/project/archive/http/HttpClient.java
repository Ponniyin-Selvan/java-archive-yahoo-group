package in.thiru.project.archive.http;

import java.io.IOException;
import java.util.Map;

public interface HttpClient {

    public abstract void initialize();

    public abstract void initialize(String proxyHost, int port);

    public abstract String getPage(String uri) throws IOException;

    public abstract void postPage(String uri,
            Map<String, String> formValues);

}