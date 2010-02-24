package org.apache.tapestry5.internal.portlet.services;

import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newList;
import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newMap;

import java.util.*;

import javax.portlet.*;

import org.apache.tapestry5.*;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.*;
import org.apache.tapestry5.internal.structure.ComponentPageElement;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.internal.util.*;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.StrategyRegistry;
import org.apache.tapestry5.portlet.PortletUtilities;
import org.apache.tapestry5.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkSourceImplPortlet implements LinkSource, LinkCreationHub {
    
    private final Logger _logger = LoggerFactory.getLogger(LinkSourceImplPortlet.class);
    
    private final PageRenderQueue pageRenderQueue;

    private final PageActivationContextCollector contextCollector;

    private final ComponentEventLinkEncoder linkEncoder;

    private final List<LinkCreationListener> listeners = CollectionFactory.newThreadSafeList();

    private final TypeCoercer typeCoercer;

    private final ComponentClassResolver resolver;
    
    private final RenderResponse renderResponse;
    
    private final PortletRequest portletRequest;
    
    private final ContextValueEncoder contextValueEncoder;
    
    private final StrategyRegistry<PassivateContextHandler> registry;
    
    private interface PassivateContextHandler<T>
    {
        void handle(T result, List context);
    }
    
    public LinkSourceImplPortlet(PageRenderQueue pageRenderQueue,
                          PageActivationContextCollector contextCollector,
                          TypeCoercer typeCoercer,
                          ComponentClassResolver resolver,
                          ComponentEventLinkEncoder linkEncoder,
                          RenderResponse renderResponse,
                          PortletRequest portletRequest,
                          ContextValueEncoder contextValueEncoder)
    {
        this.pageRenderQueue = pageRenderQueue;
        this.contextCollector = contextCollector;
        this.typeCoercer = typeCoercer;
        this.resolver = resolver;
        this.linkEncoder = linkEncoder;
        this.renderResponse = renderResponse;
        this.portletRequest = portletRequest;
        this.contextValueEncoder = contextValueEncoder;
        

        Map<Class, PassivateContextHandler> registrations = newMap();

        registrations.put(Object.class, new PassivateContextHandler()
        {
            @SuppressWarnings("unchecked")
            public void handle(Object result, List context)
            {
                context.add(result);
            }
        });

        registrations.put(Object[].class, new PassivateContextHandler<Object[]>()
        {

            @SuppressWarnings("unchecked")
            public void handle(Object[] result, List context)
            {
                for (Object o : result)
                    context.add(o);
            }
        });

        registrations.put(Collection.class, new PassivateContextHandler<Collection>()
        {
            @SuppressWarnings("unchecked")
            public void handle(Collection result, List context)
            {
                context.addAll(result);
            }
        });

        registry = StrategyRegistry.newInstance(PassivateContextHandler.class, registrations);
    }

    public Link createComponentEventLink(Page page, String nestedId, String eventType, boolean forForm,
                                         Object... eventContext)
    {
        Defense.notNull(page, "page");
        Defense.notBlank(eventType, "action");

        Page activePage = pageRenderQueue.getRenderingPage();

        // See TAPESTRY-2184
        if (activePage == null)
            activePage = page;

        String activePageName = activePage.getName();

        Object[] pageActivationContext = contextCollector.collectPageActivationContext(activePageName);

        ComponentEventRequestParameters parameters
                = new ComponentEventRequestParameters(
                activePageName,
                page.getName(),
                toBlank(nestedId),
                eventType,
                new ArrayEventContext(typeCoercer, pageActivationContext),
                new ArrayEventContext(typeCoercer, eventContext));

        PortletURL portletURL = renderResponse.createActionURL();
        
        try 
        {
            portletURL.setPortletMode(portletRequest.getPortletMode());
            portletURL.setWindowState(portletRequest.getWindowState());
        } 
        catch (PortletException pe) 
        {
            _logger.error(PortletServicesMessages.errorProcessingRender(pe));
        }
        
        //Link link = new PortletLinkImpl(response, baseURL, portletURL, invocation, forForm);
        Link link = linkEncoder.createComponentEventLink(parameters, forForm);

        String[] activationContext = collectActivationContextForPage(activePage);
        
        // Now see if the page has an activation context.

        addActivationContextToLink(link, activationContext, forForm);
        
        for (LinkCreationListener listener : listeners)
            listener.createdComponentEventLink(link);

        return link;
    }
    
    private void addActivationContextToLink(Link link, String[] activationContext, boolean forForm)
    {
        if (activationContext.length == 0) return;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < activationContext.length; i++)
        {
            if (i > 0) builder.append("/");

            builder.append(forForm
                           ? PortletUtilities.escapePercentAndSlash(activationContext[i])
                           : PortletUtilities.encodeContext(activationContext[i]));
        }

        link.addParameter(InternalConstants.PAGE_CONTEXT_NAME, builder.toString());
    }
    
    private String toBlank(String input)
    {
        return input == null ? "" : input;
    }

    public Link createPageRenderLink(String pageName, boolean override, Object... pageActivationContext)
    {
        // Resolve the page name to its canonical format (the best version for URLs). This also validates
        // the page name.

        String canonical = resolver.canonicalizePageName(pageName);

        Object[] context = (override || pageActivationContext.length != 0)
                           ? pageActivationContext
                           : contextCollector.collectPageActivationContext(canonical);

        PageRenderRequestParameters parameters =
                new PageRenderRequestParameters(canonical,
                                                new ArrayEventContext(typeCoercer, context));

        Link link = linkEncoder.createPageRenderLink(parameters);

        for (LinkCreationListener listener : listeners)
            listener.createdPageRenderLink(link);

        return link;
    }

    public LinkCreationHub getLinkCreationHub()
    {
        return this;
    }

    public void addListener(LinkCreationListener listener)
    {
        Defense.notNull(listener, "listener");

        listeners.add(listener);
    }

    public void removeListener(LinkCreationListener listener)
    {
        Defense.notNull(listener, "listener");

        listeners.remove(listener);
    }
    

    /**
     * Returns a list of objects acquired by invoking triggering the passivate event on the page's root element. May
     * return an empty list.
     */
    public String[] collectActivationContextForPage(final Page page)
    {
        final List context = newList();

        ComponentEventCallback callback = new ComponentEventCallback()
        {
            @SuppressWarnings("unchecked")
            public boolean handleResult(Object result)
            {
                PassivateContextHandler contextHandler = registry.getByInstance(result);

                contextHandler.handle(result, context);

                return true;
            }
        };

        ComponentPageElement rootElement = page.getRootElement();

        rootElement.triggerEvent(EventConstants.PASSIVATE, null, callback);

        return toContextStrings(context.toArray());
    }

    private String[] toContextStrings(Object[] context)
    {
        if (context == null) return new String[0];

        String[] result = new String[context.length];

        for (int i = 0; i < context.length; i++)
        {

            Object value = context[i];

            String encoded = value == null ? null : contextValueEncoder.toClient(value);

            if (InternalUtils.isBlank(encoded))
                throw new RuntimeException(PortletServicesMessages.contextValueMayNotBeNull());

            result[i] = encoded;
        }

        return result;
    }
}
