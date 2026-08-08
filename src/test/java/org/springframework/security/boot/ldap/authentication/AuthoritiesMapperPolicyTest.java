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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuthoritiesMapperPolicy}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("AuthoritiesMapperPolicy Tests")
class AuthoritiesMapperPolicyTest {

    @Test
    @DisplayName("Should expose exactly the expected constants")
    void shouldExposeExpectedConstants() {
        assertThat(AuthoritiesMapperPolicy.values())
                .containsExactly(
                        AuthoritiesMapperPolicy.ROLE_HIERARCHY,
                        AuthoritiesMapperPolicy.SIMPLE,
                        AuthoritiesMapperPolicy.NONE);
    }

    @Test
    @DisplayName("valueOf should resolve each constant by name")
    void valueOf_shouldResolveByName() {
        assertThat(AuthoritiesMapperPolicy.valueOf("ROLE_HIERARCHY"))
                .isSameAs(AuthoritiesMapperPolicy.ROLE_HIERARCHY);
        assertThat(AuthoritiesMapperPolicy.valueOf("SIMPLE"))
                .isSameAs(AuthoritiesMapperPolicy.SIMPLE);
        assertThat(AuthoritiesMapperPolicy.valueOf("NONE"))
                .isSameAs(AuthoritiesMapperPolicy.NONE);
    }

    @Test
    @DisplayName("Custom equals should agree with compareTo")
    void equals_shouldReflectCompareTo() {
        assertThat(AuthoritiesMapperPolicy.ROLE_HIERARCHY.equals(AuthoritiesMapperPolicy.ROLE_HIERARCHY)).isTrue();
        assertThat(AuthoritiesMapperPolicy.SIMPLE.equals(AuthoritiesMapperPolicy.SIMPLE)).isTrue();
        assertThat(AuthoritiesMapperPolicy.NONE.equals(AuthoritiesMapperPolicy.NONE)).isTrue();

        assertThat(AuthoritiesMapperPolicy.ROLE_HIERARCHY.equals(AuthoritiesMapperPolicy.SIMPLE)).isFalse();
        assertThat(AuthoritiesMapperPolicy.SIMPLE.equals(AuthoritiesMapperPolicy.NONE)).isFalse();
        assertThat(AuthoritiesMapperPolicy.NONE.equals(AuthoritiesMapperPolicy.ROLE_HIERARCHY)).isFalse();
    }

    @Test
    @DisplayName("compareTo should define a stable ordering")
    void compareTo_shouldDefineStableOrdering() {
        assertThat(AuthoritiesMapperPolicy.ROLE_HIERARCHY.compareTo(AuthoritiesMapperPolicy.SIMPLE)).isNegative();
        assertThat(AuthoritiesMapperPolicy.SIMPLE.compareTo(AuthoritiesMapperPolicy.NONE)).isNegative();
        assertThat(AuthoritiesMapperPolicy.NONE.compareTo(AuthoritiesMapperPolicy.ROLE_HIERARCHY)).isPositive();
    }
}
