package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.ArchiveType;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ReflectionResolverTest extends AbstractEngineTest {

    @Test
    public void testResolution() {

        ReflectionResolver resolver = new ReflectionResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        Hammer hammer = new Hammer();
        assertNull(resolver.resolve(null, "whatever", null));
        assertNotNull(resolver.resolve(hammer, "age", null));
        // Methods have higher priority
        assertEquals(Integer.valueOf(10), resolver.resolve(hammer, "age", null));
        assertNull(resolver.resolve(hammer, "getAgeForName", null));
    }

    @Test
    public void testInterpolation() {
        Map<String, Object> data = ImmutableMap.<String, Object> of("hammer",
                new Hammer(), "type", ArchiveType.class);
        assertEquals(
                "Hello Edgar of age 10, persistent: false and !",
                engine.compileMustache(
                        "reflection_resolver",
                        "Hello {{hammer.name}} of age {{hammer.age}}, persistent: {{hammer.persistent}} and {{hammer.invalidName}}!")
                        .render(data));
        assertEquals(
                "NAIL|jar",
                engine.compileMustache("reflection_resolver_fields",
                        "{{hammer.nail}}|{{type.JAR.suffix}}").render(data));
        assertEquals(
                "jar,war,ear,",
                engine.compileMustache("reflection_resolver_static_method",
                        "{{#type.values}}{{this.suffix}},{{/type.values}}")
                        .render(data));
    }

    @Test
    public void testMemberCacheInvalidation() {

        final AtomicInteger invalidatedEntries = new AtomicInteger(0);
        final ReflectionResolver resolver = new ReflectionResolver() {
            @Override
            public void onRemoval(
                    RemovalNotification<MemberKey, Optional<MemberWrapper>> notification) {
                super.onRemoval(notification);
                invalidatedEntries.incrementAndGet();
            }
        };

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        Hammer hammer = new Hammer();
        assertNotNull(resolver.resolve(hammer, "age", null));
        resolver.invalidateMemberCache(null);
        assertEquals(1, invalidatedEntries.get());
        invalidatedEntries.set(0);

        assertNotNull(resolver.resolve(hammer, "age", null));
        assertNotNull(resolver.resolve(ArchiveType.class, "JAR", null));
        resolver.invalidateMemberCache(new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getName().equals(ArchiveType.class.getName());
            }
        });
        assertEquals(1, invalidatedEntries.get());
    }

    @Test(expected=IllegalStateException.class)
    public void testMultipleInit() {

        ReflectionResolver resolver = new ReflectionResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        resolver.init(null);
    }

}
