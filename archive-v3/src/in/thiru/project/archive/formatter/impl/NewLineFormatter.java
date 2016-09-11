package in.thiru.project.archive.formatter.impl;

import in.thiru.project.archive.formatter.AbstractFormatter;

public class NewLineFormatter extends AbstractFormatter {

    private String newLineFromString = "\n";

    private String newLineToString = "<br />";

    public NewLineFormatter() {
    }

    public NewLineFormatter(String newLineFromString, String newLineToString) {
        this.newLineFromString = newLineFromString;
        this.newLineToString = newLineToString;
    }

    public String getNewLineToString() {
        return newLineToString;
    }

    public void setNewLineToString(String newLineString) {
        this.newLineToString = newLineString;
    }

    public String getNewLineFromString() {
        return newLineFromString;
    }

    public void setNewLineFromString(String newLineFromString) {
        this.newLineFromString = newLineFromString;
    }

    @Override
    public String format(String source) {
        return source.replaceAll(newLineFromString, newLineToString);
    }
}
