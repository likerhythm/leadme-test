package com.leadme.test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String embedding;

    @ManyToMany
    @JoinTable(
            name = "meta_info_content", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "content_id"), // 현재 엔티티(Content)의 FK
            inverseJoinColumns = @JoinColumn(name = "meta_info_id") // 반대쪽 엔티티(MetaInfo)의 FK
    )
    private List<MetaInfo> metaInfos;
}
