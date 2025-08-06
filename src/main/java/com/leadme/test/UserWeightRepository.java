package com.leadme.test;

import com.leadme.test.entity.UserWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWeightRepository extends JpaRepository<UserWeight, Long> {

    List<UserWeight> findByUserId(long userId);

    boolean existsByUserIdAndMetaInfoId(long userId, long metaInfoId);

    UserWeight findByUserIdAndMetaInfoId(long userId, long metaInfoId);
}
