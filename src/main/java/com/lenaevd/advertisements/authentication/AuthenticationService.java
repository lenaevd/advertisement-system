package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dao.impl.UserDaoImpl;
import com.lenaevd.advertisements.exception.UserAlreadyExistsException;
import com.lenaevd.advertisements.exception.WrongCredentialsException;
import com.lenaevd.advertisements.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDaoImpl userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenService;

    @Transactional
    public void register(User user) {
        if (userDao.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("username", user.getUsername());
        } else if (userDao.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(user);
        }
    }

    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        User user = userDao.findByUsername(username).orElseThrow(() -> new WrongCredentialsException("Wrong username"));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new WrongCredentialsException("Wrong password");
        }
        return jwtTokenService.generateToken(user);
    }
}
