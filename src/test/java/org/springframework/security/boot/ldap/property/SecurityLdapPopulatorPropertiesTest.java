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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Unit tests for {@link SecurityLdapPopulatorProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityLdapPopulatorProperties Tests")
class SecurityLdapPopulatorPropertiesTest {

    @Test
    @DisplayName("Defaults match documented values")
    void defaults() {
        SecurityLdapPopulatorProperties props = new SecurityLdapPopulatorProperties();
        assertThat(props.getDefaultRole()).isNull();
        assertThat(props.getGroupRoleAttribute()).isEqualTo("cn");
        assertThat(props.getGroupSearchBase()).isNull();
        assertThat(props.isSearchSubtree()).isFalse();
        assertThat(props.getGroupSearchFilter()).isEqualTo("(member={0})");
        assertThat(props.getRolePrefix()).isEqualTo("ROLE_");
        assertThat(props.isConvertToUpperCase()).isTrue();
        assertThat(props.isIgnorePartialResultException()).isFalse();
    }

    @Test
    @DisplayName("Setters round-trip non-null values")
    void setters_roundTrip() {
        SecurityLdapPopulatorProperties props = new SecurityLdapPopulatorProperties();
        props.setDefaultRole("USER");
        props.setGroupRoleAttribute("dn");
        props.setGroupSearchBase("ou=groups");
        props.setSearchSubtree(true);
        props.setGroupSearchFilter("(uniqueMember={0})");
        props.setRolePrefix("GROUP_");
        props.setConvertToUpperCase(false);
        props.setIgnorePartialResultException(true);

        assertThat(props.getDefaultRole()).isEqualTo("USER");
        assertThat(props.getGroupRoleAttribute()).isEqualTo("dn");
        assertThat(props.getGroupSearchBase()).isEqualTo("ou=groups");
        assertThat(props.isSearchSubtree()).isTrue();
        assertThat(props.getGroupSearchFilter()).isEqualTo("(uniqueMember={0})");
        assertThat(props.getRolePrefix()).isEqualTo("GROUP_");
        assertThat(props.isConvertToUpperCase()).isFalse();
        assertThat(props.isIgnorePartialResultException()).isTrue();
    }

    @Test
    @DisplayName("setDefaultRole(null) should reject with IllegalArgumentException")
    void setDefaultRole_null_shouldReject() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SecurityLdapPopulatorProperties().setDefaultRole(null));
    }

    @Test
    @DisplayName("setGroupRoleAttribute(null) should reject with IllegalArgumentException")
    void setGroupRoleAttribute_null_shouldReject() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SecurityLdapPopulatorProperties().setGroupRoleAttribute(null));
    }

    @Test
    @DisplayName("setGroupSearchFilter(null) should reject with IllegalArgumentException")
    void setGroupSearchFilter_null_shouldReject() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SecurityLdapPopulatorProperties().setGroupSearchFilter(null));
    }

    @Test
    @DisplayName("setRolePrefix(null) should reject with IllegalArgumentException")
    void setRolePrefix_null_shouldReject() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SecurityLdapPopulatorProperties().setRolePrefix(null));
    }
}
