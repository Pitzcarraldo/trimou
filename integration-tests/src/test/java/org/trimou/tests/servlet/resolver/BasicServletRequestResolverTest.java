package org.trimou.tests.servlet.resolver;

import static org.junit.Assert.assertFalse;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicServletRequestResolverTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase().addAsLibraries(
                getResolver().artifact("org.trimou:trimou-extension-servlet")
                        .resolveAsFiles());
    }

    @Test
    public void testResolution() {

        Mustache mustache = MustacheEngineBuilder
                .newBuilder()
                .build()
                .compileMustache("servlet_request_resolver_test",
                        "{{request.method}}");

        assertFalse(mustache.render(null).isEmpty());
    }

}
