package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CategoryItemRepository  extends JpaRepository<CategoryItem, Long>, QuerydslPredicateExecutor<CategoryItem> {
    Optional<CategoryItem> findByNameIgnoreCase(String name);

}
