package com.leadme.test.entity;

import com.leadme.test.MetaInfoType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class MetaInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MetaInfoType type;

    @Column
    private String name;

    @ManyToMany(mappedBy = "metaInfos")
    private List<Content> contents;

    public boolean isGenre() {
        return type == MetaInfoType.GENRE;
    }
}
