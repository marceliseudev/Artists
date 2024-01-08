package com.marceliseu.api.security.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

	/**
	 * Recover caller JWT token 
	 *
	 * @return JWT token value
	 */
	public String readCallerToken() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof JwtAuthenticationToken) {
			return ((JwtAuthenticationToken) auth).getToken().getTokenValue();
		} else {
			return "";
		}
	}
	
	/**
	 * Recover caller JWT 
	 *
	 * @return JWT 
	 */
	public Jwt readCallerJwt() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof JwtAuthenticationToken) {
			return ((Jwt) auth.getPrincipal());
		} else {
			return null;
		}
	}

	/**
	 * Recover caller name from token
	 *
	 * @return JWT token value
	 */
	public String readCallerUserId() {
		String userid = "";
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof AbstractAuthenticationToken) {
			userid = ((AbstractAuthenticationToken) auth).getName();
		}
		return userid;
	}

	/**
	 * Recover Authorities Roles from token
	 *
	 * @return JWT token value
	 */
	public boolean callerHasRole(String authRole) {
		boolean hasRole = false;
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof AbstractAuthenticationToken) {
			hasRole = ((AbstractAuthenticationToken) auth).getAuthorities()
					.contains(new SimpleGrantedAuthority(authRole));
		}
		return hasRole;
	}

}
