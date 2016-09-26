/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.xml;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.util.logging.Logging;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * EntityResolver implementation to prevent usage of external entities.
 * 
 * When parsing an XML entity, the empty InputSource returned by this resolver provokes 
 * throwing of a java.net.MalformedURLException, which can be handled appropriately.
 * 
 * @author Davide Savazzi - geo-solutions.it
 */
public class NoExternalEntityResolver implements EntityResolver {

    public static final String ERROR_MESSAGE_BASE = "Entity resolution disallowed for ";
    private static final Logger LOGGER = Logging.getLogger(NoExternalEntityResolver.class);
    public static final NoExternalEntityResolver INSTANCE = new NoExternalEntityResolver();
    
    private NoExternalEntityResolver() {
        // singleton
    }
    
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("resolveEntity request: publicId=" + publicId + ", systemId=" + systemId);
        }
        
        // allow schema parsing for validation.
        // http(s) - external schema reference
        // jar - internal schema reference
        // vfs - internal schema reference (JBOSS)
        if (systemId != null && systemId.matches("(?i)(jar:file|http|vfs)[^?#;]*\\.xsd")) {
            return null;
        }
        
        // do not allow external entities
        throw new SAXException(ERROR_MESSAGE_BASE + systemId);
    }
}