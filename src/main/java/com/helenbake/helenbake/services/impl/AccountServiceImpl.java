package com.helenbake.helenbake.services.impl;

import com.helenbake.helenbake.command.*;
import com.helenbake.helenbake.converters.*;
import com.helenbake.helenbake.domain.*;
import com.helenbake.helenbake.domain.Collections;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.dto.ReportValue;
import com.helenbake.helenbake.exception.InvalidDataException;
import com.helenbake.helenbake.repo.*;
import com.helenbake.helenbake.services.AccountService;
import com.helenbake.helenbake.util.JsonConverter;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private AccountToCommand accountToCommand;
    private AccountDetailsQuantityToCommand accountDetailsQuantityToCommand;
    private AccountDetailsRepository accountDetailsRepository;
    private AccountDetailsToCommand accountDetailsToCommand;
    private CategoryItemRepository categoryItemRepository;
    private CollectionRepository collectionRepository;
    private CollectionToCommand collectionToCommand;
    private AccountLogRepository accountLogRepository;
    private AccountItemQuantityRepository accountItemQuantityRepository;
    private AccountLogToCommand accountLogToCommand;

    public AccountServiceImpl(AccountRepository accountRepository, AccountToCommand accountToCommand,
                              AccountDetailsRepository accountDetailsRepository,
                              AccountDetailsToCommand accountDetailsToCommand,
                              AccountLogToCommand accountLogToCommand,
                              AccountDetailsQuantityToCommand accountDetailsQuantityToCommand,
                              CollectionRepository collectionRepository,
                              AccountLogRepository accountLogRepository,
                              CollectionToCommand collectionToCommand,
                              AccountItemQuantityRepository accountItemQuantityRepository,
                              CategoryItemRepository categoryItemRepository) {
        this.accountRepository = accountRepository;
        this.accountLogToCommand = accountLogToCommand;
        this.accountDetailsQuantityToCommand = accountDetailsQuantityToCommand;
        this.accountToCommand = accountToCommand;
        this.accountDetailsRepository = accountDetailsRepository;
        this.accountDetailsToCommand = accountDetailsToCommand;
        this.categoryItemRepository = categoryItemRepository;
        this.collectionRepository = collectionRepository;
        this.accountLogRepository = accountLogRepository;
        this.collectionToCommand = collectionToCommand;
        this.accountItemQuantityRepository = accountItemQuantityRepository;
    }

    @Override
    public Account createAccount(AccountCommand account, Long id) {
        if (manipulateAccount(account)) {
            return null;
        }
        Account account1 = new Account();
        account1.setToDate(account.getTo());
        account1.setFromDate(account.getFrom());
        account1.setDescription(account.getDescription());
        account1.setAmount(account.getAmount());
        account1.setCreatedBy(id);
        return accountRepository.saveAndFlush(account1);
    }

    @Override
    public Account editAccount(AccountCommand account, Account previous, Long id) {
        if (manipulateAccountEdit(account, previous)) {
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

        for (Account account1 : accountRepository.findAll()) {
            if ((account.getTo().isAfter(account1.getFromDate()) && account.getTo().isBefore(account1.getToDate()))
                    || (account.getFrom().isAfter(account1.getFromDate()) && account.getFrom().isBefore(account1.getToDate()))
                    || account.getFrom().isEqual(account1.getFromDate())
                    || account.getTo().isEqual(account1.getFromDate()) || account.getTo().isEqual(account1.getToDate())) {
                return true;

            }

        }
        return false;
    }

    private Boolean manipulateAccountEdit(AccountCommand account, Account previous) {

        for (Account account1 : accountRepository.findAll()) {
            if (previous.getId() != account.getId()) {
                if ((account.getTo().isAfter(account1.getFromDate()) && account.getTo().isBefore(account1.getToDate()))
                        || (account.getFrom().isAfter(account1.getFromDate()) && account.getFrom().isBefore(account1.getToDate()))
                        || account.getFrom().isEqual(account1.getFromDate())
                        || account.getTo().isEqual(account1.getFromDate()) || account.getTo().isEqual(account1.getToDate())) {
                    return true;

                }
            }

        }
        return false;
    }

    @Override
    public Page<AccountCommand> listAllAccount(BooleanExpression expression, Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(expression, pageable);
        accounts.forEach(account -> {

            account.setSoldSoFar(collectionRepository.sumAmount(account));
        });
        Page<AccountCommand> accountCommands = accounts.map(accountToCommand::convert);
        return accountCommands;
    }

    @Override
    public Page<AccountReportCommand> listAllAccountReport(BooleanExpression expression, Pageable pageable) {
        Page<AccountLog> accounts = accountLogRepository.findAll(expression, pageable);

        Page<AccountReportCommand> accountCommands = accounts.map(accountLogToCommand::convert);
        return accountCommands;
    }

    @Override
    public Account enableDisableAccount(Account oldValue, Long updatedBy) {
        if (oldValue.getAccountstatus().equals(true)) {
            oldValue.setAccountstatus(false);
            oldValue.setUpdatedBy(updatedBy);
            oldValue.setDateupdated(LocalDate.now());
        } else {
            oldValue.setAccountstatus(true);
            oldValue.setUpdatedBy(updatedBy);
            oldValue.setDateupdated(LocalDate.now());
        }
        return accountRepository.saveAndFlush(oldValue);
    }

    @Override
    public AccountDetails createAccountItem(AccountDetails accountDetails) {
        return accountDetailsRepository.saveAndFlush(accountDetails);
    }

    @Override
    public AccountItemQuantity createAccountItemQuantity(AccountItemQuantity accountItemQuantity) {
        return accountItemQuantityRepository.saveAndFlush(accountItemQuantity);
    }

    @Override
    public Page<AccountIDetailsCommand> listAllAccountItems(BooleanExpression expression, Pageable pageable) {
        Page<AccountDetails> accounts = accountDetailsRepository.findAll(expression, pageable);
        Page<AccountIDetailsCommand> accountIDetailsCommands = accounts.map(accountDetailsToCommand::convert);
        return accountIDetailsCommands;
    }

    @Override
    public Page<AccountDetailQuantityCommand> listAllAccountQuantityItems(BooleanExpression expression, Pageable pageable) {
        Page<AccountItemQuantity> accounts = accountItemQuantityRepository.findAll(expression, pageable);
        Page<AccountDetailQuantityCommand> accountIDetailsCommands = accounts.map(accountDetailsQuantityToCommand::convert);
        return accountIDetailsCommands;
    }

    @Override
    public AccountDetails editAccountItems(AccountIDetailsCommand account, CategoryItem categoryItem, AccountDetails previous, Long id) {
        previous.setPricePerUnit(account.getPricePerUnit());
        previous.setCategoryItem(categoryItem);
        previous.setUpdatedBy(id);
        previous.setDateupdated(LocalDate.now());
        return accountDetailsRepository.saveAndFlush(previous);
    }

    @Override
    public AccountItemQuantity editAccountItemsQuantity(AccountDetailQuantityCommand account, CategoryItem categoryItem, AccountItemQuantity previous, Long id) {
        previous.setQuantity(account.getQuantity());
        previous.setCategoryItem(categoryItem);
        previous.setUpdatedBy(id);
        previous.setDateupdated(LocalDate.now());
        return accountItemQuantityRepository.saveAndFlush(previous);
    }

    @Override
    public FileInputStream getCategoryItems() throws IOException {
        return createExcelForCategoryItems();
    }

    private FileInputStream createExcelForCategoryItems() throws IOException {
        List<CategoryItem> categoryItemList = categoryItemRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("items");


        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        String columns[] = {"Id", "Item Name", "Amount Per Unit"};

        // Create a Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum = 1;
        for (CategoryItem categoryItem : categoryItemList) {

            Optional<AccountDetails> accountDetail = accountDetailsRepository.findByCategoryItem(categoryItem);
            if (!accountDetail.isPresent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0)
                        .setCellValue(categoryItem.getId());
                row.createCell(1)
                        .setCellValue(categoryItem.getName());
            }
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(
                new File("item.xlsx"));
        //FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
        workbook.write(out);
        out.close();

        // Closing the workbook
        workbook.close();
        return new FileInputStream(new File("item.xlsx"));
    }

    @Override
    public List<AccountDetails> uploadFile(MultipartFile files, Long createdBy) throws IOException, InvalidDataException {

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        DataFormatter formatter = new DataFormatter();
        List<AccountDetails> accountDetails = new ArrayList<>();
        while (rowIterator.hasNext()) {
            AccountDetails accountDetails1 = new AccountDetails();
            row = rowIterator.next();
            if (row.getRowNum() < 1) {
                continue;
            }
            Optional<CategoryItem> categoryItem = categoryItemRepository.findById(Long.parseLong(formatter.formatCellValue(row.getCell(0)).trim()));
            if (!categoryItem.isPresent()) {
                return null;
            }
            Optional<AccountDetails> accountDetais = accountDetailsRepository.findByCategoryItem(categoryItem.get());
            if (accountDetais.isPresent()) {
                return null;
            }
            accountDetails1.setCategoryItem(categoryItemRepository.findById(Long.parseLong(formatter.formatCellValue(row.getCell(0)).trim())).get());

            accountDetails1.setPricePerUnit(new BigDecimal(formatter.formatCellValue(row.getCell(2))));
            if (Integer.parseInt(accountDetails1.getPricePerUnit().toString()) <= 0) {
                InvalidDataException invalidDataException = new InvalidDataException("Amount cannot be less than or equal to zero");
                throw invalidDataException;
            }
            accountDetails1.setCreatedBy(createdBy);
            accountDetails.add(accountDetails1);
        }

        List<AccountDetails> accountDetailsList = accountDetailsRepository.saveAll(accountDetails);
        return accountDetailsList;
    }

    @Override
    public AccountCommand getAccountName(Long id) {
        return accountToCommand.convert(accountRepository.findById(id).orElse(null));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public AccountLog createAccountLog(com.helenbake.helenbake.dto.AccountLog[] accountLog, String paymentType, String customerName, Long createdBy, Account account) {
        Collections collections = saveCollections(accountLog, account, paymentType, customerName, createdBy);
        AccountLog accountLog1 = saveAccountLog(collections, accountLog, account, createdBy);
        accountLog1.setRefCode(collections.getReceiptNumber());
        accountLog1.setPayMethod(collections.getPaymentType());
        accountLog1.setCusName(collections.getCustomerName());

        return accountLog1;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Collections saveCollections(com.helenbake.helenbake.dto.AccountLog[] accountLog, Account account, String paymentType, String customerName, Long createdBy) {
        Collections collections = new Collections();
        BigDecimal totalVal = new BigDecimal("0.00");

        for (com.helenbake.helenbake.dto.AccountLog accountLog1 : accountLog) {
            Optional<CategoryItem> categoryItemOption = categoryItemRepository.findById(accountLog1.getCategoryItemId());
            Optional<AccountDetails> accountDetails = accountDetailsRepository.findByCategoryItem(categoryItemOption.get());
            totalVal = totalVal.add(accountDetails.get().getPricePerUnit().multiply(new BigDecimal(accountLog1.getUnit())));
        }
        Long collections1 = collectionRepository.getcountAccount(account);

            collections.setReceiptNumber("HB0" + (collections1 + 1L));
        collections.setAccount(account);
        collections.setCustomerName(customerName);
        collections.setTotal(totalVal);
        collections.setCreatedBy(createdBy);
        collections.setPaymentType(paymentType);
        return collectionRepository.saveAndFlush(collections);
    }

    private AccountLog saveAccountLog(Collections collections, com.helenbake.helenbake.dto.AccountLog[] accountLogs, Account account, Long createdBy) {
        AccountLog accountLogss = new AccountLog();
        for (com.helenbake.helenbake.dto.AccountLog accountLog : accountLogs) {
            AccountLog accountLog1 = new AccountLog();
            Optional<CategoryItem> categoryItemOption = categoryItemRepository.findById(accountLog.getCategoryItemId());
            Optional<AccountDetails> accountDetails = accountDetailsRepository.findByCategoryItem(categoryItemOption.get());
            accountLog1.setQuantity(accountLog.getUnit());
            accountLog1.setAccount(account);
            accountLog1.setCategoryItem(categoryItemOption.get());
            accountLog1.setAmountPerItem(accountDetails.get().getPricePerUnit());
            accountLog1.setTotalAmount(accountDetails.get().getPricePerUnit().multiply(new BigDecimal(accountLog.getUnit())));
            accountLog1.setCollections(collections);
            accountLog1.setCreatedBy(createdBy);

            accountLogss = accountLogRepository.saveAndFlush(accountLog1);


        }
        return accountLogss;

    }

    @Override
    public List<AccountCommand> getAccount() {
        List<AccountCommand> accountCommands = new ArrayList<>();
        accountRepository.findAll().forEach(account -> {
            AccountCommand accountCommand = accountToCommand.convert(account);
            accountCommands.add(accountCommand);
        });
        return accountCommands.stream().sorted(Comparator.comparingLong(AccountCommand ::getId ).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<CollectionCommand> listAccountCollection(BooleanExpression expression, Pageable pageable) {
        Page<Collections> collections = collectionRepository.findAll(expression, pageable);

        Page<CollectionCommand> collectionCommands = collections.map(collectionToCommand::convert);
        return collectionCommands;
    }

    @Override
    public List<AccountReportCommand> getAccountReport(Collections collections) {
        List<AccountReportCommand> accountReportCommands = new ArrayList<>();
        accountLogRepository.findByCollections(collections).forEach(account -> {
            AccountReportCommand accountCommand = accountLogToCommand.convert(account);
            accountReportCommands.add(accountCommand);
        });
        return accountReportCommands;
    }

    @Override
    public List<ReportValue> detailedReport(Account account, String name) {
        List<String> accountLog = new ArrayList<>();
        List<String> accountQuantity = new ArrayList<>();
        List<ReportValue> reportValues1 = new ArrayList<>();
        if (name != null) {
            System.out.println("i got here");
            accountLog = accountLogRepository.getValt(account, name);
            accountQuantity = accountItemQuantityRepository.getValt(account, name);

            for (String m : accountLog) {
                ReportValue reportValue = new ReportValue();
                String[] mb = m.split(",");

                for (String mm : accountQuantity) {
                    String[] mv = mm.split(",");

                    if(mv[0].equalsIgnoreCase(mb[0]))
                    {
                        reportValue.setReceived(Long.parseLong(mv[1]));
                    }

                }

                reportValue.setName(mb[0]);
                reportValue.setSold(Long.parseLong(mb[1]));
                reportValue.setPrice(new BigDecimal(mb[2]));
                reportValues1.add(reportValue);
            }


        } else {
            System.out.println("i got here again");
            accountLog = accountLogRepository.getValt(account);
            accountQuantity = accountItemQuantityRepository.getValt(account);
            for (String m : accountLog) {
                ReportValue reportValue = new ReportValue();
                String[] mb = m.split(",");

                for (String mm : accountQuantity) {
                    String[] mv = mm.split(",");

                    if(mv[0].equalsIgnoreCase(mb[0]))
                    {
                        reportValue.setReceived(Long.parseLong(mv[1]));
                    }

                }

                reportValue.setName(mb[0]);
                reportValue.setSold(Long.parseLong(mb[1]));
                reportValue.setPrice(new BigDecimal(mb[2]));
                reportValues1.add(reportValue);
            }


        }


        return reportValues1;
    }

    @Override
    public Long remainingData(Account account, CategoryItem categoryItem) {

        return accountItemQuantityRepository.getValue(account,categoryItem)- accountLogRepository.getValue(account, categoryItem);
    }
}
