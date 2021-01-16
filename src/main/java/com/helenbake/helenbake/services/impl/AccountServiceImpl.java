package com.helenbake.helenbake.services.impl;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.repo.AccountRepository;
import com.helenbake.helenbake.services.AccountService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(AccountCommand account, Long id) {
        if(manipulateAccount(account))
        {
            return null;
        }
        Account account1= new Account();
        account1.setTo(account.getTo());
        account1.setFrom(account.getFrom());
        account1.setDescription(account.getDescription());
        account1.setCreatedBy(id);
       return accountRepository.saveAndFlush(account1);
    }

    @Override
    public Account editAccount(AccountCommand account,Account previous, Long id) {
        if(manipulateAccount(account))
        {
            return null;
        }

        previous.setTo(account.getTo());
        previous.setFrom(account.getFrom());
        previous.setDescription(account.getDescription());
        previous.setAccountstatus(account.getAccountstatus());
        previous.setUpdatedBy(id);
        return accountRepository.saveAndFlush(previous);
    }

    private Boolean manipulateAccount(AccountCommand account) {
        Boolean status = false;
        for (Account account1 : accountRepository.findAll()) {
            if ((account.getTo().isAfter(account1.getTo()) && account.getTo().isBefore(account.getFrom()))
                    || (account.getFrom().isAfter(account1.getTo()) && account.getFrom().isBefore(account1.getFrom()))
                    || account.getFrom().isEqual(account1.getFrom()) || account.getFrom().isEqual(account1.getTo())
                    || account.getTo().isEqual(account.getTo()) || account.getTo().isEqual(account1.getFrom())) {
                status = true;

            }
            break;
        }
        return status;
    }
}
