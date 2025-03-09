package me.hyeyul.springbootblog.controller;

import lombok.RequiredArgsConstructor;
import me.hyeyul.springbootblog.domain.Article;
import me.hyeyul.springbootblog.dto.AddArticleRequest;
import me.hyeyul.springbootblog.dto.ArticleResponse;
import me.hyeyul.springbootblog.dto.UpdateArticleRequest;
import me.hyeyul.springbootblog.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    // HTTP 메서드가 POST일 때 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    // @RequestBody로 요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    // 전체 글을 조회한 뒤 반환
    // /api/articles GET 요청이 오면 글 전체를 조회하는 findAll() 메서드를 호출
    // 응답용 객체인 ArticleResponse로 파싱해 body에 담아 클라이언트에게 전송
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    // URL 경로에서 값 추출
    // @PathVariable은 URL에서 값을 가져오는 애너테이션
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        // findById로 넘어가 해당 id 번호의 블로그 글 찾음
        Article article = blogService.findById(id);

        // 글의 정보를 body에 담아 웹 브라우저로 전송
        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    // /api/articles/{id} DELETE 요청이 오면 {id}에 해당하는 값이 @PathVariable 애너테이션을 통해 들어옴
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    // /api/articles/{id} PUT 요청이 오면 Request Body 정보가 request로 넘어옴
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        // 서비스 클래스의 update() 메서드에 id와 request를 넘겨줌
        Article updatedArticle = blogService.update(id, request);

        // 응답 값을 body에 담아 전송
        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}
