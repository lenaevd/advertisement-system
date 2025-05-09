package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.user.ChangeEmailRequest;
import com.lenaevd.advertisements.dto.user.ChangePasswordRequest;
import com.lenaevd.advertisements.dto.user.ChangeUsernameRequest;
import com.lenaevd.advertisements.dto.user.UserDto;
import com.lenaevd.advertisements.mapper.UserMapper;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PatchMapping("/username")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> changeUsername(@RequestBody @Validated ChangeUsernameRequest request, Principal principal) {
        userService.changeUsername(principal, request.newUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/email")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> changeEmail(@RequestBody @Validated ChangeEmailRequest request, Principal principal) {
        userService.changeEmail(principal, request.newEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> changePassword(@RequestBody @Validated ChangePasswordRequest request, Principal principal) {
        userService.changePassword(principal, request.newPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(mapper.usersToUserDtos(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserByIdIfExists(id);
        return ResponseEntity.ok(mapper.userToUserDto(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
