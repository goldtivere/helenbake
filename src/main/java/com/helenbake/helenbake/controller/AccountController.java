package com.helenbake.helenbake.controller;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.dto.TransactionStatus;
import com.helenbake.helenbake.repo.AccountRepository;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.AccountService;
import com.helenbake.helenbake.util.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private AccountService accountService;
    private AccountRepository accountRepository;
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService,AccountRepository accountRepository)
    {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("createAccount")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountDto accountDto, BindingResult bindingResult,
                                                  @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        LocalDate to;
        LocalDate from;
        try {
             to = LocalDate.parse(accountDto.getTo());
             from = LocalDate.parse(accountDto.getFrom());
        }
        catch(DateTimeException e)
        {
            e.printStackTrace();
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Invalid Date entered:  " + accountDto.getTo() + " "+ accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        AccountCommand accountCommand= new AccountCommand();
        accountCommand.setTo(to);
        accountCommand.setFrom(from);
        accountCommand.setDescription(accountDto.getDescription());

        Account account = accountService.createAccount(accountCommand, user2.getId());
        if(account ==null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Date Already Exist:  " + accountDto.getTo() + " "+ accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        logger.info("New Account created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("New Account created");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("editAccount")
    public ResponseEntity<?> editAccount(@RequestBody @Valid AccountDto accountDto, BindingResult bindingResult,
                                                   @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Account> doesExist = accountRepository.findById(accountDto.getId());
        if(!doesExist.isPresent())
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        LocalDate to;
        LocalDate from;
        try {
            to = LocalDate.parse(accountDto.getTo());
            from = LocalDate.parse(accountDto.getFrom());
        }
        catch(DateTimeException e)
        {
            e.printStackTrace();
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Invalid Date entered:  " + accountDto.getTo() + " "+ accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        AccountCommand accountCommand= new AccountCommand();
        accountCommand.setTo(to);
        accountCommand.setFrom(from);
        accountCommand.setAccountstatus(accountDto.getAccountstatus());
        accountCommand.setDescription(accountDto.getDescription());

        Account account = accountService.editAccount(accountCommand,doesExist.get(), user2.getId());
        if(account ==null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Date Already Exist:  " + accountDto.getTo() + " "+ accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        logger.info("Account Edited at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Account Edited!");
        return ResponseEntity.ok(transactionStatus);
    }
}
