package in.thiru.project.archive.test.xmlrpc.drupal;

import java.util.Date;
import java.util.Vector;

import com.sun.net.ssl.internal.ssl.Debug;

/**
 * Example showing how the Drupal XML-RPC Service could be used.
 * 
 * @author sauermann
 * 
 */
public class DrupalXmlRpcServiceExample {
    
    /**
     * Testing parameters
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DrupalXmlRpcService service = new DrupalXmlRpcService(
                "aperture",
                "442c5629267cc4568ad43ceaa7f3dbe4",
                "http://beta.ponniyinselvan.in/xmlrpc");
        service.connect();
        service.login("root", "root");
        DrupalNode node = new DrupalNode();
        node.setType(DrupalNode.TYPE_STORY);
        node.setTitle("HEllo WORLD");
        node.setBody("at "+new Date().toGMTString());
        service.nodeSave(node);
        
        // test to upload a file as "string"
        String myfile="Hello World. This is a textfile";
        service.fileSave(myfile.getBytes());

        service.logout();
        System.out.println("done");

    }


}