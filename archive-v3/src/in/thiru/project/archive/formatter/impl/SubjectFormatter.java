package in.thiru.project.archive.formatter.impl;

import java.util.regex.Pattern;

import in.thiru.project.archive.formatter.AbstractFormatter;

public class SubjectFormatter extends AbstractFormatter {

    private String[] removeWords = {"re:", "fw:", "fwd:"};

    public String[] getRemoveWords() {
        return removeWords;
    }

    public void setRemoveWords(String[] removeWords) {
        this.removeWords = removeWords;
    }

    @Override
    public String format(String source) {
        String fixedSubject = source;

        for (String word : this.removeWords) {
            fixedSubject =
                    Pattern.compile(word, Pattern.CASE_INSENSITIVE).matcher(
                            fixedSubject).replaceAll("");
        }
        return fixedSubject.trim();
    }

}
