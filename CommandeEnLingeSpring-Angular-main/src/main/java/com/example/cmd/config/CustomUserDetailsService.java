package com.example.cmd.config;

import com.example.cmd.model.Client;
import com.example.cmd.model.Utilisateur;
import com.example.cmd.repository.ClientRepository;
import com.example.cmd.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository, ClientRepository clientRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByUsername(username);

        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();
            return new CustomUserPrincipal(
                    utilisateur.getUsername(),
                    utilisateur.getMotDePasse(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRoleType().getNom().toUpperCase())),
                    null
            );
        } else {
            Client client = clientRepository.findByUsername(username);
            if (client != null) {
                return new CustomUserPrincipal(
                        client.getUsername(),
                        client.getMotDePasse(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + client.getRoleType().getNom().toUpperCase())),
                        client
                );
            } else {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }
    }
}
