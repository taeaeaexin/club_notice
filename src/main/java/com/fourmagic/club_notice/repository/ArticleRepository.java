package com.fourmagic.club_notice.repository;

import com.fourmagic.club_notice.entity.Article;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {
}
