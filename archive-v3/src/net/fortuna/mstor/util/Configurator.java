/**
 * Copyright (c) 2009, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.util;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides configuration properties specified either as system properties
 * or in an mstor.properties configuration file.
 * 
 * @author Ben
 * 
 * <pre>
 * $Id: Configurator.java,v 1.2 2009/03/05 06:04:58 fortuna Exp $
 *
 * Created on 06/02/2008
 * </pre>
 * 
 *
 */
public final class Configurator {

    private static final Log LOG = LogFactory.getLog(Configurator.class);
    
    private static final Properties CONFIG = new Properties();
    
    static {
        try {
            CONFIG.load(Configurator.class.getResourceAsStream("/mstor.properties"));
        }
        catch (Exception e) {
            LOG.info("mstor.properties not found.");
        }
    }
    
    /**
     * Constructor made private to enforce static nature.
     */
    private Configurator() {
    }
    
    /**
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return CONFIG.getProperty(key, System.getProperty(key));
    }
    
    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        return CONFIG.getProperty(key, System.getProperty(key, defaultValue));
    }
}
