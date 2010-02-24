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

import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.services.RequestPathOptimizer;
import org.apache.tapestry5.ioc.internal.util.*;
import org.apache.tapestry5.portlet.PortletConstants;
import org.apache.tapestry5.services.Response;

/**
 * Wrapper around {@link javax.portlet.PortletURL}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class LinkImplPortlet implements Link
{	
    private Map<String, String> parameters;

    private final PortletURL portletURL;
    
    private final String absoluteURI;

    private final boolean optimizable;

    private final boolean forForm;

    private final Response response;

    private final RequestPathOptimizer optimizer;

    private String anchor;

    public LinkImplPortlet(PortletURL portletURL, String absoluteURI, boolean optimizable, boolean forForm, Response response,
                     RequestPathOptimizer optimizer)
    {
        this.portletURL = portletURL;
        this.absoluteURI = absoluteURI;
        this.optimizable = optimizable;
        this.forForm = forForm;
        this.response = response;
        this.optimizer = optimizer;
    }

    public void addParameter(String parameterName, String value)
    {
        Defense.notBlank(parameterName, "parameterName");
        Defense.notBlank(value, "value");

        if (parameters == null)
            parameters = CollectionFactory.newMap();

        parameters.put(parameterName, value);
    }

    public String getAnchor()
    {
        return anchor;
    }

    public List<String> getParameterNames()
    {
        return InternalUtils.sortedKeys(parameters);
    }

    public String getParameterValue(String name)
    {
        return InternalUtils.get(parameters, name);
    }

    public void setAnchor(String anchor)
    {
        this.anchor = anchor;
    }

    public String toAbsoluteURI()
    {
        return appendAnchor(response.encodeURL(buildURI()));
    }

    public String toRedirectURI()
    {
        return appendAnchor(response.encodeRedirectURL(buildURI()));
    }

    public String toURI()
    {
        String path = buildURI();

        if (optimizable)
            path = optimizer.optimizePath(path);

        return appendAnchor(response.encodeURL(path));
    }

    private String appendAnchor(String path)
    {
        return InternalUtils.isBlank(anchor)
               ? path
               : path + "#" + anchor;
    }

    /**
     * Returns the value from {@link #toURI()}
     */
    @Override
    public String toString()
    {
        return toURI();
    }

    /**
     * Extends the absolute path with any query parameters. Query parameters are never added to a forForm link.
     *
     * @return absoluteURI appended with query parameters
     */
    private String buildURI()
    {
        portletURL.setParameter(PortletConstants.PORTLET_PAGE, absoluteURI);

        StringBuilder builder = new StringBuilder(300);

        builder.append(unencode(portletURL.toString()));

        if (InternalUtils.isNonBlank(anchor))
        {
            builder.append("#");
            builder.append(anchor);
        }

        return builder.toString();
    }

    /**
     * The PortletURL class returns a url that's already XML-escaped, ready for
     * inclusion directly into the response stream. However, the IMarkupWriter
     * expects to do that encoding too ... and double encoding is bad. So we
     * back out the most likely encoding (convert '&amp;amp;' to just '&amp;').
     */
    private String unencode(String url)
    {
        StringBuffer buffer = new StringBuffer(url.length());
        String text = url;

        while(true)
        {
            int ampx = text.indexOf("&amp;");

            if (ampx < 0) break;

            // Take up to and including the '&'

            buffer.append(text.substring(0, ampx + 1));

            text = text.substring(ampx + 5);
        }

        buffer.append(text);

        return buffer.toString();
    }

}
