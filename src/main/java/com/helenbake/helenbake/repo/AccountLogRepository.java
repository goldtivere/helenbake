package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.AccountDetails;
import com.helenbake.helenbake.domain.AccountLog;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.domain.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountLogRepository extends JpaRepository<AccountLog,Long>, QuerydslPredicateExecutor<AccountLog> {
    List<AccountLog> findByCollections(Collections collections);
}
