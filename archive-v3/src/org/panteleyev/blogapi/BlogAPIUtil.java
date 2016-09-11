/*
 * Copyright (c) 2010, Petr Panteleyev
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.panteleyev.blogapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author peterp
 */
class BlogAPIUtil {
    static XmlRpcClient setClient(String xmlRpc, Blog blog) throws MalformedURLException {
        return setClient(xmlRpc, blog, true);
    }

    static XmlRpcClient setClient(String xmlRpc, Blog blog, boolean adjustTz) throws MalformedURLException {
        if (blog == null) {
            throw new NullPointerException();
        }

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        if (adjustTz) {
            config.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        String url = blog.getXmlrpc();
        if (url == null) {
            url = xmlRpc;
        }
        config.setServerURL(new URL(url));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        return client;
    }
}
