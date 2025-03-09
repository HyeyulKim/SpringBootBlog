package me.hyeyul.springbootblog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.hyeyul.springbootblog.domain.Article;
import me.hyeyul.springbootblog.dto.AddArticleRequest;
import me.hyeyul.springbootblog.dto.UpdateArticleRequest;
import me.hyeyul.springbootblog.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 해당 클래스를 빈으로 서블릿 컨테이너에 등록해줌
public class BlogService {
    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    // AddArticleRequest 클래스에 저장된 값들을 article 데이터베이스에 저장함
    public Article save(AddArticleRequest request, String userName){
        return blogRepository.save(request.toEntity(userName));
    }

    // article 테이블에 저장되어 있는 모든 데이터 조회
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 하나 조회
    // JPA에서 제공하는 findById() 메서드를 사용해 ID를 받아 엔티티를 조회
    // 없으면 IllegalArgumentException 예외 발생
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional // 트랜잭션 메서드: 매칭한 메서드를 하나의 트랜잭션으로 묶는 역할
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

}
