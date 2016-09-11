package in.thiru.project.archive.http.impl;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpParams;

public class HttpClient extends DefaultHttpClient {

    
    public HttpClient() {
        super();
    }

    public HttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    public HttpClient(HttpParams params) {
        super(params);
    }

    @Override
    protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler(10, true);
    }

}
