package gem.training_spring.base_app.repository;

import gem.training_spring.base_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
