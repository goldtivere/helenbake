package com.helenbake.helenbake.services;

import com.helenbake.helenbake.command.*;
import com.helenbake.helenbake.domain.*;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.dto.AccountLog;
import com.helenbake.helenbake.exception.InvalidDataException;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public interface AccountService {
    Account createAccount(AccountCommand account, Long id);
    Account editAccount(AccountCommand account,Account previous, Long id);
    Page<AccountCommand> listAllAccount(BooleanExpression expression, Pageable pageable);
    Page<AccountReportCommand> listAllAccountReport(BooleanExpression expression, Pageable pageable);
    Page<CollectionCommand> listAccountCollection(BooleanExpression expression, Pageable pageable);
    Page<AccountIDetailsCommand> listAllAccountItems(BooleanExpression expression, Pageable pageable);
    Page<AccountDetailQuantityCommand> listAllAccountQuantityItems(BooleanExpression expression, Pageable pageable);
    Account enableDisableAccount(Account oldValue, Long updatedBy);
    AccountDetails createAccountItem(AccountDetails accountDetails);
    AccountItemQuantity createAccountItemQuantity(AccountItemQuantity accountItemQuantity);
    List<AccountDetails> uploadFile(MultipartFile files, Long createdBy) throws IOException, InvalidDataException;
    AccountDetails editAccountItems(AccountIDetailsCommand account, CategoryItem categoryItem,AccountDetails previous, Long id);
    AccountItemQuantity editAccountItemsQuantity(AccountDetailQuantityCommand account, CategoryItem categoryItem, AccountItemQuantity previous, Long id);
    FileInputStream getCategoryItems() throws IOException;
    AccountCommand getAccountName(Long id);
    List<AccountCommand> getAccount();
    List<AccountReportCommand> getAccountReport(Collections collections);
    com.helenbake.helenbake.domain.AccountLog createAccountLog(AccountLog[] accountLog,String paymentType,String customerName, Long createdBy,Account account);
}
