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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.boot.biz.authentication.AuthenticationListener;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link LdapAuthenticationSuccessHandler}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("LdapAuthenticationSuccessHandler Tests")
class LdapAuthenticationSuccessHandlerTest {

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken("user", "pwd");
    }

    @Test
    @DisplayName("Constructor with defaultTargetUrl sets default target url")
    void constructor_defaultTargetUrl() {
        LdapAuthenticationSuccessHandler handler = new LdapAuthenticationSuccessHandler("/home");
        // getDefaultTargetUrl() is protected in Spring Security 7; verify indirectly via the
        // POST JSON output (which echoes the default target url as "successUrl").
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/ldap");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            handler.onAuthenticationSuccess(request, response, authentication());
            assertThat(response.getContentAsString()).contains("\"successUrl\":\"/home\"");
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertThat(handler.getAuthenticationListeners()).isNull();
    }

    @Test
    @DisplayName("Constructor with listeners sets both fields")
    void constructor_withListeners() {
        AuthenticationListener listener = mock(AuthenticationListener.class);
        LdapAuthenticationSuccessHandler handler =
                new LdapAuthenticationSuccessHandler(Collections.singletonList(listener), "/welcome");
        // Verify the listeners field via the public getter.
        assertThat(handler.getAuthenticationListeners()).containsExactly(listener);
    }

    @Test
    @DisplayName("POST request writes JSON status/successUrl and invokes listeners")
    void onAuthenticationSuccess_postRequest_writesJsonAndNotifiesListeners() throws Exception {
        AuthenticationListener listener = mock(AuthenticationListener.class);
        LdapAuthenticationSuccessHandler handler =
                new LdapAuthenticationSuccessHandler(Arrays.asList(listener), "/done");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/ldap");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString()).contains("\"status\":\"1\"").contains("\"successUrl\":\"/done\"");
        verify(listener).onSuccess(request, response, authentication());
    }

    @Test
    @DisplayName("POST request without listeners does not NPE")
    void onAuthenticationSuccess_postRequest_noListeners() throws Exception {
        LdapAuthenticationSuccessHandler handler = new LdapAuthenticationSuccessHandler("/done");
        handler.setAuthenticationListeners(Collections.emptyList());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/ldap");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication());
        assertThat(response.getContentAsString()).contains("\"status\":\"1\"");
    }

    @Test
    @DisplayName("GET request delegates to the super implementation")
    void onAuthenticationSuccess_getRequest_delegatesToSuper() throws Exception {
        AuthenticationListener listener = mock(AuthenticationListener.class);
        // Use a stub target url so super.onAuthenticationSuccess performs a redirect without error.
        LdapAuthenticationSuccessHandler handler =
                new LdapAuthenticationSuccessHandler(Arrays.asList(listener), "/default");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/anywhere");
        request.setContextPath("");
        // Super requires a session to clear authentication attributes; provide a mock session.
        request.setSession(new org.springframework.mock.web.MockHttpSession());
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication());

        // Listener is still notified regardless of request method.
        verify(listener).onSuccess(request, response, authentication());
        // The super implementation performs a redirect to the default target url.
        assertThat(response.getRedirectedUrl()).isEqualTo("/default");
    }

    @Test
    @DisplayName("setAuthenticationListeners round-trips the value")
    void setAuthenticationListeners_roundTrip() {
        LdapAuthenticationSuccessHandler handler = new LdapAuthenticationSuccessHandler("/x");
        AuthenticationListener listener = mock(AuthenticationListener.class);
        handler.setAuthenticationListeners(Collections.singletonList(listener));
        assertThat(handler.getAuthenticationListeners()).containsExactly(listener);
    }

    @Test
    @DisplayName("Writer-based output path is exercised even when response wraps a string writer")
    void writeJsonString_exercisesFastjsonPath() throws Exception {
        // Cover JSONObject.writeJSONString(writer, map) indirectly: the success handler calls it
        // against response.getWriter(). Mock the response so getWriter returns a real writer.
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // Use a mock request flagged as POST via WebUtils (method == POST).
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");

        LdapAuthenticationSuccessHandler handler = new LdapAuthenticationSuccessHandler("/out");
        handler.onAuthenticationSuccess(request, response, authentication());

        assertThat(sw.toString()).contains("\"successUrl\":\"/out\"");
        // clearAuthenticationAttributes is invoked on the POST path.
        verify(request, never()).getParameterMap();
    }
}
