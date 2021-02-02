package com.helenbake.helenbake.controller;

import com.helenbake.helenbake.command.*;
import com.helenbake.helenbake.converters.AccountDetailsQuantityToCommand;
import com.helenbake.helenbake.converters.AccountLogToCommand;
import com.helenbake.helenbake.domain.*;
import com.helenbake.helenbake.dto.*;
import com.helenbake.helenbake.dto.AccountLog;
import com.helenbake.helenbake.repo.*;
import com.helenbake.helenbake.repo.predicate.CustomPredicateBuilder;
import com.helenbake.helenbake.repo.predicate.Operation;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.AccountService;
import com.helenbake.helenbake.util.GenericUtil;
import com.helenbake.helenbake.util.JsonConverter;
import com.helenbake.helenbake.util.PageUtil;
import com.sun.media.sound.InvalidDataException;
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
import org.springframework.web.multipart.MultipartFile;

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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private AccountDetailsRepository accountDetailsRepository;
    private CategoryItemRepository categoryItemRepository;
    private AccountLogRepository accountLogRepository;
    private AccountLogToCommand accountLogToCommand;
    AccountDetailsQuantityToCommand accountDetailsQuantityToCommand;
    private AccountItemQuantityRepository accountItemQuantityRepository;
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService, AccountRepository accountRepository,
                             AccountLogRepository accountLogRepository,
                             AccountLogToCommand accountLogToCommand,
                             AccountDetailsQuantityToCommand accountDetailsQuantityToCommand,
                             CategoryItemRepository categoryItemRepository,
                             AccountItemQuantityRepository accountItemQuantityRepository,
                             UserRepository userRepository, AccountDetailsRepository accountDetailsRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountDetailsRepository = accountDetailsRepository;
        this.categoryItemRepository = categoryItemRepository;
        this.accountLogRepository = accountLogRepository;
        this.accountLogToCommand = accountLogToCommand;
        this.accountItemQuantityRepository = accountItemQuantityRepository;
        this.accountDetailsQuantityToCommand= accountDetailsQuantityToCommand;
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
        if(Integer.parseInt(accountDto.getAmount().toString()) <= 0)
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Amount cannot be less than or equal to zero!");
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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
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
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.desc("datecreated")));
        Page<AccountCommand> accountCommands = accountService.listAllAccount(builder.build(), pageRequest);
        return ResponseEntity.ok(accountCommands);
    }

    private CustomPredicateBuilder getAccountBuilder(LocalDate to, LocalDate from) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("account", Account.class)
                .with("toDate", Operation.DATE_EQUALS, to)
                .with("fromDate", Operation.DATE_EQUALS, from);
        return builder;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping("accountItemQuantity/{id}")
    public ResponseEntity<Page<AccountDetailQuantityCommand>> listAccountQuantity(@PathVariable("id") Long id,
                                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                                  @RequestParam(value = "name", required = false) String name,
                                                                                  @RequestParam(value = "quantity", required = false) Long quantity) {

        Optional<Account> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {

            return ResponseEntity.notFound().build();
        }
        CustomPredicateBuilder builder = getAccountItemQuantityBuilder(name, quantity, id);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("categoryItem.name"), Sort.Order.asc("quantity")));
        Page<AccountDetailQuantityCommand> accountIDetailsCommands = accountService.listAllAccountQuantityItems(builder.build(), pageRequest);
        return ResponseEntity.ok(accountIDetailsCommands);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("accountItemQuantityReport")
    public ResponseEntity<Page<AccountDetailQuantityCommand>> listAccountQuantityReport(
                                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                                  @RequestParam(value = "id", required = false) Long id,
                                                                                  @RequestParam(value = "to", required = false) String too,
                                                                                  @RequestParam(value = "from", required = false) String fromm,
                                                                                  @RequestParam(value = "name", required = false) String name,
                                                                                  @RequestParam(value = "quantity", required = false) Long quantity) {


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
        CustomPredicateBuilder builder = getAccountItemQuantityReportBuilder(name, quantity, id,from,to);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("categoryItem.name")));
        Page<AccountDetailQuantityCommand> accountIDetailsCommands = accountService.listAllAccountQuantityItems(builder.build(), pageRequest);
        return ResponseEntity.ok(accountIDetailsCommands);
    }


    private CustomPredicateBuilder getAccountItemBuilder(String name, BigDecimal pricePerUnit) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("accountDetails", AccountDetails.class)
                .with("categoryItem.name", Operation.LIKE, name)
                .with("pricePerUnit", Operation.EQUALS, pricePerUnit);
        return builder;
    }

    private CustomPredicateBuilder getAccountItemQuantityBuilder(String name, Long quantity, Long id) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("accountItemQuantity", AccountItemQuantity.class)
                .with("account.id", Operation.EQUALS, id)
                .with("categoryItem.name", Operation.LIKE, name)
                .with("quantity", Operation.EQUALS, quantity);
        return builder;
    }
    private CustomPredicateBuilder getAccountItemQuantityReportBuilder(String name, Long quantity, Long id,LocalDate from,LocalDate to) {
        LocalDateTime frm = null;
        LocalDateTime tt = null;
        if (from != null) {
            frm = from.atStartOfDay();
        }

        if (to != null) {
            tt = to.atStartOfDay();
        }
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("accountItemQuantity", AccountItemQuantity.class)
                .with("account.id", Operation.EQUALS, id)
                .with("datecreated", Operation.GREATER_THAN_OR_EQUAL, frm)
                .with("datecreated", Operation.LESS_THAN_OR_EQUAL, tt)
                .with("categoryItem.name", Operation.LIKE, name)
                .with("quantity", Operation.EQUALS, quantity);
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

        if(Integer.parseInt(accountDto.getPricePerUnit().toString()) <= 0)
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Amount cannot be less than or equal zero!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountDto.getCategoryItemId());


        if (!categoryItem.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Item does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountDetails> accountDetais = accountDetailsRepository.findByCategoryItem(categoryItem.get());
        if (accountDetais.isPresent()) {
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


        if (!categoryItem.isPresent()) {
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
        Optional<AccountDetails> accountDetais = accountDetailsRepository.findByCategoryItem(categoryItem.get());
        if (accountDetais.isPresent() && (accountDetais.get().getId() != doesExist.get().getId())) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item Already Exists!!");
            return ResponseEntity.ok(transactionStatus);
        }
        if (!accountDetais.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item does not exist for this Account Item!!");
            return ResponseEntity.ok(transactionStatus);
        }

        AccountDetails account = accountService.editAccountItems(accountDto, categoryItem.get(), doesExist.get(), user2.getId());

        logger.info("Account Item Edited at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Account Item Edited!");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("download")
    public @ResponseBody
    byte[] downloadUploadFile() {
        try {
            return GenericUtil.pathToByteArrayFileInputStream(accountService.getCategoryItems());

        } catch (IOException ex) {
            return new byte[0];
        }
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile files, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (files.isEmpty() || files == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }


        try {
            List<AccountDetails> dataUpload = accountService.uploadFile(files, user2.getId());

            if (dataUpload == null || dataUpload.isEmpty()) {
                transactionStatus.setStatus(false);
                transactionStatus.setMessage("Data already exists!!");
                return ResponseEntity.ok(transactionStatus);
            }

            logger.info("File Uploaded for Account Item at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(dataUpload));

        } catch (InvalidDataException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Error in data uploaded. Kindly make sure fields are properly filled!");
            return ResponseEntity.ok(transactionStatus);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Please enter valid numbers!");
            return ResponseEntity.ok(transactionStatus);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Error in data uploaded. Kindly contact your Administrator!");
            return ResponseEntity.ok(transactionStatus);

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Something went wrong! Kindly contact your Administrator!");
            return ResponseEntity.ok(transactionStatus);

        }
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Upload Successful");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping("accountName/{id}")
    public ResponseEntity<AccountCommand> getCategoryName(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountService.getAccountName(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping("getaccount")
    public ResponseEntity<List<AccountCommand>> getCategoryName() {
        return ResponseEntity.ok(accountService.getAccount());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @PostMapping("createAccountLog/{id}/{paymentType}")
    public ResponseEntity<?> createAccountLog(@PathVariable("id") Long id, @PathVariable("paymentType") String paymentType, @RequestParam("accountdto") String accountdto,
                                              @AuthenticationPrincipal ProfileDetails profileDetails) {


        if (accountdto.isEmpty() || accountdto == null || id == 0L || paymentType.isEmpty() || paymentType == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Account> account = accountRepository.findById(id);
        if (!account.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        AccountLog[] accountLogs = JsonConverter.getElements(accountdto, AccountLog[].class);
        for (AccountLog accountLo : accountLogs) {
            try {
                Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountLo.getCategoryItemId());
                if (!categoryItem.isPresent()) {
                    transactionStatus.setStatus(false);
                    transactionStatus.setMessage("Category Item not found!");
                    return ResponseEntity.ok(transactionStatus);
                }

                if(Integer.parseInt(accountLo.getPricePerUnit().toString()) <= 0 || accountLo.getUnit() <=0 )
                {
                    transactionStatus.setStatus(false);
                    transactionStatus.setMessage("Negative Values are not allowed");
                    return ResponseEntity.ok(transactionStatus);
                }
            } catch (Exception e) {
                transactionStatus.setStatus(false);
                transactionStatus.setMessage("Something went wrong, Please contact the administrator!!");
                logger.error("Something went wrong at  " + LocalDateTime.now() + " " + e.toString());
                return ResponseEntity.ok(transactionStatus);
            }
        }
        com.helenbake.helenbake.domain.AccountLog accountLog = new com.helenbake.helenbake.domain.AccountLog();
        try {
            accountLog =
                    accountService.createAccountLog(accountLogs, paymentType, user2.getId(), account.get());
        } catch (Exception e) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Something went wrong, Please contact the administrator!!");
            logger.error("Something went wrong at  " + LocalDateTime.now() + " " + e.toString());
            return ResponseEntity.ok(transactionStatus);
        }
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Saved Successful");
        transactionStatus.setAdd(accountLog.getRefCode());
        transactionStatus.setMethid(accountLog.getPayMethod());
        logger.info("New AccountLog created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(accountLog));
        return ResponseEntity.ok(transactionStatus);

    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("createAccountItemQuantity/{id}")
    public ResponseEntity<?> createAccountItemQuantity(@PathVariable("id") Long id, @RequestBody @Valid AccountDetQuan accountDto, BindingResult bindingResult,
                                                       @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null || id == 0L) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("User does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }

        if(accountDto.getQuantity()<=0)
        {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Quantity cannot be less than or equal to zero!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountDto.getCategoryItemId());
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }

        if (!categoryItem.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Item does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }

        AccountItemQuantity accountDetails = new AccountItemQuantity();
        accountDetails.setCategoryItem(categoryItem.get());
        accountDetails.setAccount(accountOptional.get());
        accountDetails.setQuantity(accountDto.getQuantity());
        accountDetails.setCreatedBy(user2.getId());
        AccountItemQuantity account = accountService.createAccountItemQuantity(accountDetails);

        logger.info("New Account Item Quantity created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("New Quantity Added!");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("editAccountItemQuantity/{id}")
    public ResponseEntity<?> editAccountItemQuantity(@PathVariable("id") Long id, @RequestBody @Valid AccountDetailQuantityCommand accountDto, BindingResult bindingResult,
                                                     @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || accountDto == null || id == 0L) {
            return ResponseEntity.badRequest().build();
        }
        TransactionStatus transactionStatus = new TransactionStatus();
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<CategoryItem> categoryItem = categoryItemRepository.findById(accountDto.getCategoryItemId());

        Optional<Account> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account does not exist!");
            return ResponseEntity.ok(transactionStatus);
        }
        if (!categoryItem.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Account Item Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountItemQuantity> doesExist = accountItemQuantityRepository.findById(accountDto.getId());
        if (!doesExist.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Quantity Does not Exist!!");
            return ResponseEntity.ok(transactionStatus);
        }
        Optional<AccountDetails> accountDetais = accountDetailsRepository.findByCategoryItem(categoryItem.get());
        if (accountDetais.isPresent() && (accountDetais.get().getId() != doesExist.get().getId())) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item Already Exists!!");
            return ResponseEntity.ok(transactionStatus);
        }
        if (!accountDetais.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("Category Item does not exist for this Account Item!!");
            return ResponseEntity.ok(transactionStatus);
        }

        AccountItemQuantity account = accountService.editAccountItemsQuantity(accountDto, categoryItem.get(), doesExist.get(), user2.getId());

        logger.info("Account Item Edited at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(account));
        transactionStatus.setStatus(true);
        transactionStatus.setMessage("Account Item Edited!");
        return ResponseEntity.ok(transactionStatus);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("accountItemQuantityReport/download")
    public ResponseEntity<Iterable<AccountDetailQuantityCommand>> listAccountQuantityReportDownload(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "to", required = false) String too,
            @RequestParam(value = "from", required = false) String fromm,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "quantity", required = false) Long quantity) {


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


        CustomPredicateBuilder builder = getAccountItemQuantityReportBuilder(name, quantity, id,from,to);

        Stream<AccountItemQuantity> accountLogStream = StreamSupport.stream(accountItemQuantityRepository.findAll(builder.build(), getSortObject(null)).spliterator(), false);
        List<AccountDetailQuantityCommand> accountReportCommands = accountLogStream.map(accountDetailsQuantityToCommand::convert).collect(Collectors.toList());

        Iterable<AccountDetailQuantityCommand> accountReportCommands1 = accountReportCommands;
        return ResponseEntity.ok(accountReportCommands1);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("accountReport/download")
    public ResponseEntity<Iterable<AccountReportCommand>> listAccountReportdownload(@RequestParam(value = "receiptNumber", required = false) String receiptNumber,
                                                                                    @RequestParam(value = "userid", required = false) Long userid,
                                                                                    @RequestParam(value = "itemId", required = false) Long itemId,
                                                                                    @RequestParam(value = "to", required = false) String too,
                                                                                    @RequestParam(value = "from", required = false) String fromm,
                                                                                    @RequestParam(value = "accountId", required = false) Long accountId) {
        Optional<User> user;
        Optional<Account> accountOptional;
        Optional<CategoryItem> categoryItem;
        if (userid != null) {
            user = userRepository.findById(userid);
            if (!user.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }
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
        if (accountId != null) {
            accountOptional = accountRepository.findById(accountId);
            if (!accountOptional.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }
        if (itemId != null) {
            categoryItem = categoryItemRepository.findById(itemId);
            if (!categoryItem.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }


        CustomPredicateBuilder builder = getAccountReportBuilder(userid, itemId, accountId, receiptNumber, from, to);

        Stream<com.helenbake.helenbake.domain.AccountLog> accountLogStream = StreamSupport.stream(accountLogRepository.findAll(builder.build(), getSortObject(null)).spliterator(), false);
        List<AccountReportCommand> accountReportCommands = accountLogStream.map(accountLogToCommand::convert).collect(Collectors.toList());

        Iterable<AccountReportCommand> accountReportCommands1 = accountReportCommands;
        return ResponseEntity.ok(accountReportCommands1);

    }

    private Sort getSortObject(LocalDateTime orderBy) {
        Sort sort;
        if (orderBy != null) {
            sort = Sort.by(String.valueOf(orderBy));
        } else {
            sort = Sort.by(Sort.Direction.DESC, "datecreated");
        }
        return sort;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("accountReport")
    public ResponseEntity<Page<AccountReportCommand>> listAccountReport(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                        @RequestParam(value = "receiptNumber", required = false) String receiptNumber,
                                                                        @RequestParam(value = "userid", required = false) Long userid,
                                                                        @RequestParam(value = "itemId", required = false) Long itemId,
                                                                        @RequestParam(value = "to", required = false) String too,
                                                                        @RequestParam(value = "from", required = false) String fromm,
                                                                        @RequestParam(value = "accountId", required = false) Long accountId) {
        Optional<User> user;
        Optional<Account> accountOptional;
        Optional<CategoryItem> categoryItem;
        if (userid != null) {
            user = userRepository.findById(userid);
            if (!user.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }
        if (accountId != null) {
            accountOptional = accountRepository.findById(accountId);
            if (!accountOptional.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }
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
        if (itemId != null) {
            categoryItem = categoryItemRepository.findById(itemId);
            if (!categoryItem.isPresent()) {

                return ResponseEntity.notFound().build();
            }
        }


        CustomPredicateBuilder builder = getAccountReportBuilder(userid, itemId, accountId, receiptNumber, from, to);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("id")));
        Page<AccountReportCommand> accountIDetailsCommands = accountService.listAllAccountReport(builder.build(), pageRequest);
        return ResponseEntity.ok(accountIDetailsCommands);
    }

    private CustomPredicateBuilder getAccountReportBuilder(Long createdBy, Long itemId, Long accountId, String receiptNumber, LocalDate fromm, LocalDate too) {
        LocalDateTime frm = null;
        LocalDateTime tt = null;
        if (fromm != null) {
            frm = fromm.atStartOfDay();
        }

        if (too != null) {
            tt = too.atStartOfDay();
        }
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("accountLog", com.helenbake.helenbake.domain.AccountLog.class)
                .with("createdBy", Operation.EQUALS, createdBy)
                .with("categoryItem.id", Operation.EQUALS, itemId)
                .with("datecreated", Operation.GREATER_THAN_OR_EQUAL, frm)
                .with("datecreated", Operation.LESS_THAN_OR_EQUAL, tt)
                .with("collections.receiptNumber", Operation.LIKE, receiptNumber)
                .with("collections.account.id", Operation.EQUALS, accountId);
        return builder;
    }
}
