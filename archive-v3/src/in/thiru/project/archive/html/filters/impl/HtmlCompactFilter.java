package in.thiru.project.archive.html.filters.impl;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.HTMLEntities;
import org.cyberneko.html.filters.Writer;

public class HtmlCompactFilter extends Writer {

    
    public HtmlCompactFilter() {
        super();
    }

    public HtmlCompactFilter(OutputStream outputStream, String encoding)
            throws UnsupportedEncodingException {
        super(outputStream, encoding);
    }

    public HtmlCompactFilter(java.io.Writer writer, String encoding) {
        super(writer, encoding);
    }

    @Override
    protected void printCharacters(XMLString text, boolean normalize) {
        if (normalize) {
            for (int i = 0; i < text.length; i++) {
                char c = text.ch[text.offset + i];
                if (c != '\n') {
                    String entity = HTMLEntities.get(c);
                    if (entity != null) {
                        printEntity(entity);
                    } else {
                        fPrinter.print(c);
                    }
                } else {
                    // fPrinter.println();
                }
            }
        } else {
            for (int i = 0; i < text.length; i++) {
                char c = text.ch[text.offset + i];
                fPrinter.print(c);
            }
        }
        fPrinter.flush();
    }

}
