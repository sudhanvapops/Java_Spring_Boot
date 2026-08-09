package com.sudhanva.library_management_v2.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhanva.library_management_v2.Model.RefreshToken;
import java.util.List;
import java.util.Optional;


public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long>{
    Optional<RefreshToken> findByJti(String jti);

}
