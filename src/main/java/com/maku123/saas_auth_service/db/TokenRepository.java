package com.maku123.saas_auth_service.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenStorage, String> {
    // By extending JpaRepository, we get all these methods:
    // - save(TokenStorage token)
    // - findById(String serviceName)
    // - delete(TokenStorage token)
    // - ...
}