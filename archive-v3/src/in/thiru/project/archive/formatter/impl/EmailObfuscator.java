package in.thiru.project.archive.formatter.impl;

import in.thiru.project.archive.formatter.AbstractFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailObfuscator extends AbstractFormatter {

    public String obfuscate(String email) {
        String[] emailParts = email.split("@");
        return emailParts[0] + "@...";
    }
    
    @Override
    public String format(String source) {
        String regEx = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        Matcher matcher =
                Pattern.compile(regEx, Pattern.MULTILINE | Pattern.UNICODE_CASE)
                        .matcher(source);

        StringBuffer obfuscatedContent = new StringBuffer();
        int lastIndex = 0;
        while (matcher.find()) {

            String email = matcher.group();
            String obfuscatedEmail = obfuscate(email);
            // System.out.println("Email found at text " + matcher.group()
            // + " starting at index " + matcher.start()
            // + "and ending at index " + matcher.end());

            int startIndex = matcher.start();

            obfuscatedContent.append(source.substring(lastIndex, startIndex));
            obfuscatedContent.append(obfuscatedEmail);
            //System.out.println(obfuscatedContent.toString());
            lastIndex = matcher.end();
        }
        obfuscatedContent.append(source.substring(lastIndex));
        return obfuscatedContent.toString();
    }

}
