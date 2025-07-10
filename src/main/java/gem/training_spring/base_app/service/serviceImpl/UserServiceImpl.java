package gem.training_spring.base_app.service.serviceImpl;

import gem.training_spring.base_app.dto.UserDTO;
import gem.training_spring.base_app.entity.User;
import gem.training_spring.base_app.exceptions.IdInvalidExceptions;
import gem.training_spring.base_app.repository.UserRepository;
import gem.training_spring.base_app.service.UserService;
import gem.training_spring.base_app.util.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO handleCreateUser(User user) throws IdInvalidExceptions {
        boolean isExist = this.isUserExist(user.getUsername());
        if (isExist) {
            throw new IdInvalidExceptions("user is already exist");
        }
//        String password = user.getPassword().trim();
//        String hassPassword = this.passwordEncoder.encode(password);
        User newUser = new User();
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setRole(RoleEnum.USER);
        return this.userRepository.save(newUser).toDTO(UserDTO.class);
    }

    @Override
    public UserDTO handleGetDetailUser(Long id) throws IdInvalidExceptions {
        return this.userRepository.findById(id).orElseThrow(
                () -> new IdInvalidExceptions("user is not exist")
        ).toDTO(UserDTO.class);
    }

    @Override
    public void handleDeleteUser(Long id) throws IdInvalidExceptions {
        User user = this.userRepository.findById(id).orElseThrow(() -> new IdInvalidExceptions("user is not exist"));
        this.userRepository.delete(user);
    }

    @Override
    public UserDTO handleUpdateUser(UserDTO userDTO) throws IdInvalidExceptions {
        User currentUser = this.userRepository.findById(userDTO.getId()).orElseThrow(()
                -> new IdInvalidExceptions("id is not found: " + userDTO.getId()));
        if (userDTO.getEmail() != null) {
            currentUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUsername() != null) {
            currentUser.setUsername(userDTO.getUsername());
        }
        currentUser = this.userRepository.save(currentUser);
        return currentUser.toDTO(UserDTO.class);
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public boolean isUserExist(String username) {
        return this.userRepository.findByUsername(username.trim()) instanceof User ? true : false;
    }
}
