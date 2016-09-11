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

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Petr Panteleyev <petr@panteleyev.org>
 */
public class WordPressComment {
    private int                     id;
    private int                     parentId;
    private int                     postId;
    private String                  content;
    private String                  ip;
    private String                  link;
    private String                  author;
    private String                  authorEmail;
    private String                  authorUrl;
    private int                     userId;
    private WordPressCommentStatus  status;
    private Date                    dateCreated;
    private String                  postTitle;

    public WordPressComment() {
        status = WordPressCommentStatus.APPROVE;
    }
    
    WordPressComment(HashMap<String,Object> map) {
        id = Integer.parseInt((String)map.get("comment_id"));
        parentId = Integer.parseInt((String)map.get("parent"));
        postId = Integer.parseInt((String)map.get("post_id"));
        link = (String)map.get("link");
        content = (String)map.get("content");
        ip = (String)map.get("author_ip");
        author = (String)map.get("author");
        authorEmail = (String)map.get("author_email");
        authorUrl = (String)map.get("author_url");
        userId = Integer.parseInt((String)map.get("user_id"));
        status = WordPressCommentStatus.valueOf(((String)map.get("status")).toUpperCase());
        dateCreated = (Date)map.get("date_created_gmt");
        postTitle = (String)map.get("post_title");
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public String getLink() {
        return link;
    }

    public int getParentId() {
        return parentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public WordPressCommentStatus getStatus() {
        return status;
    }

    public int getUserId() {
        return userId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(WordPressCommentStatus status) {
        this.status = status;
    }
}
