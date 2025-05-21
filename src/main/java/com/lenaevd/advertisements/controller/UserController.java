package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.user.ChangeUserRequest;
import com.lenaevd.advertisements.dto.user.UserDto;
import com.lenaevd.advertisements.mapper.UserMapper;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Users", description = "endpoints working with users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Changing users info")
    public ResponseEntity<Void> changeUserInfo(@RequestBody @Validated ChangeUserRequest request, Principal principal) {
        userService.changeUserInfo(principal, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all users", description = "Allowed only for admin")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(mapper.usersToUserDtos(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get user by id", description = "Allowed only for admin")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapper.userToUserDto(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete user", description = "Allowed only for admin")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
