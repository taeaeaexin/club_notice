package com.fourmagic.club_notice.controller;

import com.fourmagic.club_notice.dto.ArticleForm;
import com.fourmagic.club_notice.entity.Article;
import com.fourmagic.club_notice.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Slf4j
@Controller
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form, RedirectAttributes rttr) {
        log.info("Received ArticleForm: {}", form);

        try {
            // 1. DTO를 엔티티로 변환
            Article article = form.toEntity();
            article.setId(null); // 기존 ID 제거로 중복 방지
            log.info("Converted to Entity: {}", article);

            // 2. 레파지토리로 엔티티를 DB에 저장
            Article saved = articleRepository.save(article);
            log.info("Saved Article: {}", saved);

            // 3. 성공 메시지 설정
            rttr.addFlashAttribute("msg", "새 글이 성공적으로 작성되었습니다!");
        } catch (DataIntegrityViolationException e) {
            // Primary Key 중복 예외 처리
            log.error("Database constraint violation while creating article", e);
            rttr.addFlashAttribute("msg", "중복된 데이터로 인해 글을 작성할 수 없습니다.");
            return "redirect:/articles/new";
        } catch (Exception e) {
            // 일반적인 예외 처리
            log.error("Unexpected error while creating article", e);
            rttr.addFlashAttribute("msg", "글 작성 중 예상치 못한 오류가 발생했습니다.");
            return "redirect:/articles/new";
        }

        // 4. 목록 페이지로 리다이렉트
        return "redirect:/articles";
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model){
        log.info("id = " + id);
        // 1. id 조회해서 데이터 가져오기
        Article articleEntity = articleRepository.findById(id).orElse(null);

        // 2. 모델에 데이터 등록하기
        model.addAttribute("article", articleEntity);

        // 3. 뷰 페이지 반환하기
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model){
        // 1. 모든 데이터 가져오기
        ArrayList<Article> articleEntityList = articleRepository.findAll();

        // 2. 모델에 데이터 등록하기
        model.addAttribute("articleList", articleEntityList);

        // 3. 뷰 페이지 설정하기
        return "articles/index";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        Article articleEntity = articleRepository.findById(id).orElse(null);
        model.addAttribute("article", articleEntity);
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form, RedirectAttributes rttr) {
        log.info(form.toString());
        // 1. DTO를 엔티티로 변환하기
        Article articleEntity = form.toEntity();
        log.info("Converted to Entity: {}", articleEntity);

        // 2. 엔티티를 DB에 저장하기
        // 2-1. DB에서 기존 데이터 가져오기
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);

        // 2-2. 기존 데이터가 있을 경우 업데이트 수행
        if (target != null) {
            articleRepository.save(articleEntity);
            rttr.addFlashAttribute("msg", "글이 성공적으로 수정되었습니다!");
        }

        // 3. 수정된 게시글 상세 페이지로 리다이렉트하기
        return "redirect:/articles/" + articleEntity.getId();
    }


    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제 요청");
        // 1. 삭제 할 대상 가져오기
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());
        // 2. 대상 엔티티 삭제하기
        if(target != null){
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제되었습니다.");
        }
        // 3. 결과 페이지로 리다이렉트 하기
        return "redirect:/articles";
    }
}