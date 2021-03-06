package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.locator.MapTemplateLocator;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class PartialSegmentTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @Test
    public void testRecursiveInvocationLimitExceeded() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "part", "{{>part}}", "alpha", "{{>bravo}}", "bravo",
                "{{>alpha}}"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        5).build();

        // Simply test infinite loops
        try {
            engine.getMustache("part").render(null);
            fail("Limit exceeded and no exception thrown");
        } catch (MustacheException e) {
            if (!e.getCode()
                    .equals(MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
            // else {
            // e.printStackTrace();
            // }
        }

        try {
            engine.getMustache("alpha").render(null);
            fail("Limit exceeded and no exception thrown");
        } catch (MustacheException e) {
            if (!e.getCode()
                    .equals(MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
            // else {
            // e.printStackTrace();
            // }
        }
    }

    @Test
    public void testRecursiveInvocationDisabled() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "node", "{{content}}<{{#nodes}}{{>node}}{{/nodes}}>"));
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(
                        EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                        0).build();

        Map<String, Object> data = ImmutableMap.<String, Object> of("content",
                "X", "nodes", new Map[] { ImmutableMap.of("content", "Y",
                        "nodes", new Map[] { ImmutableMap.of("content", "Z",
                                "nodes", new Map[] {}) }) });

        try {
            engine.getMustache("node").render(data);
            fail("Limit exceeded and no exception thrown");
        } catch (MustacheException e) {
            if (!e.getCode()
                    .equals(MustacheProblem.RENDER_TEMPLATE_INVOCATION_RECURSIVE_LIMIT_EXCEEDED)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
            // else {
            // e.printStackTrace();
            // }
        }
    }

    @Test
    public void testRecursiveInvocationAllowed() {

        MapTemplateLocator locator = new MapTemplateLocator(ImmutableMap.of(
                "node", "{{content}}<{{#nodes}}{{>node}}{{/nodes}}>"));
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator).build();

        Map<String, Object> data = ImmutableMap.<String, Object> of("content",
                "X", "nodes", new Map[] { ImmutableMap.of("content", "Y",
                        "nodes", new Map[] { ImmutableMap.of("content", "Z",
                                "nodes", new Map[] {}) }) });

        assertEquals("X<Y<Z<>>>", engine.getMustache("node").render(data));
    }

    @Test
    public void testPartialNotFound() {

        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();

        try {
            engine.compileMustache("partial_not_found",
                    "Hello,\n this partial \n\n {{>neverexisted}}")
                    .render(null);
            fail("Partial does not exist!");
        } catch (MustacheException e) {
            if (!e.getCode().equals(MustacheProblem.RENDER_INVALID_PARTIAL_KEY)) {
                fail("Invalid problem");
            }
            System.out.println(e.getMessage());
        }
    }

}
