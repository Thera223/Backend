package com.example.cmd.config;

import com.example.cmd.model.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;


public class CustomUserPrincipal extends User {

    private final Client client;

    public CustomUserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities, Client client) {
        super(username, password, authorities);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}