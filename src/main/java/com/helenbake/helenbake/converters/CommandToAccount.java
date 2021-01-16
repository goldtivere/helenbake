package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommandToAccount implements Converter<AccountCommand, Account> {

    @Override
    public Account convert(AccountCommand source) {
        if(source ==null) {
            return null;
        }
        final Account account= new Account();
        account.setId(source.getId());
        account.setTo(source.getTo());
        account.setFrom(source.getFrom());
        account.setDescription(source.getDescription());
        return  account;
    }
}
