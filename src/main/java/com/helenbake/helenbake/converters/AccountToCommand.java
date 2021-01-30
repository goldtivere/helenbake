package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountToCommand implements Converter<Account, AccountCommand> {
    @Override
    public AccountCommand convert(Account source) {
        if(source == null) {
            return null;
        }
        final AccountCommand accountCommand= new AccountCommand();
        accountCommand.setId(source.getId());
        accountCommand.setTo(source.getToDate());
        accountCommand.setFrom(source.getFromDate());
        accountCommand.setDescription(source.getDescription());
        accountCommand.setAmount(source.getAmount());
        accountCommand.setAccountstatus(source.getAccountstatus());
        accountCommand.setSoldSoFar(source.getSoldSoFar());
        accountCommand.setAccountValue(source.getDescription()+" "+source.getFromDate() + " to "+ source.getToDate());

        return accountCommand;
    }
}
