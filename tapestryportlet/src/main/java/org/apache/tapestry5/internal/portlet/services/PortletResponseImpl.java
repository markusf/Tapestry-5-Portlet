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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayRenderResponse;

/**
 * Adapts {@link javax.portlet.PortletResponse} as
 * {@link org.apache.tapestry.web.WebResponse}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class PortletResponseImpl implements Response
{
	private final Logger _logger = LoggerFactory.getLogger(PortletResponseImpl.class);

    private final PortletResponse _response;
    private final Request _request;
    protected boolean _isCommited = false;

    public PortletResponseImpl(PortletResponse portletResponse, Request portletRequest)
    {
        Defense.notNull(portletResponse, "response");
        
        _response = portletResponse;
        _request = portletRequest;
    }
    
    public PrintWriter getPrintWriter(String contentType) throws IOException
    {	
//    	_logger.error(PortletServicesMessages.unsupportedMethod("getPrintWriter"));
//    	return null;
//    	LiferayPortletResponse liferayResponse = (LiferayPortletResponse) _response;
//	    return liferayResponse.getHttpServletResponse().getWriter();
    	
//    	HttpServletResponse servletResponse = ((LiferayPortletResponse) _response).getHttpServletResponse();
//    	_logger.info("getPrintWriter " + contentType + " " + servletResponse.getCharacterEncoding());
//    	servletResponse.setContentType(contentType);
//    	OutputStream os = servletResponse.getOutputStream();
//    	Writer w = new OutputStreamWriter(os, servletResponse.getCharacterEncoding());
//    	return new PrintWriter(new BufferedWriter(w));
    	_logger.info("getPrintWriter");
    	LiferayPortletResponse liferayResponse = (LiferayPortletResponse) _response;
    	
    	if (_request == null){
    		_logger.info("REQUEST IS NULL");
    		return null;
    	}
    	
    	if (_request.isXHR()){
//    		_logger.info("Request is XHR IN RESPONSE TOO");
    		// TODO: Content Type Header + Encoding setzen (?)
    		_logger.info("XHR Content-Type: " + contentType);
    		liferayResponse.setHeader("Content-Type", contentType);
    		_logger.info("Response Content-Type: " + liferayResponse.getHttpServletResponse().getContentType());
    		return liferayResponse.getHttpServletResponse().getWriter();
    	} else {
//    		_logger.info("Request is not XHR IN RESPONSE");
    		return null;
    	}	
    }

    public OutputStream getOutputStream(String contentType) throws IOException
    {
//    	_logger.error(PortletServicesMessages.unsupportedMethod("getOutputStream"));
    	_logger.info("getOutputStream: " + contentType + " " + _response.getClass());
    	_isCommited = true;
    	LiferayRenderResponse liferayResponse = (LiferayRenderResponse) _response;
    	liferayResponse.setContentType(contentType);
        return liferayResponse.getPortletOutputStream();
    }
    
    public void sendRedirect(String URL) throws IOException
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("sendRedirect(URL)"));
    }
    
    public void sendRedirect(Link link) throws IOException
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("sendRedirect(link)"));
    }
    
    public void setStatus(int sc)
    {
        _logger.error(PortletServicesMessages.unsupportedMethod("setStatus"));
    }
    
    public void sendError(int statusCode, String message) throws IOException
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("sendError"));
    }
    
    public void setContentLength(int contentLength)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("setContentLength"));
    }
    
    public void setDateHeader(String string, long date)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("setDateHeader"));
    }
    
    public void setHeader(String name, String value)
    {
//    	_logger.error(PortletServicesMessages.unsupportedMethod("setHeader"));
//    	LiferayPortletResponse liferayResponse = (LiferayPortletResponse) _response;
//    	liferayResponse.getHttpServletResponse().setHeader(name, value);
    	_logger.info("Response Header: " + name + " " + value + " Class: " + _response.getClass());
//    	liferayResponse.setHeader(name, value);
    }
    
    public void setIntHeader(String name, int value)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("setIntHeader"));
    }

    public String encodeURL(String url)
    {
        return _response.encodeURL(url);
    }
    
    public String encodeRedirectURL(String URL)
    {
    	_logger.error(PortletServicesMessages.unsupportedMethod("encodeRedirectURL"));
    	
    	return null;
    }
    
    public boolean isCommitted()
    {
    	_logger.info("Commited: " + _isCommited);
    	
    	return _isCommited;
    }

}
