package com.example.controller;

import com.example.configuration.JwtResponse;
import com.example.configuration.JwtTokenUtil;
import com.example.configuration.LoginRequest;
import com.example.service.CustomUserDetails;
import com.example.service.CustomUserDetailsService;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")

public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest)throws Exception {
        try {
//            System.out.println(loginRequest.getEmail());
//
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

            authenticate(loginRequest.getEmail(), loginRequest.getPassword());

            final CustomUserDetails customUserDetails = customUserDetailsService
                    .loadUserByUsername(loginRequest.getEmail());


            // Générer le token JWT en utilisant CustomUserDetails
            String token = jwtTokenUtil.generateToken(customUserDetails);
            System.out.println(token);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    @GetMapping("/hello")
    public String helloworld() {

        return "helloworld";
    }

}


