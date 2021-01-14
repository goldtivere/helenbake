package com.helenbake.helenbake.bootstrap;



import com.helenbake.helenbake.domain.Role;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.domain.enums.GenericStatus;
import com.helenbake.helenbake.domain.enums.RoleType;
import com.helenbake.helenbake.repo.RoleRepository;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.util.GenericUtil;
import com.helenbake.helenbake.util.Utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private Utilities utilities;
    @Autowired
    private BCryptPasswordEncoder encoder;
    private Logger logger = LogManager.getLogger(DataLoader.class);

    public DataLoader(RoleRepository roleRepository, Utilities utilities, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.utilities = utilities;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createStoragePath();
        seedRoles();
        seedUsers();

    }

    private void seedUsers() {
        List<User> users = userRepository.findAll();
        List<User> saveUsers = new ArrayList<>();

        if (users.isEmpty()) {
            User user = new User();

            user.setFirstName("Gold");
            user.setLastName("Osawota");
            user.setPhoneNumber("08131248746");
            user.setPassword(encoder.encode("password"));
            user.setStatus(GenericStatus.ACTIVE);
            user.setIsdeleted(false);
            Role role = roleRepository.findByName(RoleType.SUPER_ADMIN).get();
            user.setRole(role);
            saveUsers.add(user);
            saveUsers = userRepository.saveAll(saveUsers);
            for (User u : saveUsers) {
                logger.info("Saved user - id:" + u.getId());
            }
        }
    }

    private void seedRoles() {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            Role role = roleRepository.findByName(roleType).orElse(null);
            if (role == null) {
                role = new Role();
                role.setName(roleType);
                role.setRoleDescription(roleType.name());
                roleRepository.saveAndFlush(role);
            }
        });
    }

    private void createStoragePath() {
        String path = GenericUtil.getStoragePath();
        File file = Paths.get(path).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
