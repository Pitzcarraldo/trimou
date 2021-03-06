package org.trimou.tests.gson.resolver;

import static org.junit.Assert.assertEquals;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import java.io.StringReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.gson.JsonParser;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicJsonElementResolverTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase().addAsLibraries(
                getResolver().artifact("org.trimou:trimou-extension-gson")
                        .resolveAsFiles());
    }

    @Test
    public void testInterpolation() {
        Mustache mustache = MustacheEngineBuilder.newBuilder().build()
                .compileMustache("json_element_resolver_test", "{{foo.name}}");
        assertEquals("Jachym",
                mustache.render(new JsonParser().parse(new StringReader(
                        "{ \"foo\": { \"name\": \"Jachym\"}}"))));
    }

}
