package in.thiru.project.archive.test.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestHarness {

    public static void main(String[] args) {

        Pattern pattern = Pattern.compile("\\bRJC\\b");

        Matcher matcher = pattern.matcher("This is RJC ");

        boolean found = false;
        while (matcher.find()) {
            System.out.println("I found the text " + matcher.group()
                               + " starting at " + "index " + matcher.start()
                               + "and ending at index " + matcher.end());
            found = true;
        }
        if (!found) {
            System.out.println("No match found");
        }
    }
}