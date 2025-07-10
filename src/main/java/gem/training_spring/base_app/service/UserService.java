package gem.training_spring.base_app.service;

import gem.training_spring.base_app.dto.UserDTO;
import gem.training_spring.base_app.entity.User;
import gem.training_spring.base_app.exceptions.IdInvalidExceptions;

public interface UserService {
    UserDTO handleCreateUser(User user) throws IdInvalidExceptions;

    UserDTO handleGetDetailUser(Long id) throws IdInvalidExceptions;

    void handleDeleteUser(Long id) throws IdInvalidExceptions;

    UserDTO handleUpdateUser(UserDTO userDTO) throws IdInvalidExceptions;
    User handleGetUserByUsername(String username);
}
