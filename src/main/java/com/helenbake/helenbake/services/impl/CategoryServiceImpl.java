package com.helenbake.helenbake.services.impl;



import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.command.CategoryItemCommand;
import com.helenbake.helenbake.converters.CategoryItemToCommand;
import com.helenbake.helenbake.converters.CategoryToCommand;
import com.helenbake.helenbake.converters.CommandToCategory;
import com.helenbake.helenbake.converters.CommandToCategoryItem;
import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.repo.CategoryItemRepository;
import com.helenbake.helenbake.repo.CategoryRepository;
import com.helenbake.helenbake.services.CategoryService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryToCommand categoryToCommand;

    private CommandToCategory commandToCategory;

    private CategoryItemToCommand categoryItemToCommand;

    private CommandToCategoryItem commandToCategoryItem;

    private CategoryRepository catergoryRepository;

    private CategoryItemRepository categoryItemRepository;

    public CategoryServiceImpl(CategoryToCommand categoryToCommand,
                               CategoryRepository catergoryRepository,
                               CommandToCategory commandToCategory,
                               CategoryItemRepository categoryItemRepository,
                               CategoryItemToCommand categoryItemToCommand,
                               CommandToCategoryItem commandToCategoryItem) {
        this.categoryToCommand = categoryToCommand;
        this.catergoryRepository = catergoryRepository;
        this.commandToCategory = commandToCategory;
        this.categoryItemRepository = categoryItemRepository;
        this.categoryItemToCommand = categoryItemToCommand;
        this.commandToCategoryItem = commandToCategoryItem;
    }

    @Override
    public Page<CategoryCommand> listAllCategory(BooleanExpression expression, Pageable pageable) {
        Page<Category> companyPage = catergoryRepository.findAll(expression, pageable);
        Page<CategoryCommand> companyCommands = companyPage.map(categoryToCommand::convert);
        return companyCommands;
    }

    @Override
    public CategoryCommand createCategory(CategoryCommand categoryCommand,Long id) {
        Category category = commandToCategory.convert(categoryCommand);
        category.setCreatedBy(id);
        Category category1 = catergoryRepository.saveAndFlush(category);
        return categoryToCommand.convert(category1);
    }

    @Override
    public CategoryCommand editCategory(Category previous, CategoryCommand usercurrent, User inputer) {
        previous.setName(usercurrent.getName());
        previous.setDescription(usercurrent.getDescription());
        previous.setDateupdated(LocalDate.now());
        previous.setUpdatedBy(inputer.getId());
        return categoryToCommand.convert(catergoryRepository.saveAndFlush(previous));
    }

    @Override
    public CategoryItemCommand editCategoryItem(CategoryItem previous, CategoryCommand usercurrent, User inputer) {
        previous.setName(usercurrent.getName());
        previous.setDescription(usercurrent.getDescription());
        previous.setUpdatedBy(inputer.getId());
        previous.setDateupdated(LocalDate.now());
        return categoryItemToCommand.convert(categoryItemRepository.saveAndFlush(previous));
    }

    @Override
    public CategoryCommand getCategoryName(Long id) {
        return categoryToCommand.convert(catergoryRepository.findById(id).orElse(null));
    }

    @Override
    public Page<CategoryItemCommand> listAllCategoryItems(BooleanExpression expression, Pageable pageable) {
        Page<CategoryItem> categoryItems = categoryItemRepository.findAll(expression, pageable);
        return categoryItems.map(categoryItemToCommand::convert);
    }

    @Override
    public CategoryItemCommand createItemCategory(CategoryCommand categoryCommand, Category category,Long id) {
        CategoryItem categoryItem= new CategoryItem();
        categoryItem.setCategory(category);
        categoryItem.setName(categoryCommand.getName());
        categoryItem.setDescription(categoryCommand.getDescription());
        categoryItem.setCreatedBy(id);
        return categoryItemToCommand.convert(categoryItemRepository.saveAndFlush(categoryItem));
    }

}
