package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.AccountReportCommand;
import com.helenbake.helenbake.domain.AccountLog;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountLogToCommand implements Converter<AccountLog, AccountReportCommand> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public AccountReportCommand convert(AccountLog source) {
        if (source == null) {
            return null;
        }

        final AccountReportCommand accountReportCommand = new AccountReportCommand();
        User user = userRepository.findById(source.getCreatedBy()).get();
        accountReportCommand.setAmountPerItem(source.getAmountPerItem());
        accountReportCommand.setTotalAmount(source.getTotalAmount());
        accountReportCommand.setQuantity(source.getQuantity());
        accountReportCommand.setItemName(source.getCategoryItem().getName());
        accountReportCommand.setReceiptNumber(source.getCollections().getReceiptNumber());
        accountReportCommand.setSoldBy(user.getFirstName() + " " + user.getLastName());
        accountReportCommand.setFrom(source.getCollections().getAccount().getFromDate());
        accountReportCommand.setTo(source.getCollections().getAccount().getToDate());
        accountReportCommand.setDateCreated(source.getDatecreated());
        return accountReportCommand;
    }
}
