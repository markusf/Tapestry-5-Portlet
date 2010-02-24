package org.apache.tapestry5.portlet.services;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

public interface PortletResourceRequestHandler {
    boolean service(String portletName, ResourceRequest request, ResourceResponse response)
    throws IOException, PortletException;
}
