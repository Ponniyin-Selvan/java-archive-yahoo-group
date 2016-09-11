package in.thiru.project.archive.html;

public class HtmlParserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HtmlParserException(String message) {
        super(message);
    }

    public HtmlParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
