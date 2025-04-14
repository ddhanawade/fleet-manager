package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.dto.UserDTO;
import com.inventory.fleet_manager.model.User;
import com.inventory.fleet_manager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user  = userService.createUser(convertToEntity(userDTO));
        return ResponseEntity.ok( convertToDTO(user));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userdto = new UserDTO();
        userdto.setUsername(user.getUsername());
        userdto.setPassword(user.getPassword());
        userdto.setRole(user.getRole());
        userdto.setCreatedDate(user.getCreatedDate());
        return userdto;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setCreatedDate(LocalDate.now()); // Use LocalDate.now() to set the current date
        return user;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
