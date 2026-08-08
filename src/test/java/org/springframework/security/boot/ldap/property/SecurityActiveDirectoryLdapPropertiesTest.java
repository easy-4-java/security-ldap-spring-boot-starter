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
package org.springframework.security.boot.ldap.property;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SecurityActiveDirectoryLdapProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityActiveDirectoryLdapProperties Tests")
class SecurityActiveDirectoryLdapPropertiesTest {

    @Test
    @DisplayName("Default constructor creates non-null instance")
    void testDefaultInstance() {
        assertThat(new SecurityActiveDirectoryLdapProperties()).isNotNull();
    }

    @Test
    @DisplayName("enabled defaults to false and is round-trippable")
    void testEnabledField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.isEnabled()).isFalse();
        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("domain is round-trippable")
    void testDomainField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.getDomain()).isNull();
        props.setDomain("corp.example.com");
        assertThat(props.getDomain()).isEqualTo("corp.example.com");
    }

    @Test
    @DisplayName("rootDn is round-trippable")
    void testRootDnField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.getRootDn()).isNull();
        props.setRootDn("dc=example,dc=com");
        assertThat(props.getRootDn()).isEqualTo("dc=example,dc=com");
    }

    @Test
    @DisplayName("url is round-trippable")
    void testUrlField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.getUrl()).isNull();
        props.setUrl("ldap://ad.example.com:389");
        assertThat(props.getUrl()).isEqualTo("ldap://ad.example.com:389");
    }

    @Test
    @DisplayName("convertSubErrorCodesToExceptions defaults to false and is round-trippable")
    void testConvertSubErrorCodesToExceptionsField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.isConvertSubErrorCodesToExceptions()).isFalse();
        props.setConvertSubErrorCodesToExceptions(true);
        assertThat(props.isConvertSubErrorCodesToExceptions()).isTrue();
    }

    @Test
    @DisplayName("searchFilter has a sensible default and is round-trippable")
    void testSearchFilterField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.getSearchFilter()).isNotEmpty();
        props.setSearchFilter("(uid={0})");
        assertThat(props.getSearchFilter()).isEqualTo("(uid={0})");
    }

    @Test
    @DisplayName("environment defaults to a non-null empty map and is replaceable")
    void testEnvironmentField() {
        SecurityActiveDirectoryLdapProperties props = new SecurityActiveDirectoryLdapProperties();
        assertThat(props.getEnvironment()).isNotNull().isEmpty();

        Map<String, Object> env = new HashMap<>();
        env.put("java.naming.referral", "follow");
        props.setEnvironment(env);
        assertThat(props.getEnvironment()).isSameAs(env).containsEntry("java.naming.referral", "follow");

        // Replacing with an empty map keeps the field non-null.
        props.setEnvironment(Collections.emptyMap());
        assertThat(props.getEnvironment()).isNotNull().isEmpty();
    }
}
