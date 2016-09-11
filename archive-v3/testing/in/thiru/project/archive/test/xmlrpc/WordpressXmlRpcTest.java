package in.thiru.project.archive.test.xmlrpc;

import in.thiru.project.archive.xmlrpc.MessageEntry;

import org.panteleyev.blogapi.Blog;
import org.panteleyev.blogapi.WordPressAccount;

public class WordpressXmlRpcTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        WordPressAccount wordPressAccount =
                new WordPressAccount("http://beta.ponniyinselvan.in/xmlrpc.php");
        Blog blog = new Blog();
        try {
            wordPressAccount.login("thirumalaikv", "humble1234");
            blog = wordPressAccount.getBlog(0);
            MessageEntry blogEntry = new MessageEntry(blog);

            blogEntry.setBody("Update through xmlrpc");
            blogEntry.setSubject("Update through xmlrpc");
            blogEntry.setAuthor("thiruxxxabcx");
            blogEntry.setCreateAuthor(true);
            blogEntry.setMessageKey("52");
            // List<WordPressAuthor> authors =
            // wordPressAccount.getAuthors(blog);
            // blogEntry.setUserId(1); //authors.get(0).getId());
            wordPressAccount.createEntry(blogEntry);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
