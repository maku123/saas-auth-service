package com.maku123.saas_auth_service.client;

// This class maps to the JSON response from Dropbox
// We must use snake_case (access_token) to match the JSON
public class TokenResponse {

    private String access_token;
    private String refresh_token;
    private Long expires_in;

    // Getters and Setters for Spring's JSON parser
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }
}