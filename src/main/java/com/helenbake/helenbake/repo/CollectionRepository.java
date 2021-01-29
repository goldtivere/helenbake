package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface CollectionRepository  extends JpaRepository<Collections,Long> {
   Optional<Collections> findTopByOrderByIdDesc();
   @Query(value="Select SUM(s.total) from Collections s where "
           + "s.account = ?1 ")
   BigDecimal sumAmount(Account account);
}
