package com.example.application.services;

import com.example.application.data.requests.AuthRequest;
import com.example.application.data.response.JwtResponse;
import com.example.application.entities.User;
import com.example.application.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @noinspection ClassCanBeRecord
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody AuthRequest authRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
            );
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> authenticate(String usernameOrEmail, String password) {
        User providedUser = userService
                .findByUsernameOrEmailIgnoreCase(usernameOrEmail)
                .orElse(null);

        if (providedUser == null) {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.BAD_REQUEST);
        }

        return passwordEncoder.matches(password, providedUser.getPassword())
                ? ResponseEntity.ok("Successfully logged in")
                : new ResponseEntity<>("Wrong username or password", HttpStatus.BAD_REQUEST);
    }


}
