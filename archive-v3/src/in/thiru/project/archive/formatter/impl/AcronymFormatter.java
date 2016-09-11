package in.thiru.project.archive.formatter.impl;

import in.thiru.project.archive.formatter.AbstractFormatter;

import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcronymFormatter extends AbstractFormatter {

    private Properties acronyms = new Properties();

    public AcronymFormatter() {
    }

    public AcronymFormatter(Properties acronyms) {
        this.acronyms = acronyms;
    }

    @Override
    public String format(String source) {
        String regEx =
                "(?!<[^<>]*?)(?<![?./&])\\b%acronym%\\b(?!:)(?![^<>]*?>)";
        String acronymedContent = source;

        for (Entry<?, ?> entry : acronyms.entrySet()) {

            String acronymRegEx =
                    regEx.replace("%acronym%", (String)entry.getKey());
            Matcher matcher =
                    Pattern.compile(acronymRegEx,
                            Pattern.MULTILINE | Pattern.UNICODE_CASE).matcher(
                            acronymedContent);

            if (matcher.find()) {
                acronymedContent =
                        matcher.replaceAll("<acronym title='"
                                           + (String)entry.getValue() + "'>"
                                           + (String)entry.getKey()
                                           + "</acronym>");
            }
        }
        return acronymedContent;
    }
}
