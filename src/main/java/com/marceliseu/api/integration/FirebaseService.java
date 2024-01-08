package com.marceliseu.api.integration;

import com.marceliseu.api.service.ServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;

public interface FirebaseService {

    public void addRole(@AuthenticationPrincipal Jwt jwt, @RequestParam String role) throws ServiceException;
}
