package com.example.cmd.controller;

import com.example.cmd.DTO.UserDTO;
import com.example.cmd.model.Utilisateur;
import com.example.cmd.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.cmd.model.MyHttpResponse.response;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UtilisateurRepository utilisateurRepository) {
        this.authenticationManager = authenticationManager;
        this.utilisateurRepository = utilisateurRepository;
    }
    @PostMapping("login")
    public ResponseEntity<Object> seConnecter(@RequestBody UserDTO userDTO) {
        Optional<Utilisateur> user;
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUsername(),
                        userDTO.getPassword()
                )
        );
        if (auth.isAuthenticated()) {
            user = utilisateurRepository.findByUsername(auth.getName());
            Utilisateur userInDB = user.get();
            userDTO.setUsername(userInDB.getUsername());
            userDTO.setPassword(userInDB.getMotDePasse());
            userDTO.setRole(userInDB.getRoleType());
            userDTO.setId(userInDB.getId());
            return response(HttpStatus.OK, "Authentifié avec succès !", userDTO);
        }
        return response(HttpStatus.UNAUTHORIZED, "Echec d'authentificaton !", "okpk");
    }
}
