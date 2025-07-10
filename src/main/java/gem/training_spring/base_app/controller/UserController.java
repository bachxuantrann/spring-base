package gem.training_spring.base_app.controller;

import gem.training_spring.base_app.dto.UserDTO;
import gem.training_spring.base_app.entity.User;
import gem.training_spring.base_app.exceptions.IdInvalidExceptions;
import gem.training_spring.base_app.service.UserService;
import gem.training_spring.base_app.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    @ApiMessage("create a new user")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidExceptions {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(user));
    }

    @GetMapping("/detail/{id}")
    @ApiMessage("get info detail of user")
    public ResponseEntity<UserDTO> getDetailUser(@PathVariable Long id) throws IdInvalidExceptions {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetDetailUser(id));
    }

    @DeleteMapping("/delete/{id}")
    @ApiMessage("delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws IdInvalidExceptions {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/update")
    @ApiMessage("update info of user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) throws IdInvalidExceptions {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(userDTO));
    }
}
