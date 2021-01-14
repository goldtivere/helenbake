package com.helenbake.helenbake.services;


import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.command.CategoryItemCommand;
import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryCommand> listAllCategory(BooleanExpression expression, Pageable pageable);
    CategoryCommand createCategory(CategoryCommand categoryCommand,Long id);
    CategoryCommand editCategory(Category previous, CategoryCommand usercurrent, User inputer);
    CategoryItemCommand editCategoryItem(CategoryItem previous, CategoryCommand usercurrent, User inputer);
    CategoryCommand getCategoryName(Long id);
    Page<CategoryItemCommand> listAllCategoryItems(BooleanExpression expression, Pageable pageable);
    CategoryItemCommand createItemCategory(CategoryCommand categoryCommand,Category category,Long id);
}
