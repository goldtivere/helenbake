package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import org.springframework.core.convert.converter.Converter;

public class AccountToCommand implements Converter<Account, AccountCommand> {
    @Override
    public AccountCommand convert(Account source) {
        if(source == null) {
            return null;
        }
        final AccountCommand accountCommand= new AccountCommand();
        accountCommand.setId(source.getId());
        accountCommand.setTo(source.getTo());
        accountCommand.setFrom(source.getFrom());
        accountCommand.setDescription(source.getDescription());

        return accountCommand;
    }
}
