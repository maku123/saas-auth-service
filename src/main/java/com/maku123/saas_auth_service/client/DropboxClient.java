package com.maku123.saas_auth_service.client;

import com.maku123.saas_auth_service.config.DropboxConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class DropboxClient {

    private final RestTemplate restTemplate;
    private final DropboxConfig config;
    
    // Spring "injects" our shared tools from the config
    public DropboxClient(RestTemplate restTemplate, DropboxConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Exchanges the temporary code for permanent tokens.
     */
    public TokenResponse exchangeCodeForTokens(String authCode) {
        String tokenUrl = "https://api.dropboxapi.com/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authCode);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", config.getRedirectUri()); 
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // restTemplate converts the JSON response into a TokenResponse object
        return restTemplate.postForObject(tokenUrl, request, TokenResponse.class);
    }
}