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
    
    public DropboxClient(RestTemplate restTemplate, DropboxConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Method 1: Exchanges the temporary code for permanent tokens.
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

        return restTemplate.postForObject(tokenUrl, request, TokenResponse.class);
    }

    /**
     * Method 2: Calls the /team/get_info API
     */
    public String getTeamInfo(String accessToken) {
        String apiUrl = "https://api.dropboxapi.com/2/team/get_info";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "null";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        return restTemplate.postForObject(apiUrl, entity, String.class);
    }

    /**
     * Method 3: Calls the /team/members/list_v2 API
     */
    public String getUsersList(String accessToken) {
        String apiUrl = "https://api.dropboxapi.com/2/team/members/list_v2";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = "{\"limit\": 100}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        return restTemplate.postForObject(apiUrl, entity, String.class);
    }

    /**
     * Method 4: Calls the /team_log/get_events API
     */
    public String getSignInEvents(String accessToken) {
        String apiUrl = "https://api.dropboxapi.com/2/team_log/get_events";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = "{\"category\": \"logins\", \"limit\": 20}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        return restTemplate.postForObject(apiUrl, entity, String.class);
    }
}