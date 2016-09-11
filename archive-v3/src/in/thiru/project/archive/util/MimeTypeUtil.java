package in.thiru.project.archive.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public class MimeTypeUtil {

    Logger log = Logger.getLogger(MimeTypeUtil.class.getName());

    public static Map<String, List<Part>> findMimeTypes(Part p,
            String... mimeTypes) {

        Map<String, List<Part>> parts = new HashMap<String, List<Part>>();
        findMimeTypesHelper(p, parts, mimeTypes);
        return parts;
    }

    // a little recursive helper function that actually does all the work.
    public static void findMimeTypesHelper(Part p,
            Map<String, List<Part>> parts, String... mimeTypes) {
        try {
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    findContentTypesHelper(mp.getBodyPart(i), parts, mimeTypes);
                }
            } else {
                for (String mimeType : mimeTypes) {
                    if (p.isMimeType(mimeType)) {
                        addPart(mimeType, p, parts);
                        //parts.put(mimeType, p);
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();// FIXME
        } catch (MessagingException e) {
            e.printStackTrace();// FIXME
        } catch (IOException e) {
            e.printStackTrace();// FIXME
        }
    }

    private static void addPart(String mimeType, Part part, Map<String, List<Part>> contentTypes) {
        List<Part> parts = null;
        
        if (contentTypes.containsKey(mimeType)) {
            parts = contentTypes.get(mimeType);
        } else {
            parts = new ArrayList<Part>();
            contentTypes.put(mimeType, parts);
        }
        parts.add(part);
    }

    private static void findContentTypesHelper(Part p,
            Map<String, List<Part>> contentTypes, String... mimeTypes)
            throws MessagingException, IOException {
        try {
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                for (int i = 0; mp != null && i < mp.getCount(); i++) {
                    findContentTypesHelper(mp.getBodyPart(i), contentTypes,
                            mimeTypes);
                }
            } else {
                for (String mimeType : mimeTypes) {
                    if (p.isMimeType(mimeType)) {
                        addPart(mimeType, p, contentTypes);
                        //contentTypes.put(mimeType, p);
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace(); // FIXME
        }
    }
}
