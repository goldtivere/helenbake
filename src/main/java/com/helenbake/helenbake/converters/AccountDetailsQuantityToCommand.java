package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountDetailQuantityCommand;
import com.helenbake.helenbake.domain.AccountItemQuantity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDetailsQuantityToCommand implements Converter<AccountItemQuantity,AccountDetailQuantityCommand> {
    @Override
    public AccountDetailQuantityCommand convert(AccountItemQuantity source) {
        if(source == null)
        {
            return null;
        }

        final AccountDetailQuantityCommand accountDetailQuantityCommand= new AccountDetailQuantityCommand();
        accountDetailQuantityCommand.setId(source.getId());
        accountDetailQuantityCommand.setCategoryItemId(source.getCategoryItem().getId());
        accountDetailQuantityCommand.setName(source.getCategoryItem().getName());
        accountDetailQuantityCommand.setQuantity(source.getQuantity());
        accountDetailQuantityCommand.setDateCreated(source.getDatecreated());

        return  accountDetailQuantityCommand;
    }
}
