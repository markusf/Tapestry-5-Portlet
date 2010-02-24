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

import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newList;

import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.portlet.PortletConstants;
import org.apache.tapestry5.portlet.PortletPageResolver;
import org.apache.tapestry5.services.ComponentClassResolver;

/**
 * Implements the PortletPageResolver for a basic portlet.  It runs through all
 * of the given configurations and tries to match the current request to one
 * of the configurations.  Note that it does this in order of the declared
 * configurations in the project Module file, so it returns the first page name 
 * that matches.
 */
public class PortletPageResolverImpl implements PortletPageResolver {
	
		private ComponentClassResolver _componentClassResolver;
	
		private List<PortletPageResolverRule> _configuration = newList();
	
		public PortletPageResolverImpl(
			ComponentClassResolver componentClassResolver, 
			List<PortletPageResolverRule> configuration)
		{
				_componentClassResolver = componentClassResolver;
				_configuration.addAll(configuration);
		}
	
		public String resolve(String portletName, PortletRequest request) 
		{
		    String pageName = (String) request.getParameter(PortletConstants.PORTLET_PAGE);
			if (_componentClassResolver.isPageName(pageName)) {
      			return pageName;
			}
			return "";
		}
		
		/*
		public String resolve(String portletName, PortletRequest request) 
		{
		    for (PortletPageResolverRule c : _configuration)
		    {
		        if (c.match(portletName, request))
		        {
		            String pageName = c.getPageName();
		            
		            if (_componentClassResolver.isPageName(pageName))
		            {
		                return pageName;
		            }
		        }
		    }
		    return "";
		}
		*/
}
