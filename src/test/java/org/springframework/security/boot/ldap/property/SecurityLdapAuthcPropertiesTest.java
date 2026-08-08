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
import org.springframework.security.boot.ldap.authentication.AuthoritiesMapperPolicy;
import org.springframework.security.boot.ldap.authentication.DirContextPolicy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SecurityLdapAuthcProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityLdapAuthcProperties Tests")
class SecurityLdapAuthcPropertiesTest {

    private SecurityLdapAuthcProperties newProps() {
        return new SecurityLdapAuthcProperties();
    }

    @Test
    @DisplayName("Default constructor creates non-null instance")
    void testDefaultInstance() {
        assertThat(newProps()).isNotNull();
    }

    @Test
    @DisplayName("loginUrlPatterns defaults and is round-trippable")
    void testLoginUrlPatternsField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getLoginUrlPatterns()).isEqualTo("/login/ldap");
        props.setLoginUrlPatterns("/signin/ldap/**");
        assertThat(props.getLoginUrlPatterns()).isEqualTo("/signin/ldap/**");
    }

    @Test
    @DisplayName("successUrl defaults and is round-trippable")
    void testSuccessUrlField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getSuccessUrl()).isEqualTo("/index");
        props.setSuccessUrl("/home");
        assertThat(props.getSuccessUrl()).isEqualTo("/home");
    }

    @Test
    @DisplayName("failureUrl defaults and is round-trippable")
    void testFailureUrlField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getFailureUrl()).isEqualTo("/error");
        props.setFailureUrl("/login?error");
        assertThat(props.getFailureUrl()).isEqualTo("/login?error");
    }

    @Test
    @DisplayName("usernameParameter defaults and is round-trippable")
    void testUsernameParameterField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getUsernameParameter()).isEqualTo("username");
        props.setUsernameParameter("user");
        assertThat(props.getUsernameParameter()).isEqualTo("user");
    }

    @Test
    @DisplayName("passwordParameter defaults and is round-trippable")
    void testPasswordParameterField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getPasswordParameter()).isEqualTo("password");
        props.setPasswordParameter("pwd");
        assertThat(props.getPasswordParameter()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("targetUrlParameter defaults and is round-trippable")
    void testTargetUrlParameterField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getTargetUrlParameter()).isEqualTo("target");
        props.setTargetUrlParameter("redirectTo");
        assertThat(props.getTargetUrlParameter()).isEqualTo("redirectTo");
    }

    @Test
    @DisplayName("useAuthenticationRequestCredentials defaults and is round-trippable")
    void testUseAuthenticationRequestCredentialsField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.isUseAuthenticationRequestCredentials()).isTrue();
        props.setUseAuthenticationRequestCredentials(false);
        assertThat(props.isUseAuthenticationRequestCredentials()).isFalse();
    }

    @Test
    @DisplayName("ldapUrls is nullable and round-trippable")
    void testLdapUrlsField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getLdapUrls()).isNull();
        String[] urls = {"ldap://host1:389", "ldap://host2:389"};
        props.setLdapUrls(urls);
        assertThat(props.getLdapUrls()).containsExactly("ldap://host1:389", "ldap://host2:389");
    }

    @Test
    @DisplayName("urls is nullable and round-trippable")
    void testUrlsField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getUrls()).isNull();
        props.setUrls(new String[]{"ldap://primary:389"});
        assertThat(props.getUrls()).containsExactly("ldap://primary:389");
    }

    @Test
    @DisplayName("pooled defaults and is round-trippable")
    void testPooledField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.isPooled()).isFalse();
        props.setPooled(true);
        assertThat(props.isPooled()).isTrue();
    }

    @Test
    @DisplayName("dirContextPolicy defaults to SIMPLE and is round-trippable")
    void testDirContextPolicyField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getDirContextPolicy()).isEqualTo(DirContextPolicy.SIMPLE);
        props.setDirContextPolicy(DirContextPolicy.DEFAULT_TLS);
        assertThat(props.getDirContextPolicy()).isEqualTo(DirContextPolicy.DEFAULT_TLS);
    }

    @Test
    @DisplayName("authoritiesMapperPolicy defaults to NONE and is round-trippable")
    void testAuthoritiesMapperPolicyField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getAuthoritiesMapperPolicy()).isEqualTo(AuthoritiesMapperPolicy.NONE);
        props.setAuthoritiesMapperPolicy(AuthoritiesMapperPolicy.ROLE_HIERARCHY);
        assertThat(props.getAuthoritiesMapperPolicy()).isEqualTo(AuthoritiesMapperPolicy.ROLE_HIERARCHY);
    }

    @Test
    @DisplayName("providerUrl is round-trippable")
    void testProviderUrlField() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getProviderUrl()).isNull();
        props.setProviderUrl("ldap://192.168.0.1:389/dc=example,dc=com");
        assertThat(props.getProviderUrl()).isEqualTo("ldap://192.168.0.1:389/dc=example,dc=com");
    }

    @Test
    @DisplayName("captcha nested property is non-null by default")
    void testCaptchaNestedProperty() {
        SecurityLdapAuthcProperties props = newProps();
        assertThat(props.getCaptcha()).isNotNull();
    }

    @Test
    @DisplayName("Public constant 'PREFIX' has expected value")
    void testPREFIXConstant() {
        assertThat(SecurityLdapAuthcProperties.PREFIX).isEqualTo("spring.security.ldap.authc");
    }
}
