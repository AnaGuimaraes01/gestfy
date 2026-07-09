package com.empresa.gestfy.controllers;

import com.empresa.gestfy.services.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.authenticate(request.email(), request.senha());
            return ResponseEntity.ok(new AuthResponse(token, request.email()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            var response = authService.register(new AuthService.RegisterRequest(
                    request.nome(),
                    request.email(),
                    request.senha(),
                    request.telefone(),
                    request.endereco()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, ex.getMessage()));
        }
    }

    public static record LoginRequest(
            @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "A senha é obrigatória") String senha) {
    }

    public static record RegisterRequest(
            @NotBlank(message = "O nome é obrigatório") String nome,
            @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "A senha é obrigatória") String senha,
            @NotBlank(message = "O telefone é obrigatório") String telefone,
            String endereco) {
    }

    public static record AuthResponse(String token, String email) {
    }

    public static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
