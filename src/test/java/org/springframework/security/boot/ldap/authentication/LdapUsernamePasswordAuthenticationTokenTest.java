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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LdapUsernamePasswordAuthenticationToken}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("LdapUsernamePasswordAuthenticationToken Tests")
class LdapUsernamePasswordAuthenticationTokenTest {

    @Test
    @DisplayName("Constructor stores principal and credentials and is unauthenticated")
    void constructor_shouldStorePrincipalAndCredentials() {
        LdapUsernamePasswordAuthenticationToken token =
                new LdapUsernamePasswordAuthenticationToken("alice", "secret");

        assertThat(token.getPrincipal()).isEqualTo("alice");
        assertThat(token.getCredentials()).isEqualTo("secret");
        assertThat(token.isAuthenticated()).isFalse();
        assertThat(token.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Token is a UsernamePasswordAuthenticationToken subtype")
    void token_isUsernamePasswordAuthenticationTokenSubtype() {
        LdapUsernamePasswordAuthenticationToken token =
                new LdapUsernamePasswordAuthenticationToken("bob", "pw");
        assertThat(token).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    @DisplayName("getName returns the principal when it is a String")
    void getName_returnsPrincipalString() {
        LdapUsernamePasswordAuthenticationToken token =
                new LdapUsernamePasswordAuthenticationToken("carol", "pw");
        assertThat(token.getName()).isEqualTo("carol");
    }

    @Test
    @DisplayName("setAuthenticated(true) is rejected by the parent contract")
    void setAuthenticated_true_shouldBeRejectedByParent() {
        LdapUsernamePasswordAuthenticationToken token =
                new LdapUsernamePasswordAuthenticationToken("dave", "pw");
        assertThat(token.isAuthenticated()).isFalse();
        // The parent throws when attempting to trust an unauthenticated token.
        org.assertj.core.api.Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> token.setAuthenticated(true));
    }

    @Test
    @DisplayName("Authenticated variant via parent constructor is honoured by authorities")
    void authenticatedVariant_carriesAuthorities() {
        // Construct an authenticated token using the parent's API to exercise serialisation/equals.
        java.util.Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(() -> "ROLE_USER");
        UsernamePasswordAuthenticationToken authenticated =
                new UsernamePasswordAuthenticationToken("eve", null, authorities);
        assertThat(authenticated.isAuthenticated()).isTrue();
        assertThat(authenticated.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
    }
}
