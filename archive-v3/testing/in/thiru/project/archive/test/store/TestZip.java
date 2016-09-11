package in.thiru.project.archive.test.store;

import java.io.FileNotFoundException;

import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;

public class TestZip {

    /**
     * @param args
     * @throws FileNotFoundException 
     * @throws ArchiveException 
     */
    public static void main(String[] args) throws FileNotFoundException, ArchiveException {
        
        File file = new File("Archive.zip/.testclasspathx");
        file.copyFrom(new FileInputStream(".classpath"));
        //File.umount();
    }

}
