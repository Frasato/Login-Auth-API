package com.frasato.login_auth_api.controllers;

import com.frasato.login_auth_api.dtos.LoginRequestDto;
import com.frasato.login_auth_api.dtos.RegisterRequestDto;
import com.frasato.login_auth_api.dtos.ResponseDto;
import com.frasato.login_auth_api.models.User;
import com.frasato.login_auth_api.repositories.UserRepository;
import com.frasato.login_auth_api.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto body){
        User user = userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("ERROR: user not found!"));

        if(passwordEncoder.matches(body.password(), user.getPassword())){
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDto(user.getName(), token));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto body){
        Optional<User> findUser = userRepository.findByEmail(body.email());
        User user = new User();

        if(findUser.isEmpty()){
            user.setPassword(passwordEncoder.encode(body.password()));
            user.setEmail(body.email());
            user.setName(body.name());

            userRepository.save(user);
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDto(user.getName(), token));
        }

        return ResponseEntity.badRequest().build();
    }

}
