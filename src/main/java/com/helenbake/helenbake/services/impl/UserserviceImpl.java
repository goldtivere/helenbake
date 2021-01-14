package com.helenbake.helenbake.services.impl;



import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public UserserviceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone);
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


}
