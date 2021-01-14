package com.helenbake.helenbake.repo;



import com.helenbake.helenbake.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {
    Optional<Category> findByNameIgnoreCase(String name);

}
