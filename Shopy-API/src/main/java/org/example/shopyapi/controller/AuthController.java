package org.example.shopyapi.controller;

import org.example.shopyapi.dto.UserRegisterDto;
import org.example.shopyapi.dto.UserLoginDto;
import org.example.shopyapi.dto.UserResponseDto;
import org.example.shopyapi.dto.JwtResponseDto;
import org.example.shopyapi.model.User;
import org.example.shopyapi.repository.UserRepository;
import org.example.shopyapi.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDto> register(@RequestBody UserRegisterDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setAddress(dto.getAddress());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        UserResponseDto userDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getTelephone(), user.getAddress());
        return ResponseEntity.ok(new JwtResponseDto(token, userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody UserLoginDto dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(null);
        }

        String token = jwtService.generateToken(user);
        UserResponseDto userDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getTelephone(), user.getAddress());
        return ResponseEntity.ok(new JwtResponseDto(token, userDto));
    }
} 