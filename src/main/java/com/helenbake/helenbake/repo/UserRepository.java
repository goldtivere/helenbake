package com.helenbake.helenbake.repo;



import com.helenbake.helenbake.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumberOrEmail(String phone, String email);
    User findByPhoneNumber(String phone);
   Optional<User> findByDeletedAndId(Boolean deleted, Long id);

}
