/**
 * 
 */
package in.thiru.project.archive.store.impl;

import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.StoreException;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;

/**
 * @author VTHIRUM3
 * 
 */
public class ZipStoreImpl extends AbstractStore {

    Logger log = Logger.getLogger(ZipStoreImpl.class.getName());

    private String fileName;

    public ZipStoreImpl(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /*
     * @see in.thiru.project.archive.store.AbstractStore#close()
     */
    @Override
    public void close() throws StoreException {
        try {
            File.umount();
        } catch (ArchiveException e) {
            throw new StoreException("Couldn't unmount File", e);
        }
    }

    /*
     * @see in.thiru.project.archive.store.AbstractStore#open()
     */
    @Override
    public void open() throws StoreException {
    }

    /*
     * @see in.thiru.project.archive.store.Store#addMessage(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void addMessage(String messageKey, String message)
            throws StoreException {
        String messageFileName = "000000" + messageKey;
        messageFileName = messageFileName.substring(messageKey.length()) + ".msg";
        File file = new File(fileName + "/" + messageFileName);
        file.copyFrom(new ByteArrayInputStream(message.getBytes()));
        try {
            File.umount();
        } catch(Exception e) {
            log.logp(Level.SEVERE, "", "", "", e);
        }
    }

    /*
     * @see in.thiru.project.archive.store.Store#getLastMessageKey()
     */
    @Override
    public String getLastMessageKey() throws StoreException {
        String messageKey = null;
        
        File zipFile = new File(this.fileName);
        if (zipFile.exists()) {
            String[] files = zipFile.list();
            Arrays.sort(files);
            String messageName = files[files.length - 1];
            int loc = messageName.indexOf('.');
            messageKey = messageName.substring(0, loc);
        }
        
        return messageKey;
    }

}
