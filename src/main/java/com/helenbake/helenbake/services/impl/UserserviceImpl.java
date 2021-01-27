package com.helenbake.helenbake.services.impl;



import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.converters.UserToCommand;
import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.domain.enums.GenericStatus;
import com.helenbake.helenbake.dto.ChangePassword;
import com.helenbake.helenbake.repo.RoleRepository;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.services.UserService;
import com.helenbake.helenbake.util.GenericUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserserviceImpl implements UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserToCommand userToCommand;
    private BCryptPasswordEncoder encoder;
    @Value("${app.defaultPasscode}")
    private String defaultCode;

    public UserserviceImpl(UserRepository userRepository,RoleRepository roleRepository,UserToCommand userToCommand,
                           BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userToCommand = userToCommand;
        this.encoder = encoder;
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone).orElse(null);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String resetPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateupdated(LocalDate.now());
        user.setUpdatedBy(user.getId());
        userRepository.saveAndFlush(user);
        return "Password Reset Sucessful";
    }

    @Override
    public UserCommand createUser(UserCommand userCommand, Long id) {
        User user = new User();
        user.setFirstName(userCommand.getFirstName());
        user.setPassword(encoder.encode(defaultCode));
        user.setPasscode(defaultCode);
        user.setStatus(GenericStatus.ACTIVE);
        user.setLastName(userCommand.getLastName());
        user.setPhoneNumber(userCommand.getPhoneNumber());
        user.setRole(roleRepository.findByName(userCommand.getRoleType()).get());
        user.setCreatedBy(id);
        return userToCommand.convert(userRepository.saveAndFlush(user));
    }

    @Override
    public UserCommand editUser(User previous, UserCommand usercurrent, User inputer) {
        previous.setFirstName(usercurrent.getFirstName());
        previous.setLastName(usercurrent.getLastName());
        previous.setPhoneNumber(usercurrent.getPhoneNumber());
        previous.setRole(roleRepository.findByName(usercurrent.getRoleType()).get());
        previous.setDateupdated(LocalDate.now());
        previous.setUpdatedBy(inputer.getId());
        return userToCommand.convert(userRepository.saveAndFlush(previous));
    }

    @Override
    public Page<UserCommand> listAllUsers(BooleanExpression expression, Pageable pageable) {
        Page<User> users = userRepository.findAll(expression, pageable);
        Page<UserCommand> UserCommand = users.map(userToCommand::convert);
        return UserCommand;
    }

    @Override
    public User enableDisableUsers(User user, User user2) {
        if(user.getStatus().equals(GenericStatus.ACTIVE))
        {
            user.setStatus(GenericStatus.DEACTIVATED);
            user.setUpdatedBy(user2.getId());
            user.setDateupdated(LocalDate.now());
        } else if(user.getStatus().equals(GenericStatus.DEACTIVATED)) {
            user.setStatus(GenericStatus.ACTIVE);
            user.setUpdatedBy(user2.getId());
            user.setDateupdated(LocalDate.now());
        }
        return userRepository.saveAndFlush(user);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserCommand changePassword(User user, ChangePassword passwordDto) {
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        user.setDateupdated(LocalDate.now());
        user.setUpdatedBy(user.getId());
        return userToCommand.convert(userRepository.saveAndFlush(user));
    }

    @Override
    public UserCommand resetPassword(User user, Long updatedBy) {
        user.setPassword(encoder.encode(defaultCode));
        user.setDateupdated(LocalDate.now());
        user.setUpdatedBy(updatedBy);
        return userToCommand.convert(userRepository.saveAndFlush(user));
    }
}
