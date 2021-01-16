package com.helenbake.helenbake.services;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    Account createAccount(AccountCommand account, Long id);
    Account editAccount(AccountCommand account,Account previous, Long id);
    Page<AccountCommand> listAllAccount(BooleanExpression expression, Pageable pageable);
    Account enableDisableAccount(Account oldValue, Long updatedBy);
}