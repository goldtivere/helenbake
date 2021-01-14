package com.helenbake.helenbake.repo;



import com.helenbake.helenbake.domain.Role;
import com.helenbake.helenbake.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType roleName);
}
