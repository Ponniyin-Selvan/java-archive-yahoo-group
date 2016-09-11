package in.thiru.project.archive.formatter.impl;

import in.thiru.project.archive.formatter.AbstractFormatter;
import in.thiru.project.archive.formatter.Formatter;

import java.util.ArrayList;
import java.util.List;

public class MultiFormatter extends AbstractFormatter {

    List<Formatter> formatterList = new ArrayList<Formatter>();

    public MultiFormatter() {
    }
    
    public MultiFormatter(List<Formatter> formatterList) {
        this.formatterList = formatterList;
    }
    
    public void addFormatter(Formatter formatter) {
        formatterList.add(formatter);
        
    }
    
    @Override
    public String format(String source) {
        String formattedContent = source;
        if (null != source) {
            for(Formatter formatter : formatterList) {
                formattedContent = formatter.format(formattedContent);
            }
        }
        return formattedContent;
    }

}
