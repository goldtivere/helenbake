package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountIDetailsCommand;
import com.helenbake.helenbake.domain.AccountDetails;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDetailsToCommand implements Converter<AccountDetails,AccountIDetailsCommand> {
    @Override
    public AccountIDetailsCommand convert(AccountDetails source) {
        if(source==null) {
            return null;
        }
        final AccountIDetailsCommand accountIDetailsCommand= new AccountIDetailsCommand();

        accountIDetailsCommand.setId(source.getId());
        accountIDetailsCommand.setCategoryItemId(source.getCategoryItem().getId());
        accountIDetailsCommand.setName(source.getCategoryItem().getName());
        accountIDetailsCommand.setPricePerUnit(source.getPricePerUnit());

        return accountIDetailsCommand;
    }
}
