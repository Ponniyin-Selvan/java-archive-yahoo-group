/*
 * Copyright (c) 2008, Petr Panteleyev <petr@panteleyev.org>
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Petr Panteleyev <petr@panteleyev.org>
 */
public class FileUtil {

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            return null;
        }

        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length)
               && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    public static void deleteDirectory(File dir) {
        if ((dir != null) && dir.exists()) {
            File[] files = dir.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                   deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        dir.delete();
    }

    public static void closeStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    public static void closeStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    private FileUtil() {
    }

}
