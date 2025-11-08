package com.maku123.saas_auth_service.service;

import com.maku123.saas_auth_service.client.DropboxClient;
import com.maku123.saas_auth_service.client.TokenResponse;
import com.maku123.saas_auth_service.db.TokenRepository;
import com.maku123.saas_auth_service.db.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final DropboxClient dropboxClient;
    private final TokenRepository tokenRepository;

    // Inject our "DropboxClient" and our "TokenRepository"
    public AuthService(DropboxClient dropboxClient, TokenRepository tokenRepository) {
        this.dropboxClient = dropboxClient;
        this.tokenRepository = tokenRepository;
    }

    public void handleAuthCallback(String authCode) {
        // 1. Get the tokens
        logger.info("Exchanging auth code for tokens...");
        TokenResponse tokens = dropboxClient.exchangeCodeForTokens(authCode);

        // 2. Create the token object to save
        TokenStorage tokenStorage = new TokenStorage(
            "dropbox", // The Primary Key for our database row
            tokens.getAccess_token(),
            tokens.getRefresh_token()
        );
        
        // 3. Save the tokens
        tokenRepository.save(tokenStorage);

        logger.info("Successfully retrieved and saved Dropbox tokens to database.");
    }
}