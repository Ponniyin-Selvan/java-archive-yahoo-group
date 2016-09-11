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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class WordPressAccount {
    private String xmlRpc;
    private String username;
    private String password;
    
    private ArrayList<Blog> blogs = new ArrayList<Blog>();
    private HashMap<String,ArrayList<WordPressTag>> tags;

    /*
    public WordPressAccount() {
        this.xmlRpc = WORDPRESSCOM;
    }
    
     */
    public WordPressAccount(String xmlRpc) {
        this.xmlRpc = xmlRpc;
    }

    public int getBlogsCount() {
        return (blogs == null)? 0 : blogs.size();
    }

    public ArrayList<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(ArrayList<Blog> blogs) {
        this.blogs = blogs;
    }

    public Blog getBlog(int index) {
        return (blogs != null && index >= 0 && index < blogs.size())? blogs.get(index) : null;
    }

    public Blog getBlog(String id) {
        if (id != null && blogs != null) {
            for (Blog b : blogs) {
                if (b.getId().equals(id)) {
                    return b;
                }
            }
        }
        return null;
    }
    
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getXmlRpc() {
        return xmlRpc;
    }

    public ArrayList<WordPressTag> getTags(Blog blog) {
        return tags.get(blog.getId());
    }

    public HashMap<String,ArrayList<WordPressTag>> getTags() {
        return tags;
    }
    
    public void setTags(HashMap<String,ArrayList<WordPressTag>> tags) {
        this.tags = tags;
    }

    public boolean addTags(Blog blog, ArrayList<String> labels) {
        boolean result = false;
        
        ArrayList<WordPressTag> blogTags = tags.get(blog.getId());
        if (blogTags != null) {
            for (String l : labels) {
                boolean found = false;
                for (WordPressTag t : blogTags) {
                    if (l.equals(t.getName())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    blogTags.add(new WordPressTag(l));
                    result = true;
                }
            }
        } else {
            blogTags = new ArrayList<WordPressTag>(labels.size());
            for (String l : labels) {
                blogTags.add(new WordPressTag(l));
            }
            tags.put(blog.getId(), blogTags);
            result = true;
        }

        return result;
    }


    /*
    public boolean addTags(ArrayList<String> newTags) {
        ArrayList<String> add = new ArrayList<String>();
        for (String nt : newTags) {
            boolean found = false;
            for (String et : tags) {
                if (nt.equals(et)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                add.add(nt);
            }
        }

        if (add.size() > 0) {
            tags.addAll(add);
            Collections.sort(this.tags);
            return true;
        } else {
            return false;
        }
    }
     * 
     */
        
    public void login(String uName, String passwd) throws Exception {
        username = uName;
        password = passwd;
        
        try {
            blogs = BloggerAPI.getUserBlogs(xmlRpc, username, password);
            for (Blog blog : blogs) {
                ArrayList<Category> categories = BloggerAPI.getCategoryList(xmlRpc, username, password, blog);
                blog.setCategories(categories);
            }            
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    
    public ArrayList<MetaWeblogEntry> getEntries(Blog blog, int number) throws Exception {
        return MetaWeblogAPI.getRecentPosts(getXmlRpc(), getUsername(), getPassword(), blog, number);
    }

    public ArrayList<MetaWeblogEntry> getPages(Blog blog) throws Exception {
        return WordPressAPI.getPages(getXmlRpc(), getUsername(), getPassword(), blog);
    }

    public boolean deletePage(MetaWeblogEntry page) throws Exception {
        return WordPressAPI.deletePage(getXmlRpc(), getUsername(), getPassword(), page);
    }
    
    public void createEntry(BlogEntry entry) throws Exception {
        Blog blog = entry.getBlog();
        if (blog == null) {
            throw new IllegalStateException();
        }
            
        try {
            MetaWeblogAPI.newPost(getXmlRpc(), getUsername(), getPassword(), entry);
        } catch (Exception e) {
            throw new Exception(e);
        }        
    }
    
    public void updateEntry(BlogEntry entry) throws Exception {
        try {
            if (entry instanceof MetaWeblogEntry) {
                MetaWeblogAPI.editPost(getXmlRpc(), getUsername(), getPassword(), entry);
                return;
            }

            if (entry instanceof WordPressPage) {
                WordPressPage page = (WordPressPage)entry;
                WordPressAPI.editPage(getXmlRpc(), getUsername(), getPassword(), page);
                return;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    
    public void deleteEntry(BlogEntry entry) throws Exception {
        try {
            BloggerAPI.deletePost(getXmlRpc(), getUsername(), getPassword(), entry);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    
    public MetaWeblogEntry newEntryObject(Blog blog) {
        return MetaWeblogAPI.createBlogEntryObject(blog);
    }

    public String newMediaObject(Blog blog, File file) throws Exception {
        return MetaWeblogAPI.newMediaObject(getXmlRpc(), getUsername(), getPassword(), blog, file);
    }

    public ArrayList<WordPressComment> getComments(Blog blog, BlogEntry entry, WordPressCommentStatus status, int offset, int number) throws Exception {
        return WordPressAPI.getComments(getXmlRpc(), getUsername(), getPassword(), blog, entry, status, offset, number);
    }

    public HashMap getCommentStatusList(Blog blog) throws Exception {
        return WordPressAPI.getCommentStatusList(getXmlRpc(), getUsername(), getPassword(), blog);
    }

    public void updateTags(Blog blog) throws Exception {
        ArrayList<WordPressTag> newTags = WordPressAPI.getTags(getXmlRpc(), getUsername(), getPassword(), blog);
        Collections.sort(newTags);
        tags.put(blog.getId(), newTags);
    }

    public WordPressCommentCount getCommentCount(BlogEntry entry) throws Exception {
        return WordPressAPI.getCommentCount(getXmlRpc(), getUsername(), getPassword(), entry);
    }

    public boolean deleteComment(Blog blog, WordPressComment comment) throws Exception {
        return WordPressAPI.deleteComment(getXmlRpc(), getUsername(), getPassword(), blog, comment);
    }

    public boolean editComment(Blog blog, WordPressComment comment) throws Exception {
        return WordPressAPI.editComment(getXmlRpc(), getUsername(), getPassword(), blog, comment);
    }

    public int newComment(Blog blog, String entryId, WordPressComment comment, int parentId) throws Exception {
        return WordPressAPI.newComment(getXmlRpc(), getUsername(), getPassword(), blog, entryId, comment, parentId);
    }

    public ArrayList<WordPressAuthor> getAuthors(Blog blog) throws Exception {
        return WordPressAPI.getAuthors(getXmlRpc(), getUsername(), getPassword(), blog);
    }
}