package com.zerobase.bdg.persist;

import com.zerobase.bdg.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

boolean existsByTicker(String ticker);
Optional<CompanyEntity> findByName(String companyName);

Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
