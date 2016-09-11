package in.thiru.project.archive.store;

import in.thiru.project.archive.MessageDetails;

public interface Store extends Iterable<MessageDetails> {

    void open() throws StoreException;

    String getLastMessageKey();

    void addMessage(MessageDetails message) throws StoreException;

    void close() throws StoreException;
}
