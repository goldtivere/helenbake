package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.math.BigDecimal;

public interface AccountLogRepository extends JpaRepository<AccountLog,Long>, QuerydslPredicateExecutor<AccountLog> {

}
