package com.helenbake.helenbake.repo;



import com.helenbake.helenbake.domain.Role;
import com.helenbake.helenbake.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleType roleName);
}
