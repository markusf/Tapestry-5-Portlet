//Copyright 2006, 2007 The Apache Software Foundation
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

import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newList;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.tapestry5.*;
import org.apache.tapestry5.internal.services.*;
import org.apache.tapestry5.internal.structure.ComponentPageElement;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.portlet.PortletConstants;
import org.apache.tapestry5.services.*;

public class PortletPageResponseRendererImpl implements PageResponseRenderer
{
    
    private final PortletRequest _portletRequest;
    
    private final ActionResponse _actionResponse;

    private final PageMarkupRenderer _markupRenderer;

    private final MarkupWriterFactory _markupWriterFactory;

    private final PageContentTypeAnalyzer _pageContentTypeAnalyzer;

    private final Response _response;
    
    private final ContextValueEncoder _contextValueEncoder;
    
    private final ContextPathEncoder _contextPathEncoder;

    public PortletPageResponseRendererImpl(PortletRequest portletRequest,
    									   ActionResponse actionResponse, 
    									   MarkupWriterFactory markupWriterFactory, 
    									   PageMarkupRenderer markupRenderer, 
    									   PageContentTypeAnalyzer pageContentTypeAnalyzer,
    									   Response response,
    									   ContextValueEncoder contextValueEncoder,
    									   ContextPathEncoder contextPathEncoder)
    {
    	_portletRequest = portletRequest;
        _actionResponse = actionResponse;
        _markupWriterFactory = markupWriterFactory;
        _markupRenderer = markupRenderer;
        _pageContentTypeAnalyzer = pageContentTypeAnalyzer;
        _response = response;
        _contextValueEncoder = contextValueEncoder;
        _contextPathEncoder = contextPathEncoder;
    }

    public void renderPageResponse(Page page) throws IOException
    {
    	Defense.notNull(page, "page");
    	
    	// For the moment, the content type is all that's used determine the model for the markup writer.
    	// It's something of a can of worms.
    	ContentType contentType = _pageContentTypeAnalyzer.findContentType(page);
        
        // hack...
        // If the PrintWriter is null, then it means that this is an ActionResponse
        // Don't know if there's a better way to figure this out from the
        // given information
        PrintWriter pw = _response.getPrintWriter(contentType.toString());
        
        if (pw != null) 
        {
            MarkupWriter writer = _markupWriterFactory.newMarkupWriter(contentType);
            _markupRenderer.renderPageMarkup(page, writer);
	        writer.toMarkup(pw);
	        pw.flush();
        } else 
        {
        	if (_actionResponse != null)
        	{
		        	_actionResponse.setRenderParameter(PortletConstants.PORTLET_MODE, 
		        			_portletRequest.getPortletMode().toString());
		        	_actionResponse.setRenderParameter(PortletConstants.WINDOW_STATE, 
		        			_portletRequest.getWindowState().toString());
		        	
		        	final List context = newList();
		        	ComponentEventCallback callback = new ComponentEventCallback()
		            {
		                @SuppressWarnings("unchecked")
		                public boolean handleResult(Object result)
		                {
		                    if (result instanceof Collection) {
		                        context.addAll((Collection) result);
		                    } else if (result instanceof Object[]) {
		                        for (Object o : (Object[]) result) {
		                            context.add(o);
		                        }
		                    } else {
		                        context.add(result);
		                    }
		                    return true;
		                }
		            };

		        	
		            ComponentPageElement rootElement = page.getRootElement();

		            rootElement.triggerEvent(EventConstants.PASSIVATE, null, callback);
		            
		            String[] contextArray = toContextStrings(context.toArray());
                    System.out.println("PortletPageResponseRendererImpl.renderPageResponse() context = " + context);
		            
                    String pageName = page.getName();
                    String contextPath = _contextPathEncoder.encodeIntoPath(contextArray);
                    
                    String pageAndContext = pageName + "/" + contextPath;

                    _actionResponse.setRenderParameter(PortletConstants.PORTLET_PAGE, pageAndContext);
                    System.out.println("PortletPageResponseRendererImpl.renderPageResponse() pageAndContext = " + pageAndContext);
		            
		            
		            //String[] activationContext = collectActivationContextForPage(page);
        	}
        }
    }

    private String[] toContextStrings(Object[] context)
    {
        if (context == null) return new String[0];

        String[] result = new String[context.length];

        for (int i = 0; i < context.length; i++)
        {

            Object value = context[i];

            String encoded = value == null ? null : _contextValueEncoder.toClient(value);

            if (InternalUtils.isBlank(encoded))
                throw new RuntimeException(PortletServicesMessages.contextValueMayNotBeNull());

            result[i] = encoded;
        }

        return result;
    }
}

