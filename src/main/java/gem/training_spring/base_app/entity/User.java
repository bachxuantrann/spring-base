package gem.training_spring.base_app.entity;

import gem.training_spring.base_app.dto.UserDTO;
import gem.training_spring.base_app.util.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity<UserDTO>{
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "password is required")
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
}
