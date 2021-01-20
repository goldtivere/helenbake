package com.helenbake.helenbake.controller;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.command.AccountIDetailsCommand;
import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.AccountDetails;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDet;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.dto.TransactionStatus;
import com.helenbake.helenbake.repo.AccountDetailsRepository;
import com.helenbake.helenbake.repo.AccountRepository;
import com.helenbake.helenbake.repo.CategoryItemRepository;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.repo.predicate.CustomPredicateBuilder;
import com.helenbake.helenbake.repo.predicate.Operation;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.AccountService;
import com.helenbake.helenbake.util.GenericUtil;
import com.helenbake.helenbake.util.JsonConverter;
import com.helenbake.helenbake.util.PageUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private AccountDetailsRepository accountDetailsRepository;
    private CategoryItemRepository categoryItemRepository;
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService, AccountRepository accountRepository,
                             CategoryItemRepository categoryItemRepository,
                             UserRepository userRepository, AccountDetailsRepository accountDetailsRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountDetailsRepository = accountDetailsRepository;
        this.categoryItemRepository = categoryItemRepository;
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
            if (from.isAfter(to)) {
                transactionStatus.setStatus(false);
                transactionStatus.setMessage("From cannot be after To");
                return ResponseEntity.ok(transactionStatus);
            }
        } catch (DateTimeException e) {
            e.printStackTrace();
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Invalid Date entered:  " + accountDto.getTo() + " " + accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        AccountCommand accountCommand = new AccountCommand();
        accountCommand.setTo(to);
        accountCommand.setFrom(from);
        accountCommand.setDescription(accountDto.getDescription());
        accountCommand.setAmount(accountDto.getAmount());

        Account account = accountService.createAccount(accountCommand, user2.getId());
        if (account == null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Date Already Exist:  " + accountDto.getTo() + " " + accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        logger.info("New Account created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("New Account created");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("setStatus/{id}")
    public ResponseEntity<AccountCommand> setAccountStatus(@PathVariable("id") Long id,
                                                           @AuthenticationPrincipal ProfileDetails profileDetails) throws MessagingException {


//        System.out.println(companyUserRepository.findAll().size() + " i got here");

        Optional<Account> account = accountRepository.findById(id);
        // Optional<CompanyUser> companyUser2= companyUserRepository.findByUser(user2);
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }

        if (!account.isPresent()) {
            return ResponseEntity.notFound().build();
        }


        Account account1 = accountService.enableDisableAccount(account.get(), user2.getId());
        logger.info(" Account status updated " + account1.getAccountstatus() + " to at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(account.get()) + "  to  " + JsonConverter.getJsonRecursive(account1));
        return ResponseEntity.ok().build();
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
        if (!doesExist.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        LocalDate to;
        LocalDate from;
        try {
            to = LocalDate.parse(accountDto.getTo());
            from = LocalDate.parse(accountDto.getFrom());
            if (from.isAfter(to)) {
                transactionStatus.setStatus(false);
                transactionStatus.setMessage("From cannot be after To");
                return ResponseEntity.ok(transactionStatus);
            }
        } catch (DateTimeException e) {
            e.printStackTrace();
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Invalid Date entered:  " + accountDto.getTo() + " " + accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        AccountCommand accountCommand = new AccountCommand();
        accountCommand.setId(accountDto.getId());
        accountCommand.setTo(to);
        accountCommand.setFrom(from);
        accountCommand.setAccountstatus(accountDto.getAccountstatus());
        accountCommand.setDescription(accountDto.getDescription());
        accountCommand.setAmount(accountDto.getAmount());

        Account account = accountService.editAccount(accountCommand, doesExist.get(), user2.getId());
        if (account == null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Date Already Exist:  " + accountDto.getTo() + " " + accountDto.getFrom());
            return ResponseEntity.ok(transactionStatus);
        }
        logger.info("Account Edited at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Account Edited!");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AccountCommand>> listAccount(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                            @RequestParam(value = "to", required = false) String too,
                                                            @RequestParam(value = "from", required = false) String fromm,
                                                            @RequestParam(value = "asc", required = false) Boolean asc) {
        LocalDate to = null;
        LocalDate from = null;
        try {
            if (too != null) {
                to = LocalDate.parse(too);
            }
            if (fromm != null) {
                from = LocalDate.parse(fromm);
            }
        } catch (DateTimeException e) {
            return ResponseEntity.badRequest().build();
        }
        CustomPredicateBuilder builder = getAccountBuilder(to, from);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("toDate"), Sort.Order.asc("fromDate")));
        Page<AccountCommand> accountCommands = accountService.listAllAccount(builder.build(), pageRequest);
        return ResponseEntity.ok(accountCommands);
    }

    private CustomPredicateBuilder getAccountBuilder(LocalDate to, LocalDate from) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("account", Account.class)
                .with("toDate", Operation.DATE_EQUALS, to)
                .with("fromDate", Operation.DATE_EQUALS, from);
        return builder;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("accountItem")
    public ResponseEntity<Page<AccountIDetailsCommand>> listAccountItem(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                        @RequestParam(value = "name", required = false) String name,
                                                                        @RequestParam(value = "pricePerUnit", required = false) BigDecimal pricePerUnit) {
        CustomPredicateBuilder builder = getAccountItemBuilder(name, pricePerUnit);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("categoryItem.name"), Sort.Order.asc("pricePerUnit")));
        Page<AccountIDetailsCommand> accountIDetailsCommands = accountService.listAllAccountItems(builder.build(), pageRequest);
        return ResponseEntity.ok(accountIDetailsCommands);
    }
    private CustomPredicateBuilder getAccountItemBuilder(String name, BigDecimal pricePerUnit) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("accountDetails", AccountDetails.class)
                .with("categoryItem.name", Operation.LIKE, name)
                .with("pricePerUnit", Operation.EQUALS, pricePerUnit);
        return builder;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("createAccountItem")
    public ResponseEntity<?> createAccountItem(@RequestBody @Valid AccountDet accountDto, BindingResult bindingResult,
                                               @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("User does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountDto.getCategoryItemId());


        if(!categoryItem.isPresent())
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Item does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountDetails> accountDetais= accountDetailsRepository.findByCategoryItem(categoryItem.get());
        if(accountDetais.isPresent())
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Item Already Exists!");
            return ResponseEntity.ok(transactionStatus);
        }
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setCategoryItem(categoryItem.get());
        accountDetails.setPricePerUnit(accountDto.getPricePerUnit());
        accountDetails.setCreatedBy(user2.getId());
        AccountDetails account = accountService.createAccountItem(accountDetails);

        logger.info("New Account Item created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("New Account Item created");
        return ResponseEntity.ok(transactionStatus);
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("editAccountItem")
    public ResponseEntity<?> editAccountItem(@RequestBody @Valid AccountIDetailsCommand accountDto, BindingResult bindingResult,
                                         @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountDto.getCategoryItemId());


        if(!categoryItem.isPresent())
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account Item Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountDetails> doesExist = accountDetailsRepository.findById(accountDto.getId());
        if (!doesExist.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account Item Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountDetails> accountDetais= accountDetailsRepository.findByCategoryItem(categoryItem.get());
        if(accountDetais.isPresent() && (accountDetais.get().getId() != doesExist.get().getId()))
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item Already Exists!!");
            return ResponseEntity.ok(transactionStatus);
        }
        if(!accountDetais.isPresent())
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item does not exist for this Account Item!!");
            return ResponseEntity.ok(transactionStatus);
        }

        AccountDetails account = accountService.editAccountItems(accountDto, categoryItem.get(),doesExist.get(), user2.getId());

        logger.info("Account Item Edited at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Account Item Edited!");
        return ResponseEntity.ok(transactionStatus);
    }
    @GetMapping("download")
    public @ResponseBody
    byte[] downloadUploadFile() {
      try{
                return GenericUtil.pathToByteArrayFileInputStream(accountService.getCategoryItems());

        } catch (IOException ex) {
            return new byte[0];
        }
    }

}
