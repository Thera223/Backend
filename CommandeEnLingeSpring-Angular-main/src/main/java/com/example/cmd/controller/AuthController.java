package com.example.cmd.controller;

import com.example.cmd.DTO.UserDTO;
import com.example.cmd.config.JwtService;
import com.example.cmd.model.Utilisateur;
import com.example.cmd.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.cmd.model.MyHttpResponse.response;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UtilisateurRepository utilisateurRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.utilisateurRepository = utilisateurRepository;
        this.jwtService = jwtService;
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

            // Générer un token JWT
            String jwtToken = jwtService.generateToken(userInDB);

            // Ajouter le token JWT dans la réponse
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + jwtToken)
                    .body(response(HttpStatus.OK, "Authentifié avec succès !", userDTO));
        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response(HttpStatus.UNAUTHORIZED, "Échec d'authentification !", null));
    }
}
