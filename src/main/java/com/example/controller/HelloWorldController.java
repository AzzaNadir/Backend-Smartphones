package com.example.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping(value = "/api")
public class HelloWorldController {

    @GetMapping(value = "/hello")
    public String firstPage() {
        return "Hello World";
    }

    @PostMapping("/echo")
    public ResponseEntity<String> echoString(@RequestBody String input) {
        String response = "La phrase '" + input + "' a été reçue.";
        return ResponseEntity.ok(response);
    }


}