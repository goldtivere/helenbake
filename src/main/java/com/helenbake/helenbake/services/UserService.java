package com.helenbake.helenbake.services;


import com.helenbake.helenbake.domain.User;

public interface UserService {
    User findByPhone(String phone);
    String resetPassword(User user);
}
