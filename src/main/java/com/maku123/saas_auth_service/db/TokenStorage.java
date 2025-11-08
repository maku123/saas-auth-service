package com.maku123.saas_auth_service.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TokenStorage {

    @Id // This is the Primary Key
    private String serviceName; // e.g., "dropbox"

    @Column(length = 1024)
    private String accessToken;

    @Column(length = 1024)
    private String refreshToken;

    // Constructors
    public TokenStorage() {
    }

    public TokenStorage(String serviceName, String accessToken, String refreshToken) {
        this.serviceName = serviceName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters (Needed by JPA)
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
