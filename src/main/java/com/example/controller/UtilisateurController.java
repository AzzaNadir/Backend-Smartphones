package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")

public class UtilisateurController {

    @GetMapping("/hello")
    public String helloworld() {

        return "helloworld";
    }

}


