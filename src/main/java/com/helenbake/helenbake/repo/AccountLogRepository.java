package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.*;
import com.helenbake.helenbake.dto.ReportValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountLogRepository extends JpaRepository<AccountLog,Long>, QuerydslPredicateExecutor<AccountLog> {
    List<AccountLog> findByCollections(Collections collections);
    @Query(value="Select al.categoryItem.name as name,coalesce(sum(al.quantity),0) as received , " +
            "coalesce(al.amountPerItem,0) as price from AccountLog al " +
            " where al.account = :account  group by al.categoryItem ")
    List<String> getValt(@Param("account") Account account);


    @Query(value="Select al.categoryItem.name as name,coalesce(sum(al.quantity),0) as received , " +
            "coalesce(al.amountPerItem,0) as price from AccountLog al   " +
            " where al.account = :account and al.categoryItem.name LIKE CONCAT('%',:valName,'%')  group by al.categoryItem ")
    List<String> getValt(@Param("account")Account account,@Param("valName") String valName);

    @Query(value="Select coalesce(sum(al.quantity),0)  from AccountLog al "+
            " where al.account = :account and al.categoryItem=:categoryItem ")
    Long getValue(@Param("account")Account account,@Param("categoryItem") CategoryItem categoryItem);
}
