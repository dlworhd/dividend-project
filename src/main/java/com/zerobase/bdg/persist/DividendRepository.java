package com.zerobase.bdg.persist;

import com.zerobase.bdg.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
	List<DividendEntity> findAllByCompanyId(Long companyId);
	boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
 }
