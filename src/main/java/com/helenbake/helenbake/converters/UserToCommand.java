package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToCommand implements Converter<User, UserCommand> {
    @Override
    public UserCommand convert(User source) {
        if(source== null)
        {
            return null;
        }
        final UserCommand userCommand= new UserCommand();
        userCommand.setId(source.getId());
        userCommand.setFirstName(source.getFirstName());
        userCommand.setLastName(source.getLastName());
        userCommand.setPhoneNumber(source.getPhoneNumber());
        userCommand.setRoleType(source.getRole().getName());
        return userCommand;
    }
}
