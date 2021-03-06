// Copyright 2006 The Apache Software Foundation
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
package org.apache.tapestry5.portlet.multipart;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.tapestry5.portlet.services.PortletActionRequestFilter;
import org.apache.tapestry5.portlet.services.PortletActionRequestHandler;

/**
 * @author Raphael Jean
 */
public class PortletMultipartDecoderFilter implements PortletActionRequestFilter
{
    private PortletMultipartDecoder _decoder;

    public boolean service(String portletName, ActionRequest request, ActionResponse response,
            PortletActionRequestHandler servicer)
        throws IOException, PortletException
    {
        String contentType = request.getContentType();

        // contentType is occasionally null in testing. The browser tacks on
        // additional
        // information onto the contentType to indicate where the boundaries are
        // in
        // the stream.

        boolean encoded = contentType != null
                && contentType.startsWith("multipart/form-data");

        boolean serviced = false;
        try
        {
            ActionRequest newRequest = encoded ? _decoder.decode(request)
                    : request;

            serviced = servicer.service(portletName, newRequest, response);
        }
        catch (RuntimeException ex) {
        	
        }
        
        return serviced;
    }

    public void setDecoder(PortletMultipartDecoder decoder)
    {
        this._decoder = decoder;
    }

}
