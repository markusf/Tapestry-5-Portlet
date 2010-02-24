// Copyright 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.portlet.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.portlet.PortletContext;

import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapts {@link javax.portlet.PortletContext}as
 * {@link org.apache.tapestry.web.WebContext}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class PortletContextImpl implements Context
{

		private final Logger _logger = LoggerFactory.getLogger(PortletContextImpl.class);

    private final PortletContext _portletContext;

    public PortletContextImpl(PortletContext portletContext)
    {
        Defense.notNull(portletContext, "portletContext");

        _portletContext = portletContext;
    }
    
    public URL getResource(String path)
    {
        try
        {
            return _portletContext.getResource(path);
        }
        catch (MalformedURLException ex)
        {
            _logger.error(PortletServicesMessages.errorGettingResource(path, ex), ex);

            return null;
        }
    }
	
    public File getRealFile(String path) 
    {
    		String realPath = _portletContext.getRealPath(path);

    		return realPath == null ? null : new File(realPath);
    }
    
    public String getInitParameter(String name)
    {
        return _portletContext.getInitParameter(name);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getResourcePaths(String path)
    {
        return InternalUtils.toList((new Vector<String>(_portletContext.getResourcePaths(path))).elements());
    }
    
    public Object getAttribute(String name)
    {
        return _portletContext.getAttribute(name);
    }

	public List<String> getAttributeNames() {
			return InternalUtils.toList(_portletContext.getAttributeNames());
	}

    public String getMimeType(String file) {
        throw new IllegalStateException("not support getMimeType()");
    }

}
