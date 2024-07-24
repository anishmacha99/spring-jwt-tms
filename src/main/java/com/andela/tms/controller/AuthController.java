package com.andela.tms.controller;

import com.andela.tms.exception.GlobalExceptionHandler;
import com.andela.tms.models.entity.Role;
import com.andela.tms.models.entity.User;
import com.andela.tms.models.enums.ERole;
import com.andela.tms.payload.request.LoginRequest;
import com.andela.tms.payload.request.SignupRequest;
import com.andela.tms.payload.response.SigninResponse;
import com.andela.tms.payload.response.MessageResponse;
import com.andela.tms.repository.RoleRepository;
import com.andela.tms.repository.UserRepository;
import com.andela.tms.security.jwt.JwtUtils;
import com.andela.tms.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new SigninResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }


        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                if (role.equals(ERole.ROLE_ADMIN.name())) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElse(roleRepository.save(new Role(ERole.ROLE_ADMIN)));
                    roles.add(adminRole);
                } else if (role.equals(ERole.ROLE_USER.name())) {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElse(roleRepository.save(new Role(ERole.ROLE_USER)));
                    roles.add(userRole);
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", "role");
                    error.put("message", "Error: Role is not found.");
                    return GlobalExceptionHandler.getValidationErrorResponseEntity(HttpStatusCode.valueOf(400),
                            List.of(error));
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String authToken = authorizationHeader.substring(7); // Extract token without "Bearer " prefix

            if (jwtUtils.validateJwtToken(authToken)) {
                return ResponseEntity.ok().body(new MessageResponse("Token is valid"));
                //TODO:FEATURE: Refresh token expiry on validation
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
            }
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Authorization header missing or invalid"));
    }
}
