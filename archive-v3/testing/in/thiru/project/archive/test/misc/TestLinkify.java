package in.thiru.project.archive.test.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class TestLinkify extends TestCase {

    private String linkify(String content) {
        String linkified = content;
        String r =
                "http(s)?://([\\w+?\\.\\w+])+([a-zA-Z0-9\\~\\!\\@\\#\\$\\%\\^\\&amp;\\*\\(\\)_\\-\\=\\+\\\\\\/\\?\\.\\:\\;\\'\\,]*)?";
        Pattern pattern =
                Pattern.compile(r, Pattern.DOTALL | Pattern.UNIX_LINES
                                   | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        linkified = matcher.replaceAll("<a href=\"$0\">$0</a>");
        return linkified;
    }

    public void testLinkify() {
        assertEquals(linkify("http://ponniyinselvan.in"),
                "<a href=\"http://ponniyinselvan.in\">http://ponniyinselvan.in</a>");
    }
}
