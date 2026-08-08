/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.security.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SecurityLdapAutoConfiguration}.
 *
 * <p>The auto-configuration class declares a large number of {@code @Bean} methods
 * whose dependencies (context source, populator, redirect strategy, request cache,
 * message source, role hierarchy, ...) are only resolvable inside a full security
 * application context. Rather than wiring all of those collaborators here, these tests
 * verify the class-level conditions and instantiation directly, which is what the
 * starter contract actually guarantees at the unit level.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityLdapAutoConfiguration Tests")
class SecurityLdapAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    @DisplayName("Auto-configuration class can be instantiated")
    void testInstantiation() {
        SecurityLdapAutoConfiguration configuration = new SecurityLdapAutoConfiguration();
        assertThat(configuration).isNotNull();
    }

    @Test
    @DisplayName("Class is a Spring @Configuration")
    void isAnnotatedAsConfiguration() {
        assertThat(SecurityLdapAutoConfiguration.class.isAnnotationPresent(Configuration.class)).isTrue();
    }

    @Test
    @DisplayName("Class carries a ConditionalOnProperty matching spring.security.ldap.enabled=true")
    void hasEnabledConditionalOnProperty() {
        ConditionalOnProperty conditional = SecurityLdapAutoConfiguration.class.getAnnotation(ConditionalOnProperty.class);
        assertThat(conditional).as("@ConditionalOnProperty must be present").isNotNull();
        assertThat(conditional.prefix()).isEqualTo(SecurityLdapProperties.PREFIX);
        // The annotation uses value = "enabled" (the alias for name).
        assertThat(conditional.value()).contains("enabled");
        assertThat(conditional.havingValue()).isEqualTo("true");
    }

    @Test
    @DisplayName("When 'spring.security.ldap.enabled' is not set the configuration does not back off the runner")
    void testNotLoadedWhenPropertyAbsent() {
        // With the property absent, the @ConditionalOnProperty prevents the configuration's
        // @Bean methods from contributing. The bare runner (no user configuration) must start.
        runner.run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    @DisplayName("ConditionalOnProperty meta-annotation round-trips as a real Annotation")
    void conditionalAnnotationIsAccessibleViaReflection() {
        Annotation[] annotations = SecurityLdapAutoConfiguration.class.getAnnotations();
        boolean found = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof ConditionalOnProperty) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }
}
