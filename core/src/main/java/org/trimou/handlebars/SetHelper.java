/*
 * Copyright 2014 Martin Kouba
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
package org.trimou.handlebars;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Works similarly as {@link WithHelper} except the current
 * {@link Options#getHash()} map is pushed on the context stack.
 *
 * <pre>
 * {{#set foo="hello"}}
 *   {{foo}}
 * {{/set}}
 * </pre>
 *
 * <pre>
 * {{#set foo="Hello" myName=person.name}}
 *   {{foo}} {{myName}}!
 * {{/set}}
 * </pre>
 *
 * @author Martin Kouba
 */
public class SetHelper extends BasicSectionHelper {

    @Override
    public void execute(Options options) {
        options.push(options.getHash());
        options.fn();
        options.pop();
    }

    @Override
    public void validate(HelperDefinition definition) {
        super.validate(definition);
        if (definition.getHash().isEmpty()) {
            throw new MustacheException(
                    MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE,
                    "No hash entry specified for %s [template: %s, line: %s]",
                    SetHelper.class, definition.getTagInfo().getTemplateName(),
                    definition.getTagInfo().getLine());
        }
    }

    @Override
    protected int numberOfRequiredParameters() {
        return 0;
    }

}
