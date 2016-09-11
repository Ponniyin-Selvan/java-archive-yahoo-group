package in.thiru.project.archive.test.processor;

import in.thiru.project.archive.formatter.Formatter;
import in.thiru.project.archive.formatter.impl.AcronymFormatter;
import in.thiru.project.archive.formatter.impl.EmailObfuscator;
import in.thiru.project.archive.formatter.impl.HtmlSanitizer;
import in.thiru.project.archive.formatter.impl.MultiFormatter;
import in.thiru.project.archive.formatter.impl.NewLineFormatter;
import in.thiru.project.archive.formatter.impl.SubjectFormatter;
import in.thiru.project.archive.mail.ArchiveMessage;
import in.thiru.project.archive.processor.impl.BatchProcessor;
import in.thiru.project.archive.processor.impl.ExtractMessageDetails;
import in.thiru.project.archive.processor.impl.PickHtmlContent;
import in.thiru.project.archive.processor.impl.ProcessContent;
import in.thiru.project.archive.test.store.yahoo.YahooTestCase;
import in.thiru.project.archive.util.MimeTypeUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MailDateFormat;

public class ProcessingTest extends YahooTestCase {

    public void testExtractMessage() throws UnsupportedEncodingException,
            MessagingException, ParseException {

        Map<String, Object> details = new HashMap<String, Object>();
        ExtractMessageDetails processor = new ExtractMessageDetails();
        processor.setSubjectFormatter(new SubjectFormatter());

        ArchiveMessage message =
                getMimeMessage("test-data/test-003-html-mail-34.msg");
        processor.processMessage(message, details);
        assertEquals(details.get("from.email").toString(),
                "anandc272@yahoo.com");
        assertEquals(details.get("from.name").toString(), "Anand Kumar");
        assertEquals(details.get("member").toString(), "anandc272");
        assertEquals(details.get("subject").toString(), "Characters");
        DateFormat formatter = new MailDateFormat();
        assertEquals(details.get("sentOn").toString(), formatter.parse(
                "Mon, 27 May 2002 10:16:38 -0700 (PDT)").toString());

        message = getMimeMessage("test-data/test-003-utf-8-broken-tamil.msg");
        processor.processMessage(message, details);
        assertEquals(details.get("from.email").toString(),
                "sampath_srinivasaraghavan@yahoo.co.in");
        assertEquals(details.get("from.name").toString(), "ssampath108");
        assertEquals(details.get("member").toString(), "ssampath108");
        assertEquals(
                details.get("subject").toString(),
                "[Poetry In Stone|கல்லிலே கலைவண்ணம் கண்டோம்] The moat around the Tanjore Big temple | தஞ்சை பெரிய கோயில் அகழி");

        assertEquals(details.get("sentOn").toString(), formatter.parse(
                "Fri, 06 Nov 2009 11:34:35 -0000").toString());

    }

    @SuppressWarnings("unchecked")
    public void testPickHtmlMessage() throws MessagingException, IOException {
        Map<String, Object> details = new HashMap<String, Object>();
        ExtractMessageDetails extractProcessor = new ExtractMessageDetails();
        extractProcessor.setSubjectFormatter(new SubjectFormatter());

        BatchProcessor processor = new BatchProcessor();
        processor.addProcessor(extractProcessor);
        processor.addProcessor(new PickHtmlContent());

        ArchiveMessage message = getMimeMessage("test-data/test-001.msg");
        processor.processMessage(message, details);
        List<Part> contentParts = (List<Part>)details.get("content");

        // this message doesn't have html content, should default to text/plain
        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, "text/plain");
        assertEquals(parts.get("text/plain").get(0).toString(),
                contentParts.get(0).toString());

        message = getMimeMessage("test-data/test-003-html-mail-34.msg");
        processor.processMessage(message, details);
        contentParts = (List<Part>)details.get("content");

        // this message has html content, should pick that one
        parts = MimeTypeUtil.findMimeTypes(message, "text/html");
        assertEquals(parts.get("text/html").get(0).toString(),
                contentParts.get(0).toString());
    }

    public void testProcessMessage() throws MessagingException, IOException {

        Map<String, Object> details = new HashMap<String, Object>();
        ExtractMessageDetails extractProcessor = new ExtractMessageDetails();
        extractProcessor.setSubjectFormatter(new SubjectFormatter());

        MultiFormatter htmlFormatter = new MultiFormatter();
        MultiFormatter plainFormatter = new MultiFormatter();

        Formatter emailObfuscator = new EmailObfuscator();

        htmlFormatter.addFormatter(emailObfuscator);

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(
                "acronym-ponniyinselvan.properties"));
        Formatter acronymFormatter = new AcronymFormatter(properties);
        
        htmlFormatter.addFormatter(acronymFormatter);

        Properties htmlProperties = new Properties();
        htmlProperties.load(getClass().getClassLoader().getResourceAsStream(
                "html-sanitizer.properties"));
        Formatter htmlSanitizer = new HtmlSanitizer(htmlProperties);
        htmlFormatter.addFormatter(htmlSanitizer);

        plainFormatter.addFormatter(new NewLineFormatter());
        plainFormatter.addFormatter(emailObfuscator);
        plainFormatter.addFormatter(acronymFormatter);
        plainFormatter.addFormatter(htmlSanitizer);

        ProcessContent processContent = new ProcessContent();
        processContent.setHtmlContentFormatter(htmlFormatter);

        ProcessContent processMessage = new ProcessContent();
        processMessage.setHtmlContentFormatter(htmlFormatter);
        processMessage.setPlainContentFormatter(plainFormatter);

        BatchProcessor processor = new BatchProcessor();
        processor.addProcessor(extractProcessor);
        processor.addProcessor(new PickHtmlContent());
        processor.addProcessor(processMessage);

        ArchiveMessage message = getMimeMessage("test-data/test-001.msg");
        processor.processMessage(message, details);
        String formattedContent = (String)details.get("formatted-content");

        assertNotNull(formattedContent);

        assertEquals(
                "I have read ponniyin selvan quite a few times but I think Kalki has<br>confused a bit.......probably sliped a bit when he was walking <br>between the thin line of reality and Imagination.......<br>In one perspective we can conclude that<br>Nandini&apos;s father is the Pandiya king who  took Oomai<br>raani with him when she as unconcious...the pandia king who died <br>in the battle with Karikalan is his son....so nandini is his <br>sister...<br>In another perspective.......<br>i gathered that nandini and mathurandakan were born to oomai rani, <br>but still it is not clear about who their father. most <br>presumably<br>it was Pandiya king. but then again kalki raises a doubt because <br>&quot;how can nandini refer to her father as her lover&quot; and in her <br>hallucinations, nandini refers to the pandiya king as <br>&quot;anbe&quot;........<br>Not only me lot many people in the world are confused abt. <br>If someone could throw some light on this I would be grateful.<br>endrendrum anbudan,<br>Ram<br>",
                formattedContent);

        message = getMimeMessage("test-data/test-003-html-mail-34.msg");
        processor.processMessage(message, details);
        formattedContent = (String)details.get("formatted-content");
        String utf8Content = getMessage("test-data/formatted-content-34.txt");

        assertEquals(utf8Content, formattedContent);
    }
}
