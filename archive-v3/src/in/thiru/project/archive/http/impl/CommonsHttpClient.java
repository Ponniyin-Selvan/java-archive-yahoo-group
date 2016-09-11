package in.thiru.project.archive.http.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class CommonsHttpClient
        implements in.thiru.project.archive.http.HttpClient {

    Logger log = Logger.getLogger(CommonsHttpClient.class.getName());

    private int retryCount = 5;

    private int sleepBetweenRetry = 12000;

    private HttpClient httpClient;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retry) {
        this.retryCount = retry;
    }

    public int getSleepBetweenRetry() {
        return sleepBetweenRetry;
    }

    public void setSleepBetweenRetry(int sleepBetweenRetry) {
        this.sleepBetweenRetry = sleepBetweenRetry;
    }

    /*
     * @see in.thiru.project.archive.html.impl.HttpClient#initialize()
     */
    public void initialize() {
        initialize(null, 0);
    }

    /*
     * @see
     * in.thiru.project.archive.html.impl.HttpClient#initialize(java.lang.String
     * , int)
     */
    public void initialize(String proxyHost, int port) {

        HttpParams params = new BasicHttpParams();
        // Increase max total connection to 200
        ConnManagerParams.setMaxTotalConnections(params, 10);
        // Increase default max connection per route to 20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(10);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http",
                PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm =
                new ThreadSafeClientConnManager(params, schemeRegistry);
        this.httpClient = new HttpClient(cm, params);

        httpClient.getParams().setParameter("http.protocol.version",
                HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter("http.socket.timeout",
                new Integer(30000));
        httpClient.getParams().setParameter("http.protocol.content-charset",
                "UTF-8");
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);

        if (null != proxyHost) {
            HttpHost proxy = new HttpHost(proxyHost, port);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
        }
    }

    /*
     * @see
     * in.thiru.project.archive.html.impl.HttpClient#getPage(java.lang.String)
     */
    public String getPage(String uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);

        HttpResponse response;
        String page = null;
        int retry = 1;

        do {

            try {
                response = httpClient.execute(httpGet);
                page = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                log.logp(
                        Level.SEVERE,
                        "",
                        "",
                        "ClientProtocolException Occurred while getting uri content",
                        e);
            } catch (IOException e) {
                log.logp(Level.SEVERE, "", "",
                        "IOException Occurred while getting page", e);
                if (e instanceof SocketTimeoutException) {
                    retry++;
                    try {
                        Thread.sleep(sleepBetweenRetry);
                    } catch (InterruptedException e1) {
                        log.logp(Level.SEVERE, "", "",
                                "InterruptedException Occurred while Sleeping",
                                e);
                    }
                } else {
                    throw e;
                }
            }
        } while (page == null && retry < this.retryCount);
        return page;
    }

    /*
     * @see
     * in.thiru.project.archive.html.impl.HttpClient#postPage(java.lang.String,
     * java.util.Hashtable)
     */
    public void postPage(String uri, Map<String, String> formValues) {

        Set<String> set = formValues.keySet();

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();

        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = formValues.get(key);
            postParams.add(new BasicNameValuePair(key, value));
        }

        // boolean successful = false;

        try {
            UrlEncodedFormEntity entity =
                    new UrlEncodedFormEntity(postParams, "UTF-8");
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            log.info("Post Response Status "
                     + response.getStatusLine().getStatusCode());
            // successful = (response.getStatusLine().getStatusCode() == 302);

        } catch (UnsupportedEncodingException usee) {
            log.logp(Level.SEVERE, "", "",
                    "UnsupportedEncodingException Occurred while logging in",
                    usee);
        } catch (ClientProtocolException e) {
            log.logp(Level.SEVERE, "", "",
                    "ClientProtocolException Occurred while logging in", e);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "", "",
                    "IOException Occurred while logging in", e);
        }
    }
}
