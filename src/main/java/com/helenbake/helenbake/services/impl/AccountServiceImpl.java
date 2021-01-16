package com.helenbake.helenbake.services.impl;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.converters.AccountToCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.repo.AccountRepository;
import com.helenbake.helenbake.services.AccountService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private AccountToCommand accountToCommand;

    public AccountServiceImpl(AccountRepository accountRepository, AccountToCommand accountToCommand) {
        this.accountRepository = accountRepository;
        this.accountToCommand = accountToCommand;
    }

    @Override
    public Account createAccount(AccountCommand account, Long id) {
        if(manipulateAccount(account))
        {
            return null;
        }
        Account account1= new Account();
        account1.setToDate(account.getTo());
        account1.setFromDate(account.getFrom());
        account1.setDescription(account.getDescription());
        account1.setAmount(account.getAmount());
        account1.setCreatedBy(id);
       return accountRepository.saveAndFlush(account1);
    }

    @Override
    public Account editAccount(AccountCommand account,Account previous, Long id) {
        if(manipulateAccountEdit(account,previous))
        {
            return null;
        }

        previous.setToDate(account.getTo());
        previous.setFromDate(account.getFrom());
        previous.setDescription(account.getDescription());
        previous.setAccountstatus(account.getAccountstatus());
        previous.setUpdatedBy(id);
        previous.setDateupdated(LocalDate.now());
        previous.setAmount(account.getAmount());
        return accountRepository.saveAndFlush(previous);
    }

    private Boolean manipulateAccount(AccountCommand account) {
        Boolean status = false;
        for (Account account1 : accountRepository.findAll()) {
            if ((account.getTo().isAfter(account1.getFromDate()) && account.getTo().isBefore(account1.getToDate()))
                    || (account.getFrom().isAfter(account1.getFromDate()) && account.getFrom().isBefore(account1.getToDate()))
                    || account.getFrom().isEqual(account1.getFromDate()) || account.getFrom().isEqual(account1.getToDate())
                    || account.getTo().isEqual(account1.getFromDate()) || account.getTo().isEqual(account1.getToDate())) {
                status = true;

            }
            break;
        }
        return status;
    }

    private Boolean manipulateAccountEdit(AccountCommand account,Account previous) {
        Boolean status = false;
        for (Account account1 : accountRepository.findAll()) {
            if(previous.getId() !=account.getId()) {
                if ((account.getTo().isAfter(account1.getFromDate()) && account.getTo().isBefore(account1.getToDate()))
                        || (account.getFrom().isAfter(account1.getFromDate()) && account.getFrom().isBefore(account1.getToDate()))
                        || account.getFrom().isEqual(account1.getFromDate()) || account.getFrom().isEqual(account1.getToDate())
                        || account.getTo().isEqual(account1.getFromDate()) || account.getTo().isEqual(account1.getToDate())) {
                    status = true;

                }
            }
            break;
        }
        return status;
    }

    @Override
    public Page<AccountCommand> listAllAccount(BooleanExpression expression, Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(expression, pageable);
        Page<AccountCommand> accountCommands = accounts.map(accountToCommand::convert);
        return accountCommands;
    }

    @Override
    public Account enableDisableAccount(Account oldValue, Long updatedBy) {
        if(oldValue.getAccountstatus().equals(true))
        {
            oldValue.setAccountstatus(false);
            oldValue.setUpdatedBy(updatedBy);
            oldValue.setDateupdated(LocalDate.now());
        }else
        {
            oldValue.setAccountstatus(true);
            oldValue.setUpdatedBy(updatedBy);
            oldValue.setDateupdated(LocalDate.now());
        }
        return accountRepository.saveAndFlush(oldValue);
    }
}
