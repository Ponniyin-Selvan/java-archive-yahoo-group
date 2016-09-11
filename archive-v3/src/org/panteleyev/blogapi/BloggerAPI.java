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

class BloggerAPI {
    public static final String APPID = "PetrusBlogger";
    
    private static final XmlRpcClient client = new XmlRpcClient();
        
    @SuppressWarnings("unchecked")
    public static ArrayList<Blog> getUserBlogs(String xmlRpc, String login, String password) throws BlogAPIException {
        try {
            ArrayList<Blog> blogs = new ArrayList<Blog>();

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(xmlRpc));
            
            Object[] params = new Object[] { 
                APPID,
                login,
                password
            };
            
            Object[] result = (Object[])client.execute(config, "blogger.getUsersBlogs", params);
            for (Object map : result) {
                Blog blog = new Blog();
                blog.parse((HashMap<String,Object>)map);
                blogs.add(blog);
            }

            return blogs;
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
     * @param entry
     * @param draft
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void newPost(String xmlRpc, String login, String password, Blog blog, BlogEntry entry, boolean draft) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                APPID, 
                blog.getId(), 
                login, 
                password,
                entry.getContent(),
                Boolean.valueOf(!draft)
            };
            
            Integer result = (Integer)client.execute(config, "blogger.newPost", params);
            entry.setId(result.toString());
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
    public static void getPost(String xmlRpc, String login, String password, BlogEntry entry) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = entry.getBlog().getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                APPID, 
                entry.getId(),
                login, 
                password
            };
            
            Object result = client.execute(config, "blogger.getPost", params);
//            entry.setId(result);
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
     * @param draft
     * @throws org.panteleyev.blogapi.BlogAPIException
     */
    public static void editPost(String xmlRpc, String login, String password, BlogEntry entry, boolean draft) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = entry.getBlog().getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                APPID, 
                entry.getId(), 
                login, 
                password,
                entry.getContent(),
                Boolean.valueOf(!draft)
            };
            
            Object result = client.execute(config, "blogger.editPost", params);
//            entry.setId(result);
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
    public static void deletePost(String xmlRpc, String login, String password, BlogEntry entry) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = entry.getBlog().getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                APPID, 
                entry.getId(), 
                login, 
                password,
                Boolean.valueOf(!entry.isDraft())
            };
            
            Object result = client.execute(config, "blogger.deletePost", params);
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
    public static ArrayList<BlogEntry> getRecentPosts(String xmlRpc, String login, String password, Blog blog, Integer number) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                APPID, 
                blog.getId(), 
                login, 
                password,
                number
            };
            
            Object[] result = (Object[])client.execute(config, "blogger.getRecentPosts", params);
            
            ArrayList<BlogEntry> entries = new ArrayList<BlogEntry>(result.length);
            for (Object map : result) {
                Entry entry = new Entry(blog);
                entry.parse((HashMap<String,Object>)map);
                entries.add(entry);                
            }
            
            return entries;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }
        
    @SuppressWarnings("unchecked")
    public static ArrayList<Category> getCategoryList(String xmlRpc, String login, String password, Blog blog) throws BlogAPIException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = blog.getXmlrpc();
            if (url == null) {
                url = xmlRpc;
            }            
            config.setServerURL(new URL(url));
            
            Object[] params = new Object[] { 
                blog.getId(), 
                login, 
                password,
            };
            
            Object[] result = (Object[])client.execute(config, "mt.getCategoryList", params);
            
            ArrayList<Category> categories = new ArrayList<Category>(result.length);
            for (Object map : result) {
                Category category = Category.parse((HashMap<String,Object>)map);
                categories.add(category);                
            }
            
            return categories;
        } catch (XmlRpcException ex) {
            throw new BlogAPIException(ex);
        } catch (MalformedURLException ex) {
            throw new BlogAPIException(ex);
        }
    }
}