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
package org.springframework.security.boot.ldap.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.boot.SecurityLdapProperties;
import org.springframework.security.boot.ldap.property.SecurityActiveDirectoryLdapProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LadpAuthenticationProcessingFilter}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("LadpAuthenticationProcessingFilter Tests")
class LadpAuthenticationProcessingFilterTest {

    @Test
    @DisplayName("Constructor wires the ObjectMapper and properties")
    void constructor_wiresDependencies() {
        ObjectMapper mapper = new ObjectMapper();
        SecurityLdapProperties props = new SecurityLdapProperties();
        LadpAuthenticationProcessingFilter filter = new LadpAuthenticationProcessingFilter(mapper, props);

        assertThat(filter).isNotNull();
        assertThat(filter.getObjectMapper()).isSameAs(mapper);
    }

    @Test
    @DisplayName("authenticationToken returns LdapUsernamePasswordAuthenticationToken when AD is disabled")
    void authenticationToken_adDisabled_returnsLdapToken() {
        ObjectMapper mapper = new ObjectMapper();
        SecurityLdapProperties props = new SecurityLdapProperties();
        // Active Directory defaults to disabled.
        assertThat(props.getActiveDirectory().isEnabled()).isFalse();

        LadpAuthenticationProcessingFilter filter = new LadpAuthenticationProcessingFilter(mapper, props);
        AbstractAuthenticationToken token = filter.authenticationToken("alice", "secret");

        assertThat(token).isInstanceOf(LdapUsernamePasswordAuthenticationToken.class);
        assertThat(token.getPrincipal()).isEqualTo("alice");
        assertThat(token.getCredentials()).isEqualTo("secret");
    }

    @Test
    @DisplayName("authenticationToken delegates to super when AD is enabled")
    void authenticationToken_adEnabled_delegatesToSuper() {
        ObjectMapper mapper = new ObjectMapper();
        SecurityLdapProperties props = new SecurityLdapProperties();
        SecurityActiveDirectoryLdapProperties ad = props.getActiveDirectory();
        ad.setEnabled(true);

        LadpAuthenticationProcessingFilter filter = new LadpAuthenticationProcessingFilter(mapper, props);
        AbstractAuthenticationToken token = filter.authenticationToken("bob", "pwd");

        // Super builds a plain UsernamePasswordAuthenticationToken (not the LDAP subtype).
        assertThat(token).isNotInstanceOf(LdapUsernamePasswordAuthenticationToken.class);
        assertThat(token.getPrincipal()).isEqualTo("bob");
        assertThat(token.getCredentials()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("Filter is constructed with the LDAP properties wired in")
    void filter_carriesLdapProperties() {
        ObjectMapper mapper = new ObjectMapper();
        SecurityLdapProperties props = new SecurityLdapProperties();
        LadpAuthenticationProcessingFilter filter = new LadpAuthenticationProcessingFilter(mapper, props);

        // The constructor stores ldapProperties; verify via the AD-disabled branch which reads it.
        // authenticationToken returns the LDAP token type only when AD is disabled (the default).
        AbstractAuthenticationToken token = filter.authenticationToken("u", "p");
        assertThat(token).isInstanceOf(LdapUsernamePasswordAuthenticationToken.class);
    }
}
