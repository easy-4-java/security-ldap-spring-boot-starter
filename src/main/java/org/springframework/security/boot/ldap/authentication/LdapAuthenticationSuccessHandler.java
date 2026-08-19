package org.springframework.security.boot.ldap.authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.boot.biz.authentication.AuthenticationListener;
import org.springframework.security.boot.utils.WebUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.alibaba.fastjson.JSONObject;

/**
 * Post认证请求成功后的处理实现
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class LdapAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private List<AuthenticationListener> authenticationListeners;
	
	/**
	 * Constructs a new ldap authentication success handler instance.
	 *
	 * @param defaultTargetUrl the default target url
	 */
	public LdapAuthenticationSuccessHandler(String defaultTargetUrl) {
		this.setDefaultTargetUrl(defaultTargetUrl);
	}
	
	/**
	 * Constructs a new ldap authentication success handler instance.
	 *
	 * @param authenticationListeners the authentication listeners
	 * @param defaultTargetUrl the default target url
	 */
	public LdapAuthenticationSuccessHandler(List<AuthenticationListener> authenticationListeners, String defaultTargetUrl) {
		this.setAuthenticationListeners(authenticationListeners);
		this.setDefaultTargetUrl(defaultTargetUrl);
	}
	
	/**
	 * on Authentication Success.
	 *
	 * @param request the request
	 * @param response the response
	 * @param authentication the authentication
	 * @throws IOException if an error occurs
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		//调用事件监听器
		if(getAuthenticationListeners() != null && getAuthenticationListeners().size() > 0){
			for (AuthenticationListener authenticationListener : getAuthenticationListeners()) {
				authenticationListener.onSuccess(request, response, authentication);
			}
		}
		
		/*
		 * 判断是否Post请求
		 */
		if (WebUtils.isPostRequest(request)) {
			
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("status", "1");
			retMap.put("successUrl", getDefaultTargetUrl());

			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			
			JSONObject.writeJSONString(response.getWriter(), retMap);

			clearAuthenticationAttributes(request);
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}

	}

	/**
	 * Returns the authentication listeners.
	 *
	 * @return the authentication listeners
	 */
	public List<AuthenticationListener> getAuthenticationListeners() {
		return authenticationListeners;
	}

	/**
	 * Sets the authentication listeners.
	 *
	 * @param authenticationListeners the authentication listeners
	 */
	public void setAuthenticationListeners(List<AuthenticationListener> authenticationListeners) {
		this.authenticationListeners = authenticationListeners;
	}

}
