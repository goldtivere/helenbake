package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.Account;
import com.helenbake.helenbake.domain.AccountItemQuantity;
import com.helenbake.helenbake.domain.CategoryItem;
import com.helenbake.helenbake.dto.ReportValue;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountItemQuantityRepository extends JpaRepository<AccountItemQuantity,Long>, QuerydslPredicateExecutor<AccountItemQuantity> {

    @Query(value="Select ac.categoryItem as category, coalesce(SUM(ac.quantity),0) ,coalesce(sum(al.quantity),0) , coalesce(asd.pricePerUnit,0) from AccountItemQuantity ac join  AccountLog al on ac.categoryItem=al.categoryItem " +
            "inner join  AccountDetails asd  on ac.categoryItem=asd.categoryItem where ac.account = :account  group by ac.categoryItem ")
    List<?> getValt(@Param("account")Account account);

    @Query(value="Select ac.categoryItem as category, coalesce(SUM(ac.quantity),0) ,coalesce(sum(al.quantity),0) , coalesce(asd.pricePerUnit,0) from AccountItemQuantity ac join  AccountLog al on ac.categoryItem=al.categoryItem " +
            "inner join  AccountDetails asd  on ac.categoryItem=asd.categoryItem where ac.account = :account and ac.categoryItem.name LIKE CONCAT('%',:valName,'%')  group by ac.categoryItem ")
    List<?> getValt(@Param("account")Account account,@Param("valName") String valName);
}
