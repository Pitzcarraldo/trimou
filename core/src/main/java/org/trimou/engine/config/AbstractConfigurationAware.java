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
package org.trimou.engine.config;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract no-op configuration aware component.
 *
 * @author Martin Kouba
 */
public abstract class AbstractConfigurationAware implements ConfigurationAware {

    @Override
    public void init(Configuration configuration) {
        // No-op
    }

    @Override
    public Set<ConfigurationKey> getConfigurationKeys() {
        // No config keys by default
        return Collections.emptySet();
    }

    /**
     * @throws IllegalStateException
     *             If the isInitializedExpression evaluates to true
     */
    protected void checkNotInitialized(boolean isInitializedExpression) {
        if (isInitializedExpression) {
            throw new IllegalStateException("Component is already initialized!");
        }
    }

}
