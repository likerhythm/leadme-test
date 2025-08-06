package com.leadme.test;

import com.leadme.test.entity.MetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, Long> {
}
