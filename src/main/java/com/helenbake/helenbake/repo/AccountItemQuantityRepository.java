package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountItemQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountItemQuantityRepository extends JpaRepository<AccountItemQuantity,Long>, QuerydslPredicateExecutor<AccountItemQuantity> {

}
