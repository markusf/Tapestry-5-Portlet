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

package org.apache.tapestry5.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.servlet.FilterConfig;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.portlet.services.PortletActionRequestHandler;
import org.apache.tapestry5.portlet.services.PortletRenderRequestHandler;
import org.apache.tapestry5.portlet.services.PortletResourceRequestHandler;

/**
 * Portlet implementation for Tapestry Portlet applilcations. It's job is to create and manage the
 * HiveMind registry, to use the <code>tapestry.portlet.PortletApplicationInitializer</code>
 * service to initialize HiveMind, and the delegate requests to the
 * <code>tapestry.portlet.ActionRequestServicer</code> and
 * <code>tapestry.portlet.RenderRequestServicer</code> services.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class ApplicationPortlet implements Portlet, ResourceServingPortlet
{   
    private String _portletName;
	
	private Registry _registry;

    PortletActionRequestHandler _actionHandler;

    PortletRenderRequestHandler _renderHandler;
    
    PortletResourceRequestHandler _resourceHandler;

    public void init(PortletConfig portletConfig) throws PortletException
    {
        System.out.println("ApplicationPortlet.init()");
        _portletName = portletConfig.getPortletName();
    	
    	PortletContext context = portletConfig.getPortletContext();
    	
    	_registry = PortletUtilities.getRegistry(portletConfig, provideExtraModuleDefs(context));
    	
        _actionHandler = _registry.getService(
        	"PortletActionRequestHandler", PortletActionRequestHandler.class);
        _renderHandler = _registry.getService(
        	"PortletRenderRequestHandler", PortletRenderRequestHandler.class);
        _resourceHandler = _registry.getService ("PortletResourceRequestHandler", PortletResourceRequestHandler.class);
        init(_registry);
    }
    
    /**
     * Invoked from {@link #init(FilterConfig)} after the Registry has been created, to allow any
     * additional initialization to occur. This implementation does nothing, and my be overriden in
     * subclasses.
     * 
     * @param registry
     *            from which services may be extracted
     * @throws PortletException
     */
    protected void init(Registry registry) throws PortletException
    {

    }
    
    /**
     * Overridden in subclasses to provide additional module definitions beyond those normally
     * located. This implementation returns an empty array.
     */
    protected ModuleDef[] provideExtraModuleDefs(PortletContext context)
    {
    	return new ModuleDef[0];
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException
    {
        try
        {
        	_actionHandler.service(_portletName, request, response);
        }
        finally
        {
            _registry.cleanupThread();
        }
    }

    public void render(RenderRequest request, RenderResponse response) throws PortletException,
            IOException
    {
        try
        {
        	_renderHandler.service(_portletName, request, response);
        }
        finally
        {
            _registry.cleanupThread();
        }
    }
    
	public void serveResource(ResourceRequest request, ResourceResponse response)
	throws PortletException, IOException {
		 try
	        {
	        	_resourceHandler.service(_portletName, request, response);
	        }
	        finally
	        {
	            _registry.cleanupThread();
	        }
	}
    
    /** Shuts down and discards the registry. */
    public final void destroy()
    {
        destroy(_registry);

        _registry.shutdown();

        _registry = null;
        _actionHandler = null;
        _renderHandler = null;
    }
    
    /**
     * Invoked from {@link #destroy()} to allow subclasses to add additional shutdown logic to the
     * filter. The Registry will be shutdown after this call. This implementation does nothing, and
     * may be overridden in subclasses.
     * 
     * @param registry
     */
    protected void destroy(Registry registry)
    {

    }
    
}
