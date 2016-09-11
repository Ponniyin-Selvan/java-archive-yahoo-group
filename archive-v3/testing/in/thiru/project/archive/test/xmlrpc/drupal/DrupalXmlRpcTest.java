package in.thiru.project.archive.test.xmlrpc.drupal;

import in.thiru.project.archive.test.misc.CustomXmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;

public class DrupalXmlRpcTest {

    public static void main(String[] args) throws MalformedURLException,
            XmlRpcException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://beta.ponniyinselvan.in/xmlrpc.php"));

        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcSun15HttpTransportFactory(client));
        XmlRpcSun15HttpTransport transport =
                new XmlRpcSun15HttpTransport(client);
        client.setTransportFactory(new CustomXmlRpcCommonsTransportFactory(
                client));
        client.setConfig(config);
        Vector<Object> params = new Vector<Object>();
        params.add("forum");
        params.add("thirumalaikv");
        params.add("humble1234");
        Map<String, Object> content = new HashMap<String, Object>();
        content.put("author", "thirux");
        content.put("title", "Your xmlrpc first topicx");
        content.put("description", "Your first xmlrpc topic");
        content.put("dateCreated", new Date());
        content.put("forum_id", 107);
        content.put("message_key", 110);
        params.add(content);
        params.add(false);
        Object result = client.execute("archive.message", params);
        // Object result = client.execute("demo.sayHello", params);\

        System.out.println(result.toString());
    }
}
