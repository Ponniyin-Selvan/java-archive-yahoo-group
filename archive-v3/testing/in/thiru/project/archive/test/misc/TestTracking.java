package in.thiru.project.archive.test.misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class TestTracking {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Base64Encoder encoder = new Base64Encoder();
        String track = "ponniyinselvan&12232343&110&mbox";
        try {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
            bufos.write( track.getBytes() );
            bufos.close();
            byte[] retval= bos.toByteArray();
            bos.close();
            
            String encodedTrack = encoder.encode(retval);
            System.out.println(encodedTrack);
            
            
            ByteArrayInputStream bis = new ByteArrayInputStream(encoder.decode(encodedTrack));
            BufferedInputStream bufis = new BufferedInputStream(new GZIPInputStream(bis));
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while( (len = bufis.read(buf)) > 0 )
            {
              bos.write(buf, 0, len);
            }
            String decodedTrack = bos.toString();
            bis.close();
            bufis.close();
            bos.close();
            
            System.out.println(encodedTrack);
            System.out.println(decodedTrack);

            System.out.println(encoder.encode(track.getBytes()));
            System.out.println(new String(encoder.decode(encoder.encode(track.getBytes()))));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
