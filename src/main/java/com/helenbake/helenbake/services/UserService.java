package com.helenbake.helenbake.services;



import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.ChangePassword;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User findByPhone(String phone);
    String resetPassword(User user);
    UserCommand createUser(UserCommand userCommand, Long id);
    List<UserCommand> getUsers();
    UserCommand editUser(User previous, UserCommand usercurrent, User inputer);
    Page<UserCommand> listAllUsers(BooleanExpression expression, Pageable pageable);
    User enableDisableUsers(User user,User user1);
    UserCommand changePassword(User user, ChangePassword passwordDto);
    UserCommand resetPassword(User user, Long updatedBy);
}
