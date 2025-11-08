package com.maku123.saas_auth_service.service;

import com.maku123.saas_auth_service.client.DropboxClient;
import com.maku123.saas_auth_service.db.TokenRepository;
import com.maku123.saas_auth_service.db.TokenStorage;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    private final DropboxClient dropboxClient;
    private final TokenRepository tokenRepository;

    public DataService(DropboxClient dropboxClient, TokenRepository tokenRepository) {
        this.dropboxClient = dropboxClient;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Finds the "dropbox" token from the database, and returns it.
     */
    private String getAccessTokenFromDb() {
        TokenStorage tokens = tokenRepository.findById("dropbox")
                .orElseThrow(() -> new RuntimeException("Tokens not found in database. Please authorize first via /auth/start/dropbox"));
        
        return tokens.getAccessToken();
    }

    public String getTeamInfo() {
        String token = getAccessTokenFromDb();
        return dropboxClient.getTeamInfo(token);
    }
    
    public String getUsersList() {
        String token = getAccessTokenFromDb();
        return dropboxClient.getUsersList(token);
    }
    
    public String getSignInEvents() {
        String token = getAccessTokenFromDb();
        return dropboxClient.getSignInEvents(token);
    }
}