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
 * Unit tests for {@link DirContextPolicy}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("DirContextPolicy Tests")
class DirContextPolicyTest {

    @Test
    @DisplayName("Should expose exactly the expected constants")
    void shouldExposeExpectedConstants() {
        assertThat(DirContextPolicy.values())
                .containsExactly(
                        DirContextPolicy.DEFAULT_TLS,
                        DirContextPolicy.EXTERNAL_TLS,
                        DirContextPolicy.DIGEST_MD5,
                        DirContextPolicy.SIMPLE);
    }

    @Test
    @DisplayName("valueOf should resolve each constant by name")
    void valueOf_shouldResolveByName() {
        assertThat(DirContextPolicy.valueOf("DEFAULT_TLS")).isSameAs(DirContextPolicy.DEFAULT_TLS);
        assertThat(DirContextPolicy.valueOf("EXTERNAL_TLS")).isSameAs(DirContextPolicy.EXTERNAL_TLS);
        assertThat(DirContextPolicy.valueOf("DIGEST_MD5")).isSameAs(DirContextPolicy.DIGEST_MD5);
        assertThat(DirContextPolicy.valueOf("SIMPLE")).isSameAs(DirContextPolicy.SIMPLE);
    }

    @Test
    @DisplayName("Custom equals should agree with compareTo")
    void equals_shouldReflectCompareTo() {
        assertThat(DirContextPolicy.DEFAULT_TLS.equals(DirContextPolicy.DEFAULT_TLS)).isTrue();
        assertThat(DirContextPolicy.EXTERNAL_TLS.equals(DirContextPolicy.EXTERNAL_TLS)).isTrue();
        assertThat(DirContextPolicy.DIGEST_MD5.equals(DirContextPolicy.DIGEST_MD5)).isTrue();
        assertThat(DirContextPolicy.SIMPLE.equals(DirContextPolicy.SIMPLE)).isTrue();

        assertThat(DirContextPolicy.DEFAULT_TLS.equals(DirContextPolicy.SIMPLE)).isFalse();
        assertThat(DirContextPolicy.EXTERNAL_TLS.equals(DirContextPolicy.DIGEST_MD5)).isFalse();
        assertThat(DirContextPolicy.DIGEST_MD5.equals(DirContextPolicy.DEFAULT_TLS)).isFalse();
        assertThat(DirContextPolicy.SIMPLE.equals(DirContextPolicy.EXTERNAL_TLS)).isFalse();
    }

    @Test
    @DisplayName("compareTo should define a stable ordering")
    void compareTo_shouldDefineStableOrdering() {
        assertThat(DirContextPolicy.DEFAULT_TLS.compareTo(DirContextPolicy.EXTERNAL_TLS)).isNegative();
        assertThat(DirContextPolicy.EXTERNAL_TLS.compareTo(DirContextPolicy.DIGEST_MD5)).isNegative();
        assertThat(DirContextPolicy.DIGEST_MD5.compareTo(DirContextPolicy.SIMPLE)).isNegative();
        assertThat(DirContextPolicy.SIMPLE.compareTo(DirContextPolicy.DEFAULT_TLS)).isPositive();
    }
}
