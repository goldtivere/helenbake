package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AccountLogRepository extends JpaRepository<AccountLog,Long>, QuerydslPredicateExecutor<AccountLog> {
}
