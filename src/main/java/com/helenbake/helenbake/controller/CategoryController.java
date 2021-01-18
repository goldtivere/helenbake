package com.helenbake.helenbake.controller;


import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.command.CategoryItemCommand;
import com.helenbake.helenbake.domain.AccountDetails;
import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.AccountDet;
import com.helenbake.helenbake.dto.CategoryIte;
import com.helenbake.helenbake.dto.Response;
import com.helenbake.helenbake.dto.TransactionStatus;
import com.helenbake.helenbake.repo.CategoryItemRepository;
import com.helenbake.helenbake.repo.CategoryRepository;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.repo.predicate.CustomPredicateBuilder;
import com.helenbake.helenbake.repo.predicate.Operation;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.CategoryService;
import com.helenbake.helenbake.util.JsonConverter;
import com.helenbake.helenbake.util.PageUtil;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private CategoryService categoryService;
    private CategoryRepository catergoryRepository;
    private CategoryItemRepository categoryItemRepository;
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService,
                              CategoryRepository catergoryRepository,
                              CategoryItemRepository categoryItemRepository,
                              UserRepository userRepository)
    {
        this.categoryService = categoryService;
        this.catergoryRepository = catergoryRepository;
        this.categoryItemRepository = categoryItemRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("create")
    public ResponseEntity<CategoryCommand> createCategory(@RequestBody @Valid CategoryCommand categoryCommand, BindingResult bindingResult,
                                                          @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || categoryCommand == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Category> category = catergoryRepository.findByNameIgnoreCase(categoryCommand.getName());
        if (category.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }


        CategoryCommand company3 = categoryService.createCategory(categoryCommand,user2.getId());
        logger.info("New Category created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(company3));
        return ResponseEntity.ok(company3);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/edit")
    public ResponseEntity<CategoryCommand> editCompany(@RequestBody @Valid CategoryCommand categoryCommand, BindingResult bindingResult,
                                                      @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || categoryCommand == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Category> category = catergoryRepository.findById(categoryCommand.getId());
        if (!category.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Category> category2 = catergoryRepository.findByNameIgnoreCase(categoryCommand.getName());
        if (category2.isPresent()) {

            if(category2.get().getId() != category.get().getId()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }


        CategoryCommand categoryCommand1 = categoryService.editCategory(category.get(), categoryCommand, profileDetails.toUser());
        logger.info(" Category Edited at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(category.get()) + "  to  " + JsonConverter.getJsonRecursive(categoryCommand1));
        return ResponseEntity.ok(categoryCommand1);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CategoryCommand>> listCategory(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "description", required = false) String description,
                                                          @RequestParam(value = "asc", required = false) Boolean asc) {
        CustomPredicateBuilder builder = getCategoryBuilder(name, description);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("name"), Sort.Order.asc("description")));
        Page<CategoryCommand> companies = categoryService.listAllCategory(builder.build(), pageRequest);
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("getCategoryItem/{id}")
    public ResponseEntity<Page<CategoryItemCommand>> getCategoryItems(@PathVariable("id") Long id, @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                    @RequestParam(value = "name", required = false) String name,
                                                                    @RequestParam(value = "description", required = false) String description,
                                                                    @RequestParam(value = "asc", required = false) Boolean asc) {

        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        CustomPredicateBuilder builder = getCategoryItemBuilder(name, description, id);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("datecreated"), Sort.Order.asc("datecreated")));
        Page<CategoryItemCommand> companies = categoryService.listAllCategoryItems(builder.build(), pageRequest);
        return ResponseEntity.ok(companies);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("createCategoryItem/{id}")
    public ResponseEntity<CategoryItemCommand> createCategoryItem(@RequestBody @Valid CategoryCommand categoryCommand, BindingResult bindingResult, @PathVariable("id") Long id,
                                                                  @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || categoryCommand == null || id == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
       Optional<Category> cat= catergoryRepository.findById(id);
        if (!cat.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CategoryItemCommand categoryItemCommand = categoryService.createItemCategory(categoryCommand, cat.get(), user2.getId());
        logger.info("New CategoryItem created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(categoryItemCommand));

        return ResponseEntity.ok(categoryItemCommand);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("editCategoryItem/{id}")
    public ResponseEntity<CategoryItemCommand> editCategoryItem(@RequestBody @Valid CategoryCommand categoryCommand, BindingResult bindingResult, @PathVariable("id") Long id,
                                                              @AuthenticationPrincipal ProfileDetails profileDetails) throws MessagingException {

        if (bindingResult.hasErrors() || categoryCommand == null || id == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Category> cat= catergoryRepository.findById(id);
        Optional<CategoryItem> cat1= categoryItemRepository.findById(categoryCommand.getId());
        if (!cat.isPresent() || !cat1.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<CategoryItem> categoryItem = categoryItemRepository.findByNameIgnoreCase(categoryCommand.getName());
        if (categoryItem.isPresent()) {

            if(cat1.get().getId() != categoryItem.get().getId()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }


        CategoryItemCommand categoryItemCommand = categoryService.editCategoryItem(cat1.get(), categoryCommand, user2);
        logger.info(" Category Item Edited at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(cat1.get()) + "  to  " + JsonConverter.getJsonRecursive(categoryItemCommand));
        return ResponseEntity.ok(categoryItemCommand);
    }

    private CustomPredicateBuilder getCategoryBuilder(String name, String description) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("category", Category.class)
                .with("name", Operation.LIKE, name)
                .with("description", Operation.LIKE, description);
        return builder;
    }
    private CustomPredicateBuilder getCategoryItemBuilder(String name, String description, Long id) {

        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("categoryItem", CategoryItem.class)
                .with("category.id", Operation.EQUALS, id)
                .with("name", Operation.LIKE, name)
                .with("description", Operation.LIKE, description);
        return builder;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("categoryName/{id}")
    public ResponseEntity<CategoryCommand> getCategoryName(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getCategoryName(id));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("categoryItemList")
    public ResponseEntity<List<CategoryIte>> CategoryItemList(@AuthenticationPrincipal ProfileDetails profileDetails) {

        TransactionStatus transactionStatus = new TransactionStatus();
        Optional<User> user = userRepository.findByDeletedAndId(false, profileDetails.toUser().getId());
        if (!user.isPresent()) {
            transactionStatus.setStatus(false);
            transactionStatus.setMessage("User does not exist!!");
            return ResponseEntity.notFound().build();
        }

        List<CategoryIte> categoryItes= new ArrayList<>();
        for(CategoryItem categoryItem: categoryItemRepository.findAll()){
            CategoryIte cat= new CategoryIte();
            cat.setCategoryId(categoryItem.getId());
            cat.setName(categoryItem.getName());

            categoryItes.add(cat);
        }
        logger.info("Category Item retrieval successful at " + LocalDateTime.now());
        return ResponseEntity.ok(categoryItes);
    }

}
