package com.leadme.test;

import com.leadme.test.entity.Content;
import com.leadme.test.entity.MetaInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("""
        select mi from Content c
        join c.metaInfos mi
        where c.id = :contentId
    """)
    List<MetaInfo> findMetaInfoByContentId(long contentId);
}
