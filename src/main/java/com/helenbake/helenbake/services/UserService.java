package com.helenbake.helenbake.services;



import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User findByPhone(String phone);
    String resetPassword(User user);
    UserCommand createUser(UserCommand userCommand, Long id);
    UserCommand editUser(User previous, UserCommand usercurrent, User inputer);
    Page<UserCommand> listAllUsers(BooleanExpression expression, Pageable pageable);
    User enableDisableUsers(User user,User user1);
}
