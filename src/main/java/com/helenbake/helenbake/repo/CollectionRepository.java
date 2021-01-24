package com.helenbake.helenbake.repo;

import com.helenbake.helenbake.domain.Collections;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollectionRepository  extends JpaRepository<Collections,Long> {
   Optional<Collections> findTopByOrderByIdDesc();
}
