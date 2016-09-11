package in.thiru.project.archive.test.store.yahoo;

import in.thiru.project.archive.util.MimeTypeUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;

public class TestMessageAttributes extends YahooTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSentTimestamp() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message = getMimeMessage("test-data/test-001.msg");
        String messageSentOn = "Thu, 21 Mar 2002 01:19:35 -0000";
        DateFormat format = new MailDateFormat();
        Date sentOndDate = format.parse(messageSentOn);
        assertEquals(message.getSentDate(), sentOndDate);
    }

    public void testAuthor() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message = getMimeMessage("test-data/test-001.msg");
        assertEquals(((InternetAddress)message.getFrom()[0]).getAddress(),
                "ramchi412@yahoo.com");
    }

    public void testYahooId() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message = getMimeMessage("test-data/test-001.msg");
        assertEquals(message.getHeader("X-Yahoo-Profile")[0], "ramchi412");
    }

    public void testSubject() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message = getMimeMessage("test-data/test-001.msg");
        assertEquals(message.getSubject(), "hai");
    }

    public void testUtf8Subject() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message = getMimeMessage("test-data/test-002-utf-8.msg");
        assertEquals(message.getSubject(), "அழைக்கிறோம்...");
    }

    public void testBrokenUtf8Subject() throws UnsupportedEncodingException,
            MessagingException, ParseException {
        Message message =
                getMimeMessage("test-data/test-003-utf-8-broken-tamil.msg");
        String subject = message.getSubject();

        assertEquals(
                subject,
                "Re: Fwd: [Poetry In Stone|கல்லிலே கலைவண்ணம் கண்டோம்] The moat around the Tanjore Big temple | தஞ்சை பெரிய கோயில் அகழி");
    }

    public void testBrokenUtf8Content() throws MessagingException,
            ParseException, IOException {

        String utf8Content =
                getMessage("test-data/broken-utf-8-content-text-plain.txt");
        Message message =
                getMimeMessage("test-data/test-003-utf-8-broken-tamil.msg");
        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, "text/plain", "text/html");
        Part plainPart = parts.get("text/plain").get(0);
        String content =
                new String(plainPart.getContent().toString().getBytes(
                        "iso-8859-1"), "UTF-8");
        String utf8Hex =
                String.format("%x", new BigInteger(1,
                        utf8Content.getBytes("utf-8")));
        String contentHex =
                String.format("%x",
                        new BigInteger(1, content.getBytes("utf-8")));
        // utf 8 content will have first 6 chars as EF BB BF - need to skip
        assertEquals(utf8Hex.substring(6), contentHex);
    }
}
