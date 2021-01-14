package com.helenbake.helenbake.converters;


import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommandToUser implements Converter<UserCommand, User> {
    @Override
    public User convert(UserCommand source) {
        if(source == null)
        {
            return null;
        }
        final User user= new User();

        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setPhoneNumber(source.getPhoneNumber());

        return user;
    }
}
