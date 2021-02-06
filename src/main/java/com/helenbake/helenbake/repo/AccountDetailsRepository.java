package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountDetails;
import com.helenbake.helenbake.domain.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails,Long>, QuerydslPredicateExecutor<AccountDetails> {
    Optional<AccountDetails> findByCategoryItem(CategoryItem categoryItem);

}

