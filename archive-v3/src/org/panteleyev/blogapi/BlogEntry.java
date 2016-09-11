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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Petr Panteleyev
 */
public abstract class BlogEntry implements IBlogEntry, Serializable {
    private final static long serialVersionUID = 20100207L;

    /* Serializable fields */
    private String              id = "";
    private boolean             draft;
    private Date                dateCreated = new Date();
    private String              blogId;
    private String              subject = "";
    private String              body = "";
    private CommentsStatus      commentsStatus = CommentsStatus.DEFAULT;
    private CommentsStatus      pingStatus = CommentsStatus.DEFAULT;
    private ArrayList<Integer>  categories = new ArrayList<Integer>();
    private ArrayList<String>   tags = new ArrayList<String>();

    /* Transient fields */
    private transient int userId;
    private transient String link;
    private transient String permaLink;
    private transient boolean allowComments;
    
    private transient Blog blog;

    public BlogEntry() {        
    }
    
    public BlogEntry(Blog blog) {
        this.blog = blog;
        blogId = (blog == null)? null : blog.getId();
    }
    
    /**
     * 
     * @return
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void clearId() {
        id = "";
    }
    
    /**
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
    /**
     * Returns subject of the entry
     * @return
     */
    @Override
    public String getSubject() {
        return subject;
    }
    
    /**
     * 
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Returns body of the entry
     * @return
     */
    @Override
    public String getBody() {
        return body;
    }
    
    /**
     * 
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * 
     * @return
     */
    public Blog getBlog() {
        return blog;
    }
    
    /**
     * 
     * @return
     */
    public String getBlogId() {
        return blogId;
    }            

    /**
     * 
     * @param blog
     */
    public void setBlog(Blog blog) {
        this.blog = blog;
        blogId = (blog == null)? null : blog.getId();
    }
       
    /**
     * 
     * @return
     */
    public ArrayList<Integer> getCategories() {
        return categories;
    }

    /**
     * 
     * @param categories
     */
    public void setCategories(ArrayList<Integer> categories) {
        this.categories = categories;
    }

    /**
     * 
     * @return
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
    
    /**
     * 
     * @return
     */
    public String getLink() {
        return link;
    }

    /**
     * 
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * 
     * @return
     */
    public String getPermaLink() {
        return permaLink;
    }

    /**
     * 
     * @param permaLink
     */
    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    /**
     * 
     * @return
     */
    public boolean isAllowComments() {
        return allowComments;
    }

    /**
     * 
     * @param allowComments
     */
    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }
    
    /**
     * 
     * @return
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * 
     * @param draft
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }
    
    /**
     * 
     * @return
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public Date getDate() {
        return getDateCreated();
    }
    
    /**
     * 
     * @param dateCreated
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * 
     * @return
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public CommentsStatus getCommentsStatus() {
        return commentsStatus;
    }

    public void setCommentsStatus(CommentsStatus commentsStatus) {
        this.commentsStatus = commentsStatus;
    }

    public CommentsStatus getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(CommentsStatus pingStatus) {
        this.pingStatus = pingStatus;
    }

    abstract void parse(HashMap<String,Object> map);    
    abstract Object getContent();
            
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if ((obj == null) || !(obj instanceof BlogEntry)) {
            return false;
        }
        
        BlogEntry entry = (BlogEntry)obj;
        
        if ((getId() == null) || (getBlogId() == null)) {
            return false;
        }
        
        if ((entry == null) || (entry.getId() == null) || (entry.getBlogId() == null)) {
            return false;
        }
        
        return (getId().equals(entry.getId()) && getBlogId().equals(entry.getBlogId()));        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (getId() != null ? this.getId().hashCode() : 0);
        hash = 59 * hash + (getBlogId() != null ? this.getBlogId().hashCode() : 0);
        return hash;
    }

    @Override
    public String getBlogName() {
        return (blog == null)? null : blog.getName();
    }
}