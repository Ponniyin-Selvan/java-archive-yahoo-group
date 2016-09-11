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
package net.fortuna.mstor.data.xml;

import org.jdom.Namespace;

/**
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id: XmlBinding.java,v 1.2 2009/03/05 06:04:58 fortuna Exp $
 *
 * Created on 6/05/2006
 * </pre>
 * 
 */
public abstract class XmlBinding {

    // private static final Namespace DEFAULT_NAMESPACE = Namespace.NO_NAMESPACE;
    private static final Namespace DEFAULT_NAMESPACE = Namespace.getNamespace(
            "mstor", "http://modularity.net.au/mstor");

    protected Namespace namespace;

    /**
     * Default constructor.
     */
    public XmlBinding() {
        this(DEFAULT_NAMESPACE);
    }

    /**
     * @param namespace
     */
    public XmlBinding(final Namespace namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the namespace
     */
    public final Namespace getNamespace() {
        return namespace;
    }
}