package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails,Long>, QuerydslPredicateExecutor<AccountDetails> {
}
