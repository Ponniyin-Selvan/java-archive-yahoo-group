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

package org.panteleyev.utilities;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Petr Panteleyev <petr@panteleyev.org>
 */
public class MimeTypes {
    private static final String defaultType = "application/octet-stream";

    private HashMap<String,String> map = new HashMap<String,String>();

    public MimeTypes() {
        addMimeTypes(
           "image/png png\n" +
           "image/jpeg jpg jpeg jpe\n" +
           "image/gif gif\n" +
           "application/vnd.ms-powerpoint ppt\n" +
           "application/pdf pdf\n"
        );
    }

    public void addMimeTypes(String mime_types) {
        String[] mimes = mime_types.split("\n");

        for (String m : mimes) {
            String[] tokens = m.split(" ");

            if (tokens.length <= 1) {
                // illegal map
                continue;
            }

            String type = tokens[0];
            for (int i = 1; i < tokens.length; ++i) {
                map.put(tokens[i], type);
            }
        }        
    }

    public String getContentType(File f) {
        String type = defaultType;

        String fName = f.getName();
        int dot = fName.lastIndexOf('.');
        if (dot > 0) {
            String ext = fName.substring(dot);
            type = map.get(ext);
            if (type == null) {
                type = defaultType;
            }
        }

        return type;
    }

}
