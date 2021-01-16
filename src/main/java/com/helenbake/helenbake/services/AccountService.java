package com.helenbake.helenbake.services;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.dto.AccountDto;

public interface AccountService {
    Account createAccount(AccountCommand account, Long id);
    Account editAccount(AccountCommand account,Account previous, Long id);
}
