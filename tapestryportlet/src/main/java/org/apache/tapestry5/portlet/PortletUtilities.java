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

import static java.lang.String.format;

import java.util.BitSet;
import java.util.regex.Pattern;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.portlet.PortletContextSymbolProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.portlet.services.PortletApplicationInitializer;
import org.apache.tapestry5.portlet.services.PortletModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wiv.tapestryportlet2.services.AppModule;


/**
 * Holds the singleton registry that exists between each portlet in the application.
 * Provides initialization and access to that registry
 */
public final class PortletUtilities {
	
	private static final Logger _logger = LoggerFactory.getLogger(PortletUtilities.class);
	
	private static Registry _registry = null;
	
	private PortletUtilities()
	{
		
	}
	
	// Since multiple portlets share the registry and additionally need to access it to define action
	// and render handlers, this method makes the singleton registry available to the portlets.  This was
	// done to prevent initialization of multiple registries, making the application extremely slow.
	public static synchronized Registry getRegistry(PortletConfig portletConfig, ModuleDef... moduleDefs)
	{
		if (_registry == null)
		{
			initRegistry(portletConfig, moduleDefs);
		}
		return _registry;
	}
	
	private static void initRegistry(PortletConfig portletConfig, ModuleDef... moduleDefs)
	{
        PortletContext context = portletConfig.getPortletContext();

        SymbolProvider provider = new PortletContextSymbolProvider(context);
        
        String appName = portletConfig.getPortletContext().getPortletContextName();

        TapestryAppInitializer appInitializer = new TapestryAppInitializer(_logger, provider, appName, "portlet");
        
        // Since this is a portlet page, this module refits some of the pages for portlets
        appInitializer.addModules(PortletModule.class);
        appInitializer.addModules(AppModule.class);

        appInitializer.addModules(moduleDefs);

        _registry = appInitializer.createRegistry();

//        long start = appInitializer.getStartTime();
//
//        long toRegistry = appInitializer.getRegistryCreatedTime();

        PortletApplicationInitializer ai = _registry.getService(
                "PortletApplicationInitializer",
                PortletApplicationInitializer.class);
//
        ai.initializeApplication(portletConfig.getPortletContext());

        _registry.performRegistryStartup();
//
//        long toFinish = System.currentTimeMillis();
//
//        _logger.info(format("Startup time: %,d ms to build IoC Registry, %,d ms overall.", toRegistry
//                - start, toFinish - start));
        
        appInitializer.announceStartup();

	}
	
	private static final URLCodec CODEC = new URLCodec()
    {

        private BitSet contextSafe = (BitSet) WWW_FORM_URL.clone();

        {
            // Servlet container does not decode '+' in path to ' ',
            // so we encode ' ' to %20, not to '+'.
            contextSafe.clear(' ');
        }

        @Override
        public byte[] encode(byte[] bytes)
        {
            return encodeUrl(contextSafe, bytes);
        }
    };

    /**
     * Encodes a string for inclusion in a URL.  Slashes and percents are converted to "%25" and "%2F" respectively,
     * then the entire string is  URL encoded.
     *
     * @param input string to include, may not be blank
     * @return encoded input
     */
    public static String encodeContext(String input)
    {
        Defense.notBlank(input, "input");

        try
        {
            return CODEC.encode(escapePercentAndSlash(input));
        }
        catch (EncoderException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private static final String PERCENT = "%";
    private static final Pattern PERCENT_PATTERN = Pattern.compile(PERCENT);
    private static final String ENCODED_PERCENT = "%25";
    private static final Pattern ENCODED_PERCENT_PATTERN = Pattern.compile(ENCODED_PERCENT);

    private static final String SLASH = "/";
    private static final Pattern SLASH_PATTERN = Pattern.compile(SLASH);
    private static final String ENCODED_SLASH = "%2F";
    private static final Pattern ENCODED_SLASH_PATTERN = Pattern.compile(ENCODED_SLASH, Pattern.CASE_INSENSITIVE);

    /**
     * Encodes percent and slash characters in the string for later decoding via {@link
     * #unescapePercentAndSlash(String)}.
     *
     * @param input string to encode
     * @return modified string
     */
    public static String escapePercentAndSlash(String input)
    {
        return replace(replace(input, PERCENT_PATTERN, ENCODED_PERCENT), SLASH_PATTERN, ENCODED_SLASH);
    }

    /**
     * Used to decode certain escaped characters that are replaced when using {@link #encodeContext(String)}}.
     *
     * @param input a previously encoded string
     * @return the string with slash and percent characters restored
     */
    public static String unescapePercentAndSlash(String input)
    {
        return replace(replace(input, ENCODED_SLASH_PATTERN, SLASH), ENCODED_PERCENT_PATTERN, PERCENT);
    }
    
    private static String replace(String input, Pattern pattern, String replacement)
    {
        return pattern.matcher(input).replaceAll(replacement);
    }

}
