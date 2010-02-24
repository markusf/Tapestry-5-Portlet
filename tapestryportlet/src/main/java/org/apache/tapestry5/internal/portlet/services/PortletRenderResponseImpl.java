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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around {@link javax.portlet.RenderResponse}&nbsp;to adapt it as
 * {@link org.apache.tapestry5.internal.portlet.services.PortletResponseImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class PortletRenderResponseImpl extends PortletResponseImpl
{
	private final Logger _logger = LoggerFactory.getLogger(PortletRenderResponseImpl.class);
	
    private final RenderResponse _renderResponse;

    public PortletRenderResponseImpl(RenderResponse renderResponse)
    {
        super(renderResponse, null);

        _renderResponse = renderResponse;
    }

    public void reset()
    {
        _renderResponse.reset();
    }

    public PrintWriter getPrintWriter(String contentType)
        throws IOException
    {
    	_logger.info("getPrintWriter");
        _renderResponse.setContentType(contentType.toString());
        return _renderResponse.getWriter();
    }

    public String getNamespace()
    {
        return _renderResponse.getNamespace();
    }
    
    @Override
    public boolean isCommitted() {
    	// TODO Auto-generated method stub
    	_logger.info("isCommited");
    	return _renderResponse.isCommitted();
    }
    
    @Override
    public OutputStream getOutputStream(String contentType) throws IOException {
    	// TODO Auto-generated method stub
    	_logger.info("getOutputStream");
    	_renderResponse.setContentType(contentType);
    	return _renderResponse.getPortletOutputStream();
    }
}
