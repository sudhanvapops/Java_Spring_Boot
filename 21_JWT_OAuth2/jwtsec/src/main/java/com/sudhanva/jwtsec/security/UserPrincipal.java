package com.sudhanva.jwtsec.security;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sudhanva.jwtsec.model.User;



// User → what the object represents.
// Principal → the security identity of the authenticated user.
// In security, a principal is the entity that has been authenticated.

public class UserPrincipal implements UserDetails{

    private User user;

    public UserPrincipal(User user){
        this.user = user;
    }


    @Override
    // Authority: which permison user, Admin and all
    // Return any kind of collection that contains GrantedAuthority objects.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This creates a collection with exactly one object.
        return Collections.singleton(
            // In future Spring actually expects the authority to be ROLE_ so add"ROLE_"+
            new SimpleGrantedAuthority("ROLE_"+user.getRole().name().toUpperCase())
        );
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }    
    
}
