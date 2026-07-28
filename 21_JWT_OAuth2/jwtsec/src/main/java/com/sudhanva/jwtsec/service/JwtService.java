    package com.sudhanva.jwtsec.service;

    import java.sql.Time;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.function.Function;

    import javax.crypto.SecretKey;

    import org.jspecify.annotations.Nullable;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;

    import com.sudhanva.jwtsec.Config.JwtConfig;
    import com.sudhanva.jwtsec.model.User;
    import com.sudhanva.jwtsec.repo.UserRepo;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;


    @Service
    public class JwtService{


        private final UserRepo userRepo;
        private final JwtConfig jwtConfig;

        JwtService(
            UserRepo userRepo,
            JwtConfig jwtConfig
        ) {
            this.userRepo = userRepo;
            this.jwtConfig = jwtConfig;
        }


        public SecretKey getSigningKey(){
            byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
            System.out.println("\n"+"SecrteKey: "+jwtConfig.getSecret()+"\n");
            System.out.println("SecrteKey: "+keyBytes+"\n");
            return Keys.hmacShaKeyFor(keyBytes);
        }


        public String generateToken(String username) {
            
            // -----------------------------------------------------------------
            // Claims = Extra information stored inside the JWT payload.
            //
            // Some claims are predefined:
            //   sub -> Subject (usually username)
            //   exp -> Expiration time
            //   iat -> Issued at
            //
            // You can also add custom claims like:
            //   role
            //   email
            //   userId
            // -----------------------------------------------------------------
            Map<String,Object> claims = new HashMap<>();

            User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            //  .orElse(null);

            claims.put("role", user.getRole());

            return Jwts.builder()
                // These become part of the JWT payload.
                .claims(claims)
                //  Usually stores the username or user id.
                .subject(username)
                // Time when this JWT was created.
                .issuedAt(new Date())
                //  Token expiry time.
                // Here: 1 minutes from now.
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))

                .signWith(getSigningKey())
                // Build the JWT and convert it into a compact String.
                .compact();

        }




        /**
         * Generic method to extract any claim.
        */
        public <T> @Nullable T extractClaim(
            String token,
            Function<Claims, @Nullable T> claimsResolver
        ) {

            Claims claims = extractAllClaims(token);
            // claim resolver holds the function ref to be called isnide
            return claimsResolver.apply(claims);
        }


        /**
         * Parse JWT and return all claims.
        */
        private Claims extractAllClaims(String token){
            return Jwts.parser()
                        // Secret key used for verification
                        .verifyWith(getSigningKey())
                        // Parse JWT
                        .build()
                        // Verify signature & decode
                        .parseSignedClaims(token)
                        // Return payload
                        .getPayload();
        }


        /**
         * Extract username (Subject) from JWT.
         */
        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        /**
         * Extract expiration date.
         */
        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }


        /**
         * Check whether token has expired.
         */
        private boolean isTokenExpired(String token) {

            return extractExpiration(token)
                    .before(new Date());
        }

        /**
         * Validate JWT.
         */
        public boolean validateToken(
            String token,
            UserDetails userDetails
        ) {

            // This is i am passing
            // String username = extractUsername(token);

            Claims claims = extractAllClaims(token);

            return claims.getSubject()
                    .equals(userDetails.getUsername())
                        && claims.getExpiration().after(new Date());
            // userDeails is from DB
            //  return username.equals(userDetails.getUsername())
            //         && !isTokenExpired(token);
        }


    }
