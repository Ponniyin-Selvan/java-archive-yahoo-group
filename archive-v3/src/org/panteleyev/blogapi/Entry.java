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
import java.util.Date;
import java.util.HashMap;

final public class Entry extends BlogEntry implements Serializable {
    private final static long serialVersionUID = 20100207L;

    public Entry() {
    }
    
    public Entry(Blog blog) {
        super(blog);
    }
        
    @Override
    Object getContent() {
        StringBuilder b = new StringBuilder();
        
        if ((getSubject() != null) && (getSubject().length() != 0)) {
            b.append("<title>");
            b.append(getSubject());
            b.append("</title>");
        }

        if (getCategories().size() != 0) {
            b.append("<category>");
            b.append(getCategories().get(0));
            for (int i = 1; i < getCategories().size(); i++) {
                b.append(",");
                b.append(getCategories().get(i));
            }
            b.append("</category>");
        }
        
        b.append(getBody());
        
        return b.toString();
    }
    
    /**
     * 
     * @param map
     */
    @Override
    public void parse(HashMap<String,Object> map) {
        setId((String)map.get("postid"));
        setDateCreated((Date)map.get("dateCreated"));
        setUserId(Integer.parseInt((String)map.get("userid")));
        
        String content = (String)map.get("content");
        
        // Get subject
        int p1 = content.indexOf("<title>");
        int p2 = 0;
        if (p1 != -1) {
            p2 = content.indexOf("</title>", p1 + 7);
            if (p2 != -1) {
                setSubject(content.substring(p1 + 7, p2));
                p2 += 8;
            }
        }
        
        p1 = content.indexOf("<category>", p2);
        if (p1 != -1) {
            p2 = content.indexOf("</category>");
            if (p2 != -1) {
                String category = content.substring(p1 + 10, p2);
                String[] list = category.split(",");
                for (String cat : list) {
                    try {
                        getCategories().add(Integer.parseInt(cat));
                    } catch (NumberFormatException ex) {
                        // do nothing, just ignore
                    }
                }

                p2 += 11;
            }
        }
        
        setBody(content.substring(p2));
    }    
}
