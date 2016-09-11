/*
 * Copyright (c) 2008-2010, Petr Panteleyev All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.panteleyev.blogapi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.panteleyev.utilities.FileUtil;
import org.panteleyev.utilities.MimeTypes;

public class MetaWeblogAPI {
    private static final MimeTypes mimeTypes = new MimeTypes();

    /**
     * 
     * @param xmlRpc
     * @param login
     * @param password
     * @param entry
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void getPost(String xmlRpc, String login, String password,
            BlogEntry entry) throws BlogAPIException {
        try {
            XmlRpcClient client =
                    BlogAPIUtil.setClient(xmlRpc, entry.getBlog());

            Object[] params = new Object[] {entry.getId(), login, password};

            Object result =
                    (Object)client.execute("metaWeblog.getPost", params);
            int a = 10;

        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    /**
     * 
     * @param xmlRpc
     * @param login
     * @param password
     * @param blog
     * @param number
     * @return
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<MetaWeblogEntry> getRecentPosts(String xmlRpc,
            String login, String password, Blog blog, int number)
            throws BlogAPIException {
        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog, false);

            Object[] params =
                    new Object[] {blog.getId(), login, password, number};

            Object[] result =
                    (Object[])client.execute("metaWeblog.getRecentPosts",
                            params);
            ArrayList<MetaWeblogEntry> entries =
                    new ArrayList<MetaWeblogEntry>(result.length);
            for (Object map : result) {
                MetaWeblogEntry entry = new MetaWeblogEntry(blog);
                entry.parse((HashMap<String, Object>)map);
                entries.add(entry);
            }

            return entries;

        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    /**
     * 
     * @param xmlRpc
     * @param login
     * @param password
     * @param entry
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void newPost(String xmlRpc, String login, String password,
            BlogEntry entry) throws BlogAPIException {
        try {
            Blog blog = entry.getBlog();
            if (blog == null) {
                throw new IllegalStateException();
            }

            XmlRpcClient client =
                    BlogAPIUtil.setClient(xmlRpc, entry.getBlog());
            XmlRpcClientConfigImpl config =
                    (XmlRpcClientConfigImpl)client.getConfig(); // FIXME
            config.setEnabledForExtensions(true);
            Object content = entry.getContent();
            if (!(content instanceof HashMap)) {
                throw new IllegalStateException();
            }

            Object[] params =
                    new Object[] {blog.getId(), login, password, content,
                                  Boolean.valueOf(!entry.isDraft())};

            // FIXME - How to pass this method
            String result = (String)client.execute("archive.message", params);
            entry.setId(result);
        } catch (XmlRpcException ex) {
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static void editPost(String xmlRpc, String login, String password,
            BlogEntry entry) throws BlogAPIException {
        try {
            Blog blog = entry.getBlog();
            if (blog == null) {
                throw new IllegalStateException();
            }

            XmlRpcClient client =
                    BlogAPIUtil.setClient(xmlRpc, entry.getBlog());

            Object content = entry.getContent();
            if (!(content instanceof HashMap)) {
                throw new IllegalStateException();
            }

            Object[] params =
                    new Object[] {entry.getId(), login, password, content,
                                  Boolean.valueOf(!entry.isDraft())};

            Boolean result =
                    (Boolean)client.execute("metaWeblog.editPost", params);
            if (!result) {
                throw new BlogAPIException("Failed for uknown reason");
            }
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static String newMediaObject(String xmlRpc, String login,
            String password, Blog blog, File media) throws BlogAPIException {
        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            byte[] buf = FileUtil.getBytesFromFile(media);
            if (buf == null) {
                throw new BlogAPIException();
            }

            HashMap<String, Object> content = new HashMap<String, Object>(3);
            content.put("name", media.getName());
            content.put("bits", buf);
            content.put("type", mimeTypes.getContentType(media));

            Object[] params =
                    new Object[] {blog.getId(), login, password, content};

            Object result = client.execute("metaWeblog.newMediaObject", params);
            if ((result == null) || !(result instanceof HashMap)) {
                return null;
            } else {
                return (String)((HashMap<String, Object>)result).get("url");
            }
        } catch (IOException ex) {
            throw new BlogAPIException(ex);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static MetaWeblogEntry createBlogEntryObject(Blog blog) {
        return new MetaWeblogEntry(blog);
    }
}