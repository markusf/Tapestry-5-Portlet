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

import java.util.*;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;

/**
 * Implementation of {@link org.apache.tapestry.web.WebRequest} that adapts a
 * {@link PortletRequestImpl} .
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class PortletRequestImpl implements Request
{
	
	private static final String REQUESTED_WITH_HEADER = "X-Requested-With";
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

	private final Logger _logger = LoggerFactory.getLogger(PortletRequestImpl.class);

    private final PortletRequest _request;
    
    private final String _pageName;
    
    private Session _session;
    
    private SessionPersistedObjectAnalyzer _analyzer;

    public PortletRequestImpl(PortletRequest request, String pageName, SessionPersistedObjectAnalyzer analyzer)
    {
        _request = request;
        _pageName = pageName;
        _analyzer = analyzer;
    }
    
    public Session getSession(boolean create)
    {
        if (_session != null) return _session;
    	
    	PortletSession session = _request.getPortletSession(create);
    	
    	if (session != null) _session = new PortletSessionImpl(session, _analyzer);
        
        return _session;
    }
    
    public String getContextPath()
    {
        return _request.getContextPath();
    }

    public List<String> getParameterNames()
    {
        return InternalUtils.toList(_request.getParameterNames());
    }

    public String getParameter(String name)
    {
        return _request.getParameter(name);
    }

    public String[] getParameters(String name)
    {
//        Enumeration enumeration = _request.getParameterNames();
//        while (enumeration.hasMoreElements()) {
//            String parameter = (String) enumeration.nextElement();
//            System.out.println("request parameter: " + parameter + " " + _request.getParameter(parameter));
//        }
        return _request.getParameterValues(name);
    }
    
    public String getPath()
    {
    	return "/" + _pageName;
    }
    
    public Locale getLocale()
    {
        return _request.getLocale();
    }
    
    public List<String> getHeaderNames()
    {
        _logger.error(PortletServicesMessages.unsupportedMethod("getHeaderNames"));
        
    	return new ArrayList<String>();
    }
    
    public long getDateHeader(String name)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("getDateHeader"));

        return -1;
    }
    
    public String getHeader(String name)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("getHeader"));

        return null;
    }
    
    public void setEncoding(String requestEncoding) 
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("setEncoding"));
    }
    
    public boolean isXHR()
    {
//    	_logger.info("Content-Type:" + _request.getResponseContentType());
//    	
//    	Enumeration<String> values = _request.getResponseContentTypes();
//    	while (values.hasMoreElements()){
//    		_logger.info(values.nextElement());
//    	}
//    	_logger.info("PORTLET CLASS:" + _request.getClass());
//    	_logger.error(PortletServicesMessages.unsupportedMethod("isXHR"));
    	
//    	return false;
    	LiferayPortletRequest liferayRequest = (LiferayPortletRequest) _request;
//    	Enumeration<String> values = liferayRequest.getHttpServletRequest().getHeaderNames();
//    	
//    	while (values.hasMoreElements()){
//    		System.out.println(liferayRequest.getHttpServletRequest().getHeader(values.nextElement()));
//    	}
    	
    	if (XML_HTTP_REQUEST.equals(liferayRequest.getHttpServletRequest().getHeader((REQUESTED_WITH_HEADER)))){
    		_logger.info("REQUEST IS XHR");
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean isSecure()
    {
        return _request.isSecure();
    }
    
    public boolean isRequestedSessionIdValid()
    {
        return _request.isRequestedSessionIdValid();
    }
    
    public Object getAttribute(String name)
    {
    	return _request.getAttribute(name);
    }

    public void setAttribute(String name, Object value)
    {
    	_request.setAttribute(name, value);
    }
    
    public String getServerName()
    {
        return _request.getServerName();
    }

	public String getMethod() 
	{
		//_logger.error(PortletServicesMessages.unsupportedMethod("getMethod"));
	
		return "POST";
	}

}
