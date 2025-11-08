package com.maku123.saas_auth_service.controller;

import com.maku123.saas_auth_service.config.DropboxConfig;
import com.maku123.saas_auth_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final DropboxConfig config;

    public AuthController(AuthService authService, DropboxConfig config) {
        this.authService = authService;
        this.config = config;
    }

    /**
     * The starting point. User goes here to begin auth.
     * We use our config file to build the URL.
     */
    @GetMapping("/auth/start/dropbox")
    public RedirectView startAuth() {
        String authUrl = String.format(
                "https://www.dropbox.com/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s",
                urlEncode(config.getClientId()),
                urlEncode(config.getRedirectUri()),
                urlEncode(config.getScopes())
        );
        return new RedirectView(authUrl);
    }

    /**
     * The callback. Dropbox sends the user here after they click "Allow".
     */
    @GetMapping("/auth/callback")
    public String handleCallback(@RequestParam("code") String authCode) {
        try {
            // Handle the auth code
            authService.handleAuthCallback(authCode);
            
            String successHtml = """
                <h1>Success!</h1>
                <p>You have successfully authorized the application.</p>
                <p>You can now <a href="/h2-console" target="_blank">check the database</a> to see your saved token.</p>
                """;
            return successHtml;

        } catch (Exception e) {
            logger.error("An error occurred during auth flow: ", e);
            return "<h1>Error</h1><p>An error occurred: " + e.getMessage() + "</p>";
        }
    }
    
    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}