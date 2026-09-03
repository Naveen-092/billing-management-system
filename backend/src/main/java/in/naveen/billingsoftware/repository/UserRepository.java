package in.naveen.billingsoftware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.naveen.billingsoftware.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserId(String userId);
}
