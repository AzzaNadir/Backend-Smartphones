package com.example.service;

import com.example.model.TypeUtilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final TypeUtilisateur typeUtilisateur;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String email, String password, TypeUtilisateur typeUtilisateur, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.typeUtilisateur = typeUtilisateur;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public TypeUtilisateur getTypeUtilisateur() {
        return typeUtilisateur;
    }
}