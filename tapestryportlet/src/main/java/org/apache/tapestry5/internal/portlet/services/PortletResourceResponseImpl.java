package org.apache.tapestry5.internal.portlet.services;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortletResourceResponseImpl extends PortletResponseImpl {
	
	private final Logger _logger = LoggerFactory.getLogger(PortletRenderResponseImpl.class);
	
	private final ResourceResponse _resourceResponse;
	
	public PortletResourceResponseImpl(ResourceResponse resourceResponse) {
		super(resourceResponse, null);
		_resourceResponse = resourceResponse;
	}
	
	@Override
	public OutputStream getOutputStream(String contentType) throws IOException {
		// TODO Auto-generated method stub
		_logger.info("getOutputStream");
		_resourceResponse.setContentType(contentType);
		_isCommited = true;
		return _resourceResponse.getPortletOutputStream();
	}
	
	@Override
	public PrintWriter getPrintWriter(String contentType) throws IOException {
		// TODO Auto-generated method stub
		_logger.info("getPrintWriter");
		_resourceResponse.setContentType(contentType);
		_isCommited = true;
		return _resourceResponse.getWriter();
	}

}
