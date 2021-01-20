package com.helenbake.helenbake.services.impl;

import com.helenbake.helenbake.command.AccountCommand;
import com.helenbake.helenbake.command.AccountIDetailsCommand;
import com.helenbake.helenbake.converters.AccountDetailsToCommand;
import com.helenbake.helenbake.converters.AccountToCommand;
import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.AccountDetails;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDto;
import com.helenbake.helenbake.repo.AccountDetailsRepository;
import com.helenbake.helenbake.repo.AccountRepository;
import com.helenbake.helenbake.repo.CategoryItemRepository;
import com.helenbake.helenbake.services.AccountService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sun.media.sound.InvalidDataException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private AccountToCommand accountToCommand;
    private AccountDetailsRepository accountDetailsRepository;
    private AccountDetailsToCommand accountDetailsToCommand;
    private CategoryItemRepository categoryItemRepository;

    public AccountServiceImpl(AccountRepository accountRepository, AccountToCommand accountToCommand,
                              AccountDetailsRepository accountDetailsRepository,
                              AccountDetailsToCommand accountDetailsToCommand,
                              CategoryItemRepository categoryItemRepository) {
        this.accountRepository = accountRepository;
        this.accountToCommand = accountToCommand;
        this.accountDetailsRepository = accountDetailsRepository;
        this.accountDetailsToCommand = accountDetailsToCommand;
        this.categoryItemRepository = categoryItemRepository;
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

    @Override
    public AccountDetails createAccountItem(AccountDetails accountDetails) {
        return accountDetailsRepository.saveAndFlush(accountDetails);
    }

    @Override
    public Page<AccountIDetailsCommand> listAllAccountItems(BooleanExpression expression, Pageable pageable) {
        Page<AccountDetails> accounts = accountDetailsRepository.findAll(expression, pageable);
        Page<AccountIDetailsCommand> accountIDetailsCommands = accounts.map(accountDetailsToCommand::convert);
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
    public FileInputStream getCategoryItems() throws IOException {
        return createExcelForCategoryItems();
    }

    private FileInputStream createExcelForCategoryItems() throws IOException {
        List<CategoryItem> categoryItemList=categoryItemRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("items");


        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        String columns[]= {"Id","Item Name","Amount Per Unit"};

        // Create a Row
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum = 1;
        for(CategoryItem categoryItem: categoryItemList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(categoryItem.getId());
            row.createCell(1)
                    .setCellValue(categoryItem.getName());
        }
        for(int i = 0; i < columns.length; i++) {
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
    public List<AccountDetails> uploadFile(MultipartFile files, Long createdBy) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        DataFormatter formatter = new DataFormatter();
        List<AccountDetails> accountDetails= new ArrayList<>();
        while (rowIterator.hasNext()) {
            AccountDetails accountDetails1= new AccountDetails();
            row = rowIterator.next();
            if (row.getRowNum() < 1) {
                continue;
            }
            Optional<CategoryItem> categoryItem=categoryItemRepository.findById(Long.parseLong(formatter.formatCellValue(row.getCell(0)).trim()));
            if(!categoryItem.isPresent())
            {
               return null;
            }
            Optional<AccountDetails> accountDetais= accountDetailsRepository.findByCategoryItem(categoryItem.get());
            if(accountDetais.isPresent())
            {
                return null;
            }
            accountDetails1.setCategoryItem(categoryItemRepository.findById(Long.parseLong(formatter.formatCellValue(row.getCell(0)).trim())).get() );

            accountDetails1.setPricePerUnit(new BigDecimal(formatter.formatCellValue(row.getCell(2) )));
            accountDetails1.setCreatedBy(createdBy);
            accountDetails.add(accountDetails1);
        }

        List<AccountDetails> accountDetailsList= accountDetailsRepository.saveAll(accountDetails);
        return accountDetailsList;
    }
}
