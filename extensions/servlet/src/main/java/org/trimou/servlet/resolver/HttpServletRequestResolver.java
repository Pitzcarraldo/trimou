/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.servlet.resolver;

import static org.trimou.engine.priority.Priorities.rightAfter;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.engine.listener.MustacheCompilationEvent;
import org.trimou.engine.listener.MustacheListener;
import org.trimou.engine.listener.MustacheParsingEvent;
import org.trimou.engine.listener.MustacheRenderingEvent;
import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.resource.ReleaseCallback;
import org.trimou.engine.validation.Validateable;
import org.trimou.servlet.RequestHolder;

/**
 * This resolver must be also registered as a {@link MustacheListener} in order
 * to work correctly.
 *
 * <p>
 * If the configuration property with key {@link #ENABLED_KEY} resolves to
 * false, the resolver is marked as invalid.
 * </p>
 *
 * @author Martin Kouba
 * @see Resolver
 */
public class HttpServletRequestResolver extends AbstractResolver implements
        MustacheListener, Validateable {

    public static final int SERVLET_REQUEST_RESOLVER_PRIORITY = rightAfter(WithPriority.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

    public static final ConfigurationKey ENABLED_KEY = new SimpleConfigurationKey(
            HttpServletRequestResolver.class.getName() + ".enabled", true);

    private static final String NAME_REQUEST = "request";

    private static final Logger logger = LoggerFactory
            .getLogger(HttpServletRequestResolver.class);

    private static final ThreadLocal<HttpServletRequestWrapper> REQUEST_WRAPPER = new ThreadLocal<HttpServletRequestWrapper>();

    private boolean isEnabled;

    public HttpServletRequestResolver() {
        this(SERVLET_REQUEST_RESOLVER_PRIORITY);
    }

    public HttpServletRequestResolver(int priority) {
        super(priority);
    }

    @Override
    public Object resolve(Object contextObject, String name,
            ResolutionContext context) {

        if (contextObject != null) {
            return null;
        }

        if (NAME_REQUEST.equals(name)) {

            // Wrapper is cached for each template execution/rendering
            HttpServletRequestWrapper wrapper = REQUEST_WRAPPER.get();

            if (wrapper == null) {
                HttpServletRequest request = RequestHolder.getCurrentRequest();
                if (request != null) {
                    wrapper = new HttpServletRequestWrapper(request);
                    REQUEST_WRAPPER.set(wrapper);
                } else {
                    logger.warn("Unable to get the current HTTP request");
                }
            }
            return wrapper;
        }
        return null;
    }

    @Override
    public void init(Configuration configuration) {
        this.isEnabled = configuration.getBooleanPropertyValue(ENABLED_KEY);
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        return Collections.singleton(ENABLED_KEY);
    }

    @Override
    public void renderingStarted(MustacheRenderingEvent event) {
        event.registerReleaseCallback(new ReleaseCallback() {
            @Override
            public void release() {
                REQUEST_WRAPPER.remove();
            }
        });

    }

    @Override
    public void renderingFinished(MustacheRenderingEvent event) {
        // No-op
    }

    @Override
    public void compilationFinished(MustacheCompilationEvent event) {
        // No-op
    }

    @Override
    public void parsingStarted(MustacheParsingEvent event) {
        // No-op
    }

    @Override
    public boolean isValid() {
        return isEnabled;
    }

}
