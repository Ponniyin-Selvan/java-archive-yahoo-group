package in.thiru.project.archive.test.xmlrpc.drupal;

import java.util.List;

import org.panteleyev.blogapi.Blog;
import org.panteleyev.blogapi.BlogEntry;
import org.panteleyev.blogapi.MetaWeblogAPI;
import org.panteleyev.blogapi.MetaWeblogEntry;
import org.panteleyev.blogapi.WordPressAccount;
import org.panteleyev.blogapi.WordPressAuthor;

public class DrupalXmlRpcApiTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            BlogEntry blogEntry = new MetaWeblogEntry();
            
            blogEntry.setBody("Test through xmlrpc body");
            blogEntry.setSubject("Test through xmlrpc- subject");
            blogEntry.setUserId(1);

            MetaWeblogAPI.newPost("http://localhost/xmlrpc.php", "thiru", "Thiru123$", blogEntry);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
