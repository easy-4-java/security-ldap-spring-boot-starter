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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.boot.biz.property.SecuritySessionMgtProperties;
import org.springframework.security.boot.ldap.property.SecurityLdapAuthcProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SecurityLdapFilterConfiguration} and its nested
 * {@code LdapWebSecurityConfigurerAdapter}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityLdapFilterConfiguration Tests")
class SecurityLdapFilterConfigurationTest {

    @Test
    @DisplayName("Outer configuration class can be instantiated")
    void outerConfiguration_canBeInstantiated() {
        assertThat(new SecurityLdapFilterConfiguration()).isNotNull();
    }

    @Test
    @DisplayName("Nested LdapWebSecurityConfigurerAdapter can be constructed with empty ObjectProviders")
    void nestedAdapter_canBeConstructed() {
        SecurityBizProperties bizProperties = new SecurityBizProperties();
        SecuritySessionMgtProperties sessionMgtProperties = new SecuritySessionMgtProperties();
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        SecurityLdapAuthcProperties authcProperties = new SecurityLdapAuthcProperties();

        ObjectProvider<?> emptyProvider = new EmptyObjectProvider<>();

        SecurityLdapFilterConfiguration.LdapWebSecurityConfigurerAdapter adapter = constructAdapter(
                bizProperties, sessionMgtProperties, ldapProperties, authcProperties, emptyProvider);

        assertThat(adapter).isNotNull();
    }

    /**
     * Reflectively invoke the nested adapter constructor so all of its field-initialisation
     * lines (ldapProperties / authcProperties / localeContextFilter / entryPoint / success /
     * failure handlers / objectMapper / rememberMeServices / sessionAuthenticationStrategy)
     * execute. The type is package-private so the test stays in
     * {@code org.springframework.security.boot}.
     */
    private SecurityLdapFilterConfiguration.LdapWebSecurityConfigurerAdapter constructAdapter(
            SecurityBizProperties bizProperties,
            SecuritySessionMgtProperties sessionMgtProperties,
            SecurityLdapProperties ldapProperties,
            SecurityLdapAuthcProperties authcProperties,
            ObjectProvider<?> emptyProvider) {
        try {
            java.lang.reflect.Constructor<SecurityLdapFilterConfiguration.LdapWebSecurityConfigurerAdapter> ctor =
                    SecurityLdapFilterConfiguration.LdapWebSecurityConfigurerAdapter.class.getDeclaredConstructor(
                            SecurityBizProperties.class,
                            SecuritySessionMgtProperties.class,
                            SecurityLdapProperties.class,
                            SecurityLdapAuthcProperties.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class);
            ctor.setAccessible(true);
            return ctor.newInstance(
                    bizProperties,
                    sessionMgtProperties,
                    ldapProperties,
                    authcProperties,
                    emptyProvider, // LocaleContextFilter
                    emptyProvider, // AuthenticationProvider
                    emptyProvider, // AuthenticationListener
                    emptyProvider, // MatchedAuthenticationEntryPoint
                    emptyProvider, // MatchedAuthenticationSuccessHandler
                    emptyProvider, // MatchedAuthenticationFailureHandler
                    emptyProvider, // ObjectMapper
                    emptyProvider, // RememberMeServices
                    emptyProvider  // SessionAuthenticationStrategy
            );
        } catch (java.lang.reflect.InvocationTargetException e) {
            // Surface the real cause for easier debugging.
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new AssertionError("adapter construction failed", cause);
        } catch (Exception e) {
            throw new AssertionError("adapter construction failed", e);
        }
    }

    /** Minimal {@link ObjectProvider} that always reports empty / unavailable. */
    private static final class EmptyObjectProvider<T> implements ObjectProvider<T> {
        @Override
        public T getObject() {
            return null;
        }

        @Override
        public T getIfAvailable() {
            return null;
        }

        @Override
        public T getIfUnique() {
            return null;
        }

        @Override
        public java.util.Iterator<T> iterator() {
            return java.util.Collections.<T>emptyList().iterator();
        }

        @Override
        public java.util.stream.Stream<T> stream() {
            return java.util.stream.Stream.empty();
        }
    }
}
