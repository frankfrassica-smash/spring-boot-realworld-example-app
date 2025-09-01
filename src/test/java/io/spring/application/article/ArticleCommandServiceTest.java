package io.spring.application.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleCommandServiceTest {

  @Mock
  private ArticleRepository articleRepository;

  private ArticleCommandService articleCommandService;

  @BeforeEach
  public void setUp() {
    articleCommandService = new ArticleCommandService(articleRepository);
  }

  @Test
  public void should_create_article_successfully() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Test Title")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("java", "spring"))
        .build();

    User creator = new User("test@example.com", "testuser", "password", "bio", "image");

    Article result = articleCommandService.createArticle(param, creator);

    assertThat(result, notNullValue());
    assertThat(result.getTitle(), is("Test Title"));
    assertThat(result.getDescription(), is("Test Description"));
    assertThat(result.getBody(), is("Test Body"));
    assertThat(result.getUserId(), is(creator.getId()));
    assertThat(result.getSlug(), is("test-title"));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_create_article_with_duplicate_tags() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Test Title")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("java", "spring", "java", "boot"))
        .build();

    User creator = new User("test@example.com", "testuser", "password", "bio", "image");

    Article result = articleCommandService.createArticle(param, creator);

    assertThat(result.getTags().size(), is(3));
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  public void should_update_article_successfully() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("java"), "user123");
    
    UpdateArticleParam param = new UpdateArticleParam("Updated Title", "Updated Body", "Updated Description");

    Article result = articleCommandService.updateArticle(existingArticle, param);

    assertThat(result.getTitle(), is("Updated Title"));
    assertThat(result.getDescription(), is("Updated Description"));
    assertThat(result.getBody(), is("Updated Body"));
    assertThat(result.getSlug(), is("updated-title"));
    verify(articleRepository).save(existingArticle);
  }

  @Test
  public void should_update_article_with_partial_fields() {
    Article existingArticle = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("java"), "user123");
    
    UpdateArticleParam param = new UpdateArticleParam("Updated Title", "", "");

    Article result = articleCommandService.updateArticle(existingArticle, param);

    assertThat(result.getTitle(), is("Updated Title"));
    assertThat(result.getDescription(), is("Original Description"));
    assertThat(result.getBody(), is("Original Body"));
    assertThat(result.getSlug(), is("updated-title"));
    verify(articleRepository).save(existingArticle);
  }
}
