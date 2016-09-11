/*
 * Copyright (c) 2010, Petr Panteleyev <petr@panteleyev.org>
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
import java.util.HashMap;

/**
 *
 * @author Petr Panteleyev <petr@panteleyev.org>
 */
public class WordPressTag implements Serializable, Comparable<WordPressTag> {
    private final static long serialVersionUID = 20100503L;

    private int     id;
    private String  name;
    private int     count;
    private String  slug;
    private String  htmlUrl;
    private String  rssUrl;

    WordPressTag(HashMap<String,Object> map) {
        id = Integer.parseInt((String)map.get("tag_id"));
        name = (String)map.get("name");
        slug = (String)map.get("slug");
        htmlUrl = (String)map.get("html_url");
        rssUrl = (String)map.get("rss_url");
        count = Integer.parseInt((String)map.get("count"));
    }

    WordPressTag(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public int compareTo(WordPressTag o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
