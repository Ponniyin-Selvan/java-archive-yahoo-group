/**
 * Copyright (c) 2009, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.connector;

import javax.mail.MessagingException;

/**
 * @param <T> the type of message delegate supported
 * 
 * @author Ben
 * 
 * <pre>
 * $Id: AbstractFolderDelegate.java,v 1.5 2009/06/13 09:42:18 fortuna Exp $
 *
 * Created on 21/07/2007
 * </pre>
 * 
 *
 */
public abstract class AbstractFolderDelegate<T extends MessageDelegate> implements FolderDelegate<T> {
    
    /**
     * {@inheritDoc}
     */
    public final long allocateUid(MessageDelegate message)
        throws UnsupportedOperationException, DelegateException {
        
    	synchronized (this) {
            long uid = getLastUid() + 1;
            message.setUid(uid);
            setLastUid(uid);

            return uid;
    	}
    }
    
    /**
     * @param uid the latest UID for the folder
     * @throws UnsupportedOperationException if this method is not supported in the folder delegate
     * @throws DelegateException where an error occurs updating the latest UID
     */
    protected abstract void setLastUid(long uid) throws UnsupportedOperationException, DelegateException;

    /**
     * @param messageNumber the message number to associate with the new message
     * @return a new message delegate instance
     * @throws DelegateException where an error occurs creating the delegate
     */
    protected abstract T createMessage(int messageNumber) throws DelegateException;

    /**
     * {@inheritDoc}
     */
    public int getDeletedMessageCount() throws MessagingException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Overriding implementation not supported");
    }
}