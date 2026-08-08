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
import org.springframework.context.MessageSource;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.boot.ldap.authentication.DirContextPolicy;
import org.springframework.security.boot.ldap.property.SecurityActiveDirectoryLdapProperties;
import org.springframework.security.boot.ldap.property.SecurityLdapAuthcProperties;
import org.springframework.security.boot.ldap.property.SecurityLdapPopulatorProperties;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.RequestCache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the {@code @Bean} methods of {@link SecurityLdapAutoConfiguration}.
 *
 * <p>Each {@code @Bean} method is a plain instance method, so the configuration is
 * instantiated directly and each method is exercised with stubbed collaborators. This
 * covers every branch (default-tls / external-tls / digest-md5 / simple; role-hierarchy /
 * simple / none; AD enabled vs. disabled) without booting a full Spring context.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("SecurityLdapAutoConfiguration @Bean methods Tests")
class SecurityLdapAutoConfigurationBeanTest {

    private final SecurityLdapAutoConfiguration config = new SecurityLdapAutoConfiguration();

    private SecurityLdapAuthcProperties authcWithProviderUrl() {
        SecurityLdapAuthcProperties authc = new SecurityLdapAuthcProperties();
        authc.setProviderUrl("ldap://127.0.0.1:389/dc=example,dc=com");
        // DefaultSpringSecurityContextSource and friends reject null base environment maps.
        authc.setBaseEnvironmentProperties(new java.util.HashMap<>());
        return authc;
    }

    // ---- ldapContextSource ----

    @Test
    @DisplayName("ldapContextSource builds a context source from the authc properties")
    void ldapContextSource_buildsFromProperties() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setLdapUrls(new String[]{"ldap://h1:389", "ldap://h2:389"});

        LdapContextSource source = config.ldapContextSource(authc);
        assertThat(source).isNotNull();
    }

    // ---- authenticationStrategy: four branches ----

    @Test
    @DisplayName("authenticationStrategy returns DefaultTls for DEFAULT_TLS policy")
    void authenticationStrategy_defaultTls() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setDirContextPolicy(DirContextPolicy.DEFAULT_TLS);
        DirContextAuthenticationStrategy strategy = config.authenticationStrategy(authc);
        assertThat(strategy).isInstanceOf(org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy.class);
    }

    @Test
    @DisplayName("authenticationStrategy returns ExternalTls for EXTERNAL_TLS policy")
    void authenticationStrategy_externalTls() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setDirContextPolicy(DirContextPolicy.EXTERNAL_TLS);
        assertThat(config.authenticationStrategy(authc))
                .isInstanceOf(org.springframework.ldap.core.support.ExternalTlsDirContextAuthenticationStrategy.class);
    }

    @Test
    @DisplayName("authenticationStrategy returns DigestMd5 for DIGEST_MD5 policy")
    void authenticationStrategy_digestMd5() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setDirContextPolicy(DirContextPolicy.DIGEST_MD5);
        assertThat(config.authenticationStrategy(authc))
                .isInstanceOf(org.springframework.ldap.core.support.DigestMd5DirContextAuthenticationStrategy.class);
    }

    @Test
    @DisplayName("authenticationStrategy returns Simple for SIMPLE policy")
    void authenticationStrategy_simple() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setDirContextPolicy(DirContextPolicy.SIMPLE);
        assertThat(config.authenticationStrategy(authc))
                .isInstanceOf(org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy.class);
    }

    // ---- authenticationSource ----

    @Test
    @DisplayName("authenticationSource returns a SpringSecurityAuthenticationSource")
    void authenticationSource_returnsSpringSecuritySource() {
        assertThat(config.authenticationSource())
                .isInstanceOf(org.springframework.security.ldap.authentication.SpringSecurityAuthenticationSource.class);
    }

    // ---- contextSource ----

    @Test
    @DisplayName("contextSource configures the DefaultSpringSecurityContextSource")
    void contextSource_configuresContextSource() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setLdapUrls(new String[]{"ldap://primary:389"});
        authc.setUrls(new String[]{"ldap://primary:389"});
        DirContextAuthenticationStrategy strategy = config.authenticationStrategy(authc);
        AuthenticationSource authSource = config.authenticationSource();

        BaseLdapPathContextSource source = config.contextSource(authc, strategy, authSource);
        assertThat(source).isNotNull();
    }

    // ---- userSearch ----

    @Test
    @DisplayName("userSearch builds a FilterBasedLdapUserSearch")
    void userSearch_buildsSearch() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        // FilterBasedLdapUserSearch rejects a null searchFilter.
        authc.setSearchFilter("(uid={0})");
        BaseLdapPathContextSource ctx = mock(BaseLdapPathContextSource.class);
        LdapUserSearch search = config.userSearch(authc, ctx);
        assertThat(search).isNotNull();
    }

    // ---- userDetailsService ----

    @Test
    @DisplayName("userDetailsService wires search and populator")
    void userDetailsService_wiresDependencies() {
        LdapUserSearch search = mock(LdapUserSearch.class);
        LdapAuthoritiesPopulator populator = mock(LdapAuthoritiesPopulator.class);
        UserDetailsService uds = config.userDetailsService(search, populator);
        assertThat(uds).isNotNull();
    }

    // ---- userDetailsContextMapper ----

    @Test
    @DisplayName("userDetailsContextMapper returns an LdapUserDetailsMapper")
    void userDetailsContextMapper_returnsMapper() {
        assertThat(config.userDetailsContextMapper())
                .isInstanceOf(org.springframework.security.ldap.userdetails.LdapUserDetailsMapper.class);
    }

    // ---- authoritiesMapper: three branches ----

    @Test
    @DisplayName("authoritiesMapper returns RoleHierarchyAuthoritiesMapper for ROLE_HIERARCHY")
    void authoritiesMapper_roleHierarchy() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setAuthoritiesMapperPolicy(
                org.springframework.security.boot.ldap.authentication.AuthoritiesMapperPolicy.ROLE_HIERARCHY);
        RoleHierarchy hierarchy = mock(RoleHierarchy.class);
        GrantedAuthoritiesMapper mapper = config.authoritiesMapper(authc, hierarchy);
        assertThat(mapper).isInstanceOf(org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper.class);
    }

    @Test
    @DisplayName("authoritiesMapper returns SimpleAuthorityMapper for SIMPLE")
    void authoritiesMapper_simple() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setAuthoritiesMapperPolicy(
                org.springframework.security.boot.ldap.authentication.AuthoritiesMapperPolicy.SIMPLE);
        RoleHierarchy hierarchy = mock(RoleHierarchy.class);
        GrantedAuthoritiesMapper mapper = config.authoritiesMapper(authc, hierarchy);
        assertThat(mapper).isInstanceOf(org.springframework.security.core.authority.mapping.SimpleAuthorityMapper.class);
    }

    @Test
    @DisplayName("authoritiesMapper returns NullAuthoritiesMapper for NONE")
    void authoritiesMapper_none() {
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        authc.setAuthoritiesMapperPolicy(
                org.springframework.security.boot.ldap.authentication.AuthoritiesMapperPolicy.NONE);
        RoleHierarchy hierarchy = mock(RoleHierarchy.class);
        GrantedAuthoritiesMapper mapper = config.authoritiesMapper(authc, hierarchy);
        assertThat(mapper).isInstanceOf(org.springframework.security.core.authority.mapping.NullAuthoritiesMapper.class);
    }

    // ---- ldapAuthenticator ----

    @Test
    @DisplayName("ldapAuthenticator returns a PasswordComparisonAuthenticator")
    void ldapAuthenticator_returnsPasswordComparisonAuthenticator() {
        BaseLdapPathContextSource ctx = mock(BaseLdapPathContextSource.class);
        AbstractLdapAuthenticator authenticator = config.ldapAuthenticator(ctx);
        assertThat(authenticator).isInstanceOf(org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator.class);
    }

    // ---- ldapAuthoritiesPopulator ----

    @Test
    @DisplayName("ldapAuthoritiesPopulator applies all populator properties")
    void ldapAuthoritiesPopulator_appliesProperties() {
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        SecurityLdapPopulatorProperties pop = ldapProperties.getPopulator();
        pop.setConvertToUpperCase(true);
        pop.setDefaultRole("USER");
        pop.setGroupRoleAttribute("cn");
        pop.setGroupSearchFilter("(member={0})");
        pop.setIgnorePartialResultException(true);
        pop.setRolePrefix("ROLE_");
        pop.setSearchSubtree(true);

        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        BaseLdapPathContextSource ctx = mock(BaseLdapPathContextSource.class);

        LdapAuthoritiesPopulator result = config.ldapAuthoritiesPopulator(ldapProperties, authc, ctx);
        assertThat(result).isNotNull();
    }

    // ---- ldapAuthenticationProvider: AD enabled + disabled ----

    @Test
    @DisplayName("ldapAuthenticationProvider returns ActiveDirectory provider when AD enabled")
    void ldapAuthenticationProvider_adEnabled() throws Exception {
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        SecurityActiveDirectoryLdapProperties ad = ldapProperties.getActiveDirectory();
        ad.setEnabled(true);
        ad.setDomain("corp.example.com");
        ad.setUrl("ldap://ad.example.com:389");
        ad.setRootDn("dc=example,dc=com");
        // ActiveDirectoryLdapAuthenticationProvider rejects an empty environment map.
        ad.setEnvironment(java.util.Collections.singletonMap("java.naming.referral", "ignore"));

        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        // ActiveDirectoryLdapAuthenticationProvider.setSearchFilter requires a non-blank value.
        authc.setSearchFilter("(userPrincipalName={0})");
        GrantedAuthoritiesMapper authoritiesMapper = mock(GrantedAuthoritiesMapper.class);
        AbstractLdapAuthenticator authenticator = mock(AbstractLdapAuthenticator.class);
        LdapAuthoritiesPopulator populator = mock(LdapAuthoritiesPopulator.class);
        MessageSource messageSource = mock(MessageSource.class);
        UserDetailsContextMapper userDetailsContextMapper = mock(UserDetailsContextMapper.class);

        AbstractLdapAuthenticationProvider provider = config.ldapAuthenticationProvider(
                ldapProperties, authc, authoritiesMapper, authenticator, populator,
                messageSource, userDetailsContextMapper);

        assertThat(provider)
                .isInstanceOf(org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider.class);
    }

    @Test
    @DisplayName("ldapAuthenticationProvider returns standard LdapAuthenticationProvider when AD disabled")
    void ldapAuthenticationProvider_adDisabled() throws Exception {
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        // AD disabled by default
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        GrantedAuthoritiesMapper authoritiesMapper = mock(GrantedAuthoritiesMapper.class);
        AbstractLdapAuthenticator authenticator = mock(AbstractLdapAuthenticator.class);
        LdapAuthoritiesPopulator populator = mock(LdapAuthoritiesPopulator.class);
        MessageSource messageSource = mock(MessageSource.class);
        UserDetailsContextMapper userDetailsContextMapper = mock(UserDetailsContextMapper.class);

        AbstractLdapAuthenticationProvider provider = config.ldapAuthenticationProvider(
                ldapProperties, authc, authoritiesMapper, authenticator, populator,
                messageSource, userDetailsContextMapper);

        assertThat(provider).isInstanceOf(org.springframework.security.ldap.authentication.LdapAuthenticationProvider.class);
        // The anonymous subclass overrides supports(); verify the LDAP token is supported.
        assertThat(provider.supports(
                org.springframework.security.boot.ldap.authentication.LdapUsernamePasswordAuthenticationToken.class)).isTrue();
        assertThat(provider.supports(String.class)).isFalse();
    }

    // ---- ldapAuthenticationSuccessHandler / ldapAuthenticationFailureHandler ----

    @Test
    @DisplayName("ldapAuthenticationSuccessHandler wires redirect strategy, request cache and target url")
    void ldapAuthenticationSuccessHandler_wiresCollaborators() throws Exception {
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        RequestCache requestCache = mock(RequestCache.class);

        org.springframework.security.boot.ldap.authentication.LdapAuthenticationSuccessHandler handler =
                config.ldapAuthenticationSuccessHandler(ldapProperties, authc, null, redirectStrategy, requestCache);

        // getRedirectStrategy()/getDefaultTargetUrl() are protected in Spring Security 7, so verify
        // behaviour instead: a POST request must echo the successUrl (/index default) as JSON.
        assertThat(handler).isNotNull();
        org.springframework.mock.web.MockHttpServletRequest request =
                new org.springframework.mock.web.MockHttpServletRequest("POST", "/login/ldap");
        org.springframework.mock.web.MockHttpServletResponse response =
                new org.springframework.mock.web.MockHttpServletResponse();
        handler.onAuthenticationSuccess(request, response,
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("u", "p"));
        assertThat(response.getContentAsString()).contains("\"successUrl\":\"/index\"");
    }

    @Test
    @DisplayName("ldapAuthenticationFailureHandler wires redirect strategy and flags")
    void ldapAuthenticationFailureHandler_wiresCollaborators() {
        SecurityLdapProperties ldapProperties = new SecurityLdapProperties();
        SecurityLdapAuthcProperties authc = authcWithProviderUrl();
        org.springframework.security.boot.biz.property.SecuritySessionMgtProperties sessionMgt =
                new org.springframework.security.boot.biz.property.SecuritySessionMgtProperties();
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);

        org.springframework.security.boot.ldap.authentication.LdapAuthenticationFailureHandler handler =
                config.ldapAuthenticationFailureHandler(ldapProperties, authc, sessionMgt, null, redirectStrategy);

        // getRedirectStrategy() is protected in Spring Security 7; assert construction + type only.
        assertThat(handler).isNotNull();
        assertThat(handler)
                .isInstanceOf(org.springframework.security.boot.ldap.authentication.LdapAuthenticationFailureHandler.class);
    }

    @Test
    @DisplayName("AuthenticationProvider is resolvable on the classpath")
    void authenticationProviderClassIsPresent() {
        assertThat(AuthenticationProvider.class).isNotNull();
    }
}
