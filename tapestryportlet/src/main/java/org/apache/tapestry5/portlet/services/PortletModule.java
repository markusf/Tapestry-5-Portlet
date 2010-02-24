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

package org.apache.tapestry5.portlet.services;

import java.io.IOException;
import java.util.List;

import javax.portlet.*;
import javax.servlet.http.Cookie;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.portlet.services.*;
import org.apache.tapestry5.internal.services.*;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.PipelineBuilder;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.portlet.PortletConstants;
import org.apache.tapestry5.portlet.PortletPageResolver;
import org.apache.tapestry5.services.*;
import org.slf4j.Logger;

public final class PortletModule {
	
		public static void bind(ServiceBinder binder)
		{
				binder.bind(PortletRequestGlobals.class, PortletRequestGlobalsImpl.class);
				binder.bind(ComponentEventLinkEncoder.class, ComponentEventLinkEncoderImplPortlet.class).withId("ComponentEventLinkEncoderImplPortlet");
		}
	
		public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
		{
				configuration.add(SymbolConstants.FORCE_ABSOLUTE_URIS, "true");
				configuration.add(SymbolConstants.SUPPRESS_REDIRECT_FROM_ACTION_REQUESTS, "true");
		}
	
    /**
     * Builds a shadow of the PortletRequestGlobals.portletRequest property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(PortletInterfaceProvider.class)
    public PortletRequest buildPortletRequest(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "portletRequest", PortletRequest.class);
    }
    
    /**
     * Builds a shadow of the PortletRequestGlobals.actionRequest property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(ActionInterfaceProvider.class)
    public ActionRequest buildActionRequest(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "actionRequest", ActionRequest.class);
    }
    
    /**
     * Builds a shadow of the PortletRequestGlobals.renderRequest property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(RenderInterfaceProvider.class)
    public RenderRequest buildRenderRequest(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "renderRequest", RenderRequest.class);
    }
		
		@Marker(ResourceInterfaceProvider.class)
    public ResourceRequest buildResourceRequest(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "resourceRequest", ResourceRequest.class);
    }		
	
    /**
     * Builds a shadow of the PortletRequestGlobals.portletResponse property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(PortletInterfaceProvider.class)
    public PortletResponse buildPortletResponse(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "portletResponse", PortletResponse.class);
    }
    
    /**
     * Builds a shadow of the PortletRequestGlobals.actionResponse property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(ActionInterfaceProvider.class)
    public ActionResponse buildActionResponse(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "actionResponse", ActionResponse.class);
    }
    
    /**
     * Builds a shadow of the PortletRequestGlobals.renderResponse property. Note again that the shadow can be 
     * an ordinary singleton, even though PortletRequestGlobals is perthread.
     */
		@Marker(RenderInterfaceProvider.class)
    public RenderResponse buildRenderResponse(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "renderResponse", RenderResponse.class);
    }
		
		@Marker(ResourceInterfaceProvider.class)
    public ResourceResponse buildResourceResponse(
    		
    		PropertyShadowBuilder shadowBuilder,
    		
    		PortletRequestGlobals portletRequestGlobals)
    {
        return shadowBuilder.build(portletRequestGlobals, "resourceResponse", ResourceResponse.class);
    }
	
    public PortletApplicationInitializer build(Logger logger,
    		
    		List<PortletApplicationInitializerFilter> configuration,
            
        @InjectService("ApplicationGlobals")
        final ApplicationGlobals applicationGlobals,
            
        @InjectService("ApplicationInitializer")
        final ApplicationInitializer initializer,
            
        @InjectService("PipelineBuilder")
        PipelineBuilder builder)
    {
        PortletApplicationInitializer terminator = new PortletApplicationInitializer()
        {
            public void initializeApplication(PortletContext context)
            {
              	// this does not work since the app globals do not know of portlets  
            		//applicationGlobals.store(context);

              	initializer.initializeApplication(new PortletContextImpl(context));
            }
        };

        return builder.build(logger,
        		PortletApplicationInitializer.class,
            PortletApplicationInitializerFilter.class,
            configuration,
            terminator);
    }
    
    public PortletPageResolver build(
    		ComponentClassResolver componentClassResolver,
    		List<PortletPageResolverRule> configuration)
    {
    		return new PortletPageResolverImpl(componentClassResolver, configuration);
    }

    public void contributePortletPageResolver(
    		OrderedConfiguration<PortletPageResolverRule> configuration)
    {
//    		configuration.add("default", new PortletPageResolverRule("", "text/html", "view", "normal", "start"), "after:*");
//    		configuration.add("Index", new PortletPageResolverRule("TapestryPortlet", "text/html", "view", "normal", "Index"));
//    		configuration.add("Aboutxhr", new PortletPageResolverRule("TapestryPortlet", "application/json", "view", "normal", "About"));
//    		configuration.add("About", new PortletPageResolverRule("TapestryPortlet", "text/html", "view", "normal", "About"));
//    		configuration.add("Contact", new PortletPageResolverRule("TapestryPortlet", "text/html", "view", "normal", "Contact"));
    }
    
    public PortletActionRequestHandler build(Logger logger,
            
    		List<PortletActionRequestFilter> configuration,
    		
    		@InjectService("PipelineBuilder")
    		PipelineBuilder builder,
    	    
    		final PortletPageResolver pageResolver,
    	    
    		@InjectService("RequestGlobals")
    		final RequestGlobals requestGlobals,
    	    
    		@InjectService("PortletRequestGlobals")
    		final PortletRequestGlobals portletRequestGlobals,

    		@InjectService("RequestHandler")
    		final RequestHandler handler,
        
    		@Primary
    		final SessionPersistedObjectAnalyzer analyzer) {
        
        PortletActionRequestHandler terminator = new PortletActionRequestHandler()
        {
            public boolean service(String portletName, ActionRequest request, ActionResponse response)
            		throws IOException
            {
            		String pageName = "";
            		if (isStateChange(request)) {
            				pageName = pageResolver.resolve(portletName, request);
            		} else {
            				pageName = request.getParameter(PortletConstants.PORTLET_PAGE);
            		}
            	
            		Request portletRequest = new PortletRequestImpl(request, pageName, analyzer);
            		Response portletResponse = new PortletResponseImpl(response, portletRequest);
                
            		requestGlobals.storeRequestResponse(portletRequest, portletResponse);
            		portletRequestGlobals.store(request, response);

                return handler.service(portletRequest, portletResponse);
            }
            
            /**
             * Returns true if the portlet mode or the window state has changed since
             * (if both are null, the state has not changed).
             * The values stored previously (during an action request) are compared to
             * the current values.
             */
            private boolean isStateChange(ActionRequest actionRequest)
            {
            		String mode = actionRequest.getParameter(PortletConstants.PORTLET_MODE);
                String windowState = actionRequest.getParameter(PortletConstants.WINDOW_STATE);

                return (mode != null || windowState != null) 
                   && !(actionRequest.getPortletMode().toString().equals(mode) 
                	 && actionRequest.getWindowState().toString().equals(windowState));
            }
        };

        return builder.build(logger,
        		PortletActionRequestHandler.class,
            PortletActionRequestFilter.class,
            configuration,
            terminator);
    }
    
    public PortletRenderRequestHandler build(Logger logger,
            
    		    List<PortletRenderRequestFilter> configuration,
    		
    		    @InjectService("PipelineBuilder")
    		    PipelineBuilder builder,
    	    
    		    final PortletPageResolver pageResolver,
    	    
    		    @InjectService("RequestGlobals")
    		    final RequestGlobals requestGlobals,
    	    
    		    @InjectService("PortletRequestGlobals")
    		    final PortletRequestGlobals portletRequestGlobals,

    		    @InjectService("RequestHandler")
    		    final RequestHandler handler,

    		    @Primary
    		    final SessionPersistedObjectAnalyzer analyzer) {
        
        PortletRenderRequestHandler terminator = new PortletRenderRequestHandler()
        {
            public boolean service(String portletName, RenderRequest request, RenderResponse response)
                    throws IOException
            {
            		String pageName = request.getParameter(PortletConstants.PORTLET_PAGE);
            		if (pageName == null || pageName.equals("")) {
            				pageName = pageResolver.resolve(portletName, request);
            		}
            	
            		Request portletRequest = new PortletRequestImpl(request, pageName, analyzer);
            		Response portletResponse = new PortletRenderResponseImpl(response);
                
            		requestGlobals.storeRequestResponse(portletRequest, portletResponse);
            		portletRequestGlobals.store(request, response);

                return handler.service(portletRequest, portletResponse);
            }
        };

        return builder.build(
        		logger,
            PortletRenderRequestHandler.class,
            PortletRenderRequestFilter.class,
            configuration,
            terminator);
    }
    
    public PortletResourceRequestHandler build(Logger logger,
            
		    List<PortletResourceRequestFilter> configuration,
		
		    @InjectService("PipelineBuilder")
		    PipelineBuilder builder,
	    
		    final PortletPageResolver pageResolver,
	    
		    @InjectService("RequestGlobals")
		    final RequestGlobals requestGlobals,
	    
		    @InjectService("PortletRequestGlobals")
		    final PortletRequestGlobals portletRequestGlobals,

		    @InjectService("RequestHandler")
		    final RequestHandler handler,

		    @Primary
		    final SessionPersistedObjectAnalyzer analyzer) {
    	
    	PortletResourceRequestHandler terminator = new PortletResourceRequestHandler() {
			
			public boolean service(String portletName, ResourceRequest request,
					ResourceResponse response) throws IOException, PortletException {
				String pageName = request.getParameter(PortletConstants.PORTLET_PAGE);
        		if (pageName == null || pageName.equals("")) {
        				pageName = pageResolver.resolve(portletName, request);
        		}
        	
        		Request portletRequest = new PortletRequestImpl(request, pageName, analyzer);
        		Response portletResponse = new PortletResourceResponseImpl(response);
            
        		requestGlobals.storeRequestResponse(portletRequest, portletResponse);
        		portletRequestGlobals.store(request, response);

            return handler.service(portletRequest, portletResponse);
			}
		};
		
        return builder.build(
        		logger,
            PortletResourceRequestHandler.class,
            PortletResourceRequestFilter.class,
            configuration,
            terminator);
    	
    }
/*
    public LinkFactory buildPortletLinkFactory(
    		
    		Response response,
    		
    		@PortletInterfaceProvider
    		PortletRequest portletRequest,
    		
    		@RenderInterfaceProvider
    		RenderResponse renderResponse,
    	    
          RequestPageCache pageCache, 

    	  PageRenderQueue pageRenderQueue,
    	    
    	  ContextValueEncoder contextValueEncoder,
    	  
    	  ContextPathEncoder contextPathEncoder,
    	    
          RequestSecurityManager requestSecurityManager) 
    {
    		return new PortletLinkFactoryImpl(response, portletRequest, renderResponse,
    				componentInvocationMap, pageCache, pageRenderQueue,
    				contextValueEncoder, contextPathEncoder, requestSecurityManager);
    }
*/
    public PageResponseRenderer buildPortletPageResponseRenderer(
    		
    		@PortletInterfaceProvider
    		PortletRequest portletRequest,
    		
    		@ActionInterfaceProvider
    		ActionResponse actionResponse,
    		
    		MarkupWriterFactory markupWriterFactory,
			
    		PageMarkupRenderer markupRenderer,
			
    		PageContentTypeAnalyzer pageContentTypeAnalyzer,
			
    		Response response,
    		
            ContextValueEncoder contextValueEncoder,
            
            ContextPathEncoder contextPathEncoder)
    {
    		return new PortletPageResponseRendererImpl(portletRequest, actionResponse, 
    				markupWriterFactory, markupRenderer, pageContentTypeAnalyzer, response, contextValueEncoder, contextPathEncoder);
    }
    
    public CookieSource buildPortletCookieSource()
    {
        return new CookieSource()
        {
            public Cookie[] getCookies()
            {
                return new Cookie[0];
            }

        };
    }
    
    public void contributeAliasOverrides(
    		Configuration<AliasContribution> configuration,
    		
    		@InjectService("PortletCookieSource")
    	    final CookieSource cookieSource,
    		
    		@InjectService("PortletPageResponseRenderer")
    		final PageResponseRenderer pageResponseRenderer,
    		
    		@InjectService("ComponentEventLinkEncoder")
            final ComponentEventLinkEncoder componentEventLinkEncoder)
    {
    	configuration.add(AliasContribution.create(CookieSource.class, cookieSource));
    	configuration.add(AliasContribution.create(PageResponseRenderer.class, pageResponseRenderer));
    }
    
    public void contributeServiceOverride(MappedConfiguration<Class, Object> configuration,
                                          @Local ComponentEventLinkEncoder componentEventLinkEncoder) {
        configuration.add(ComponentEventLinkEncoder.class, componentEventLinkEncoder);
    }
}
