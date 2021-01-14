package com.helenbake.helenbake.repo;



import com.helenbake.helenbake.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Optional<User> findByPhoneNumber(String phone);
   Optional<User> findByDeletedAndId(Boolean deleted, Long id);

}
