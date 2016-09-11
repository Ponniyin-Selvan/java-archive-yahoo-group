/*
 * Copyright (c) 2008-2010, Petr Panteleyev
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
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

class WordPressAPI {
    /**
     *
     * @param xmlRpc
     * @param userName
     * @param password
     * @param blog
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<MetaWeblogEntry> getPages(String xmlRpc, String userName, String password, Blog blog) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object[] params = new Object[] {
                blog.getId(),
                userName,
                password
            };

            Object[] result = (Object[])client.execute("wp.getPages", params);
            ArrayList<MetaWeblogEntry> res = new ArrayList<MetaWeblogEntry>();
            for (Object map : result) {
                WordPressPage page = new WordPressPage(blog);
                page.parse((HashMap<String,Object>)map);
                res.add(page);
            }
            return res;
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
     * @param blogId
     * @param category
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void newCategory(String xmlRpc, String login, String password, String blogId, String category) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(xmlRpc));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object[] params = new Object[] {
                blogId,
                login,
                password,
                category
            };

            Object[] result = (Object[])client.execute("wp.newCategory", params);
            for (Object map : result) {
            }

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
     * @param page
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void getPage(String xmlRpc, String login, String password, Blog blog, WordPressPage page) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(xmlRpc));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object[] params = new Object[] {
                blog.getId(),
                page.getId(),
                login,
                password
            };

            Object[] result = (Object[])client.execute("wp.getPage", params);
            for (Object map : result) {
            }

        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    /**
     * Deletes a page.
     * @param xmlRpc
     * @param login
     * @param password
     * @param blog
     * @param page
     * @return true if successful or false otherwise
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static boolean deletePage(String xmlRpc, String login, String password, MetaWeblogEntry page) throws BlogAPIException {
        try {
            Blog blog = page.getBlog();
            if (blog == null) {
                throw new IllegalStateException();
            }
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                page.getId()
            };

            Boolean result = (Boolean)client.execute("wp.deletePage", params);
            return result;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static boolean editPage(String xmlRpc, String login, String password, WordPressPage page) throws BlogAPIException {
        try {
            Blog blog = page.getBlog();
            if (blog == null) {
                throw new IllegalStateException();
            }
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object content = page.getContent();
            if (!(content instanceof HashMap)) {
                throw new IllegalStateException();
            }

            Object[] params = new Object[] {
                blog.getId(),
                page.getId(),
                login,
                password,
                content,
                Boolean.valueOf(!page.isDraft())
            };

            Boolean result = (Boolean)client.execute("wp.editPage", params);
            return result;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<WordPressComment> getComments(
            String xmlRpc,
            String login,
            String password,
            Blog blog,
            BlogEntry entry,
            WordPressCommentStatus status,
            int offset,
            int number) throws BlogAPIException {
        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            HashMap<String,Object> struct = new HashMap<String,Object>();
            if (entry != null) {
                struct.put("post_id", Integer.parseInt(entry.getId()));
            }
            if (status != null) {
                struct.put("status", status.toString());
            }
            struct.put("offset", offset);
            struct.put("number", number);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                struct
            };

            Object[] result = (Object[])client.execute("wp.getComments", params);
            ArrayList<WordPressComment> array = new ArrayList<WordPressComment>(result.length);

            for (Object r : result) {
                WordPressComment comment = new WordPressComment((HashMap<String,Object>)r);
                array.add(comment);
            }

            return array;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static HashMap getCommentStatusList(
            String xmlRpc,
            String login,
            String password,
            Blog blog) throws BlogAPIException {
     
        
        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password
            };

            return (HashMap)client.execute("wp.getCommentStatusList", params);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<WordPressTag> getTags(
            String xmlRpc,
            String login,
            String password,
            Blog blog) throws BlogAPIException {

        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password
            };

            Object[] result = (Object[])client.execute("wp.getTags", params);
            ArrayList<WordPressTag> array = new ArrayList<WordPressTag>(result.length);

            for (Object r : result) {
                WordPressTag tag = new WordPressTag((HashMap<String,Object>)r);
                array.add(tag);
            }

            return array;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static WordPressCommentCount getCommentCount(
            String xmlRpc,
            String login,
            String password,
            BlogEntry entry) throws BlogAPIException {

        try {
            Blog blog = entry.getBlog();
            if (blog == null) {
                throw new IllegalStateException();
            }

            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                entry.getId()
            };

            HashMap<String,Object> map = (HashMap<String,Object>)client.execute("wp.getCommentCount", params);
            return new WordPressCommentCount(map);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static boolean deleteComment(
            String xmlRpc,
            String login,
            String password,
            Blog blog,
            WordPressComment comment) throws BlogAPIException {

        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                comment.getId()
            };

            return (Boolean)client.execute("wp.deleteComment", params);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static boolean editComment(
            String xmlRpc,
            String login,
            String password,
            Blog blog,
            WordPressComment comment) throws BlogAPIException {

        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            HashMap<String,Object> struct = new HashMap<String,Object>();
            struct.put("status", comment.getStatus().toString());
            struct.put("date_created_gmt", comment.getDateCreated());
            struct.put("content", comment.getContent());
            struct.put("author", comment.getAuthor());
            struct.put("author_url", comment.getAuthorUrl());
            struct.put("author_email", comment.getAuthorEmail());

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                comment.getId(),
                struct
            };

            return (Boolean)client.execute("wp.editComment", params);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    public static int newComment(
            String xmlRpc,
            String login,
            String password,
            Blog blog,
            String entryId,
            WordPressComment comment,
            int parentId) throws BlogAPIException {

        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            HashMap<String,Object> struct = new HashMap<String,Object>();
            struct.put("comment_parent", parentId);
            struct.put("content", comment.getContent());
            struct.put("author", comment.getAuthor());
            struct.put("author_url", comment.getAuthorUrl());
            struct.put("author_email", comment.getAuthorEmail());

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password,
                entryId,
                struct
            };

            return (Integer)client.execute("wp.newComment", params);
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<WordPressAuthor> getAuthors(
            String xmlRpc,
            String login,
            String password,
            Blog blog) throws BlogAPIException {

        try {
            XmlRpcClient client = BlogAPIUtil.setClient(xmlRpc, blog);

            Object[] params = new Object[] {
                blog.getId(),
                login,
                password
            };

            Object[] result = (Object [])client.execute("wp.getAuthors", params);
            ArrayList<WordPressAuthor> array = new ArrayList<WordPressAuthor>(result.length);

            for (Object r : result) {
                array.add(new WordPressAuthor((HashMap<String,Object>)r));
            }

            return array;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }
}