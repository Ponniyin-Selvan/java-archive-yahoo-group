package in.thiru.project.archive.test.format;

import in.thiru.project.archive.formatter.Formatter;
import in.thiru.project.archive.formatter.impl.AcronymFormatter;
import in.thiru.project.archive.formatter.impl.EmailObfuscator;
import in.thiru.project.archive.formatter.impl.HtmlSanitizer;
import in.thiru.project.archive.formatter.impl.MultiFormatter;
import in.thiru.project.archive.formatter.impl.NewLineFormatter;
import in.thiru.project.archive.formatter.impl.SubjectFormatter;
import in.thiru.project.archive.test.StoreTestCase;
import in.thiru.project.archive.util.MimeTypeUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;

public class FormattingTestCase extends StoreTestCase {

    public void testEmail() {
        String regEx = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        String content =
                "humble.groups@gmail.com Wrote\n\n xys dfa thiru@thiru.in Wrote";
        String expectedContent =
                "humble.groups@... Wrote\n\n xys dfa thiru@... Wrote";

        Matcher matcher =
                Pattern.compile(regEx, Pattern.MULTILINE | Pattern.UNICODE_CASE)
                        .matcher(content);

        StringBuffer emailContent = new StringBuffer();
        int lastIndex = 0;
        while (matcher.find()) {

            String email = matcher.group();
            String[] emailParts = email.split("@");
            String obfuscatedEmail = emailParts[0] + "@...";
            // System.out.println("Email found at text " + matcher.group()
            // + " starting at index " + matcher.start()
            // + "and ending at index " + matcher.end());

            int startIndex = matcher.start();

            emailContent.append(content.substring(lastIndex, startIndex));
            emailContent.append(obfuscatedEmail);
            System.out.println(emailContent.toString());
            lastIndex = matcher.end();
        }
        emailContent.append(content.substring(lastIndex));

        assertEquals(expectedContent, emailContent.toString());

        Formatter formatter = new EmailObfuscator();
        assertEquals(expectedContent, formatter.format(content));
    }

    public void testAcronym() throws IOException {
        String regEx = "(?!<[^<>]*?)(?<![?./&])\\bRJC\\b(?!:)(?![^<>]*?>)";
        String replaceBy = "<acronym title='Rajendra Cholan'>RJC</acronym>";
        String content = "This is\n RJC";
        String expectedContent =
                "This is\n <acronym title='Rajendra Cholan'>RJC</acronym>";

        String acronymedContent =
                Pattern.compile(regEx, Pattern.MULTILINE | Pattern.UNICODE_CASE)
                        .matcher(content)
                        .replaceAll(replaceBy);

        System.out.println(acronymedContent);
        assertEquals(expectedContent, acronymedContent);
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(
                "acronym-ponniyinselvan.properties"));
        // properties.setProperty("RJC", "Rajendra Cholan");

        Formatter formatter = new AcronymFormatter(properties);
        acronymedContent = formatter.format(content);
        System.out.println(acronymedContent);
        assertEquals(expectedContent, acronymedContent);
    }

    public void testNewLineToBr() {
        String source = "this is a test\nwith multiple\nlines\n";
        String target = "this is a test<br />with multiple<br />lines<br />";
        Formatter formatter = new NewLineFormatter();
        String formatted = formatter.format(source);
        assertEquals(target, formatted);
    }

    public void testFixSubject() {
        String[] removeWords = {"re:", "fw:", "fwd:", "\\[ponniyinselvan\\]", "\\[[0-9]+ Attachments\\]"};
        String content = "RE: FW: [ponniyinselvan] FWD: This is test subject [2 Attachments]";
        String expectedContent = "This is test subject";
        String fixedSubject = content;

        for (String word : removeWords) {
            fixedSubject =
                    Pattern.compile(word, Pattern.CASE_INSENSITIVE).matcher(
                            fixedSubject).replaceAll("");
        }
        fixedSubject = fixedSubject.trim();
        System.out.println(fixedSubject);
        assertEquals(expectedContent, fixedSubject);

        SubjectFormatter formatter = new SubjectFormatter();
        formatter.setRemoveWords(removeWords);
        assertEquals(expectedContent, formatter.format(content));
    }

    public void testHtmlSanitizer() throws MessagingException, ParseException,
            IOException {

        String utf8Content =
                getMessage("test-data/html-sanitizer-output-34.txt");
        Message message = getMimeMessage("test-data/test-003-html-mail-34.msg");
        Map<String, List<Part>> parts =
                MimeTypeUtil.findMimeTypes(message, "text/plain", "text/html");
        List<Part> plainPart = parts.get("text/html");
        String content =
                new String(plainPart.get(0).getContent().toString().getBytes(
                        "iso-8859-1"), "UTF-8");
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(
                "html-sanitizer.properties"));
        Formatter formatter = new HtmlSanitizer(properties);
        content = formatter.format(content.trim());
        System.out.println(content);
        String utf8Hex =
                String.format("%x", new BigInteger(1,
                        utf8Content.getBytes("utf-8")));
        String contentHex =
                String.format("%x",
                        new BigInteger(1, content.getBytes("utf-8")));

        assertEquals(utf8Hex, contentHex);
    }

    public void testMultiFormatter() throws IOException {
        String content =
                "humble.groups@gmail.com Wrote\n\n RJC dfa thiru@thiru.in Wrote";
        String expectedContent =
                "humble.groups@... Wrote<br><br> <acronym title=\"Rajendra Cholan\">RJC</acronym> dfa thiru@... Wrote";

        MultiFormatter formatter = new MultiFormatter();

        formatter.addFormatter(new NewLineFormatter());
        formatter.addFormatter(new EmailObfuscator());

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(
                "acronym-ponniyinselvan.properties"));
        formatter.addFormatter(new AcronymFormatter(properties));

        Properties htmlProperties = new Properties();
        htmlProperties.load(getClass().getClassLoader().getResourceAsStream(
                "html-sanitizer.properties"));
        formatter.addFormatter(new HtmlSanitizer(htmlProperties));

        String formattedContent = formatter.format(content);
        assertEquals(expectedContent, formattedContent);
    }
}
