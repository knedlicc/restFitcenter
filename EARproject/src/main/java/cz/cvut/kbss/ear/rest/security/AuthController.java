package cz.cvut.kbss.ear.rest.security;

import cz.cvut.kbss.ear.config.JwtTokenUtil;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserDetailsServiceImpl userDetailsService,
                          JwtTokenUtil jwtTokenUtil,
                          AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/rest/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody User authRequest) throws Exception{
        try {
            authenticate(authRequest.getEmail(), authRequest.getPassword());
        }
        catch (BadCredentialsException ex){
            throw new Exception("Incorrect email or password", ex);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
