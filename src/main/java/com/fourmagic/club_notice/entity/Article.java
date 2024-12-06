package com.fourmagic.club_notice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Entity
@Getter
@NoArgsConstructor // 기본 생성자 추가 어노테이션
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String content;

    public void setId(Object o) {
    }

    public void setTitle(String title) {

    }
}
