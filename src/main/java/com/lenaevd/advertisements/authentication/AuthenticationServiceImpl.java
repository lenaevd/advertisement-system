package com.lenaevd.advertisements.authentication;

import com.lenaevd.advertisements.dao.UserDao;
import com.lenaevd.advertisements.dto.user.RegisterRequest;
import com.lenaevd.advertisements.exception.UserAlreadyExistsException;
import com.lenaevd.advertisements.exception.WrongCredentialsException;
import com.lenaevd.advertisements.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userDao.findByUsername(request.username()).isPresent()) {
            throw new UserAlreadyExistsException("username", request.username());
        } else if (userDao.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("email", request.email());
        } else {
            User user = new User(request.username(), request.email(),
                    passwordEncoder.encode(request.password()), request.role());
            userDao.save(user);
            LOGGER.info("User {} registered", user.getUsername());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        User user = userDao.findByUsername(username).orElseThrow(() -> new WrongCredentialsException("Wrong username"));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new WrongCredentialsException("Wrong password");
        }
        LOGGER.info("User {} authenticated", user.getUsername());
        return jwtTokenService.generateToken(user);
    }
}
