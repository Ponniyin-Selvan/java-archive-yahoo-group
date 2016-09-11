/*
 * Copyright (c) 2008-2010, Petr Panteleyev <petr@panteleyev.org>
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

public class MetaWeblogEntry extends BlogEntry implements Serializable {
    private final static long serialVersionUID = 20100207L;

    public MetaWeblogEntry() {        
    }

    public MetaWeblogEntry(Blog blog) {
        super(blog);
    }

    @Override
    void parse(HashMap<String,Object> map) {
        setId((String)map.get("postid"));
        setUserId(Integer.parseInt((String)map.get("userid")));
        
        setSubject((String)map.get("title"));
        
        setDateCreated((Date)map.get("dateCreated"));
        
        String publish = (String)map.get("post_status");
        if (publish != null) {
            setDraft(!publish.equals("publish"));                       
        } else {
            setDraft(false);
        }
        
        StringBuilder b = new StringBuilder((String)map.get("description"));
        String more = (String)map.get("mt_text_more");
        if ((more != null) && (more.length() != 0)) {
            b.append("\n<!--more-->");
            b.append(more);
        }
        
        setBody(b.toString());
        
        setLink((String)map.get("link"));
        setPermaLink((String)map.get("permalink"));

        Object[] cats = (Object[])map.get("categories");
        if (cats != null) {
            ArrayList<Category> blogCats = getBlog().getCategories();
            ArrayList<Integer> entryCats = new ArrayList<Integer>();

            for (Object cat : cats) {
                String catName = (String)cat;
                for (Category blogCat : blogCats) {
                    if (blogCat.getName().equals(catName)) {
                        entryCats.add(blogCat.getId());
                        break;
                    }
                }
            }
            setCategories(entryCats);
        }
        
        Object data = map.get("mt_keywords");
        if (data != null) {
            String[] tags = ((String)data).split(",");
            ArrayList<String> tList = new ArrayList<String>(tags.length);
            for (String tag : tags) {
                tList.add(tag.trim());
            }
            setTags(tList);
        }        

        /* Comments status */
        int allowComments = (Integer)map.get("mt_allow_comments");
        setCommentsStatus((allowComments == 1)? CommentsStatus.OPEN : CommentsStatus.CLOSED);

        /* Ping status */
        int allowPings = (Integer)map.get("mt_allow_pings");
        setPingStatus((allowPings == 1)? CommentsStatus.OPEN : CommentsStatus.CLOSED);
    }
    
    @Override
    public Object getContent() {
        HashMap<String,Object> content = new HashMap<String,Object>();
        content.put("title", getSubject());
        content.put("description", getBody());        
        content.put("dateCreated", getDateCreated());

        ArrayList<Integer> cats = getCategories();
        if (cats.size() > 0) {
            String[] catArray = new String[cats.size()];
            for (int i = 0; i < catArray.length; ++i) {
                catArray[i] = getBlog().getCategory(cats.get(i));
            }
            content.put("categories", catArray);
        }

        if (getTags().size() > 0) {
            StringBuilder b = new StringBuilder();
            for (String tag : getTags()) {
                b.append(tag);
                b.append(",");
            }
            content.put("mt_keywords", b.toString());
        }

        content.put("mt_allow_comments", getCommentsStatus().s());
        content.put("mt_allow_pings", getPingStatus().s());        
        return content;
    }
}