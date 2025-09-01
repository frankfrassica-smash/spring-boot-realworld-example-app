package io.spring.core.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class ArticleTest {

  @Test
  public void should_get_right_slug() {
    Article article = new Article("a new   title", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_get_right_slug_with_number_in_title() {
    Article article = new Article("a new title 2", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title-2"));
  }

  @Test
  public void should_get_lower_case_slug() {
    Article article = new Article("A NEW TITLE", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_handle_other_language() {
    Article article = new Article("中文：标题", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("中文-标题"));
  }

  @Test
  public void should_handle_commas() {
    Article article = new Article("what?the.hell,w", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("what-the-hell-w"));
  }

  @Test
  public void should_create_article_with_valid_properties() {
    String title = "Test Title";
    String description = "Test Description";
    String body = "Test Body";
    List<String> tagList = Arrays.asList("java", "spring");
    String userId = "user123";

    Article article = new Article(title, description, body, tagList, userId);

    assertThat(article.getId(), notNullValue());
    assertThat(article.getSlug(), is("test-title"));
    assertThat(article.getTitle(), is(title));
    assertThat(article.getDescription(), is(description));
    assertThat(article.getBody(), is(body));
    assertThat(article.getUserId(), is(userId));
    assertThat(article.getCreatedAt(), notNullValue());
    assertThat(article.getUpdatedAt(), notNullValue());
    assertThat(article.getTags(), hasSize(2));
  }

  @Test
  public void should_deduplicate_tags() {
    List<String> tagList = Arrays.asList("java", "spring", "java", "boot", "spring");
    Article article = new Article("Title", "desc", "body", tagList, "user123");

    assertThat(article.getTags(), hasSize(3));
    List<String> tagNames = article.getTags().stream().map(Tag::getName).toList();
    assertThat(tagNames, containsInAnyOrder("java", "spring", "boot"));
  }

  @Test
  public void should_create_article_with_custom_created_date() {
    DateTime customDate = new DateTime(2023, 1, 1, 12, 0, 0);
    Article article = new Article("Title", "desc", "body", Arrays.asList("java"), "user123", customDate);

    assertThat(article.getCreatedAt(), is(customDate));
    assertThat(article.getUpdatedAt(), is(customDate));
  }

  @Test
  public void should_update_title_and_slug() {
    Article article = new Article("Original Title", "desc", "body", Arrays.asList("java"), "user123");
    DateTime originalUpdatedAt = article.getUpdatedAt();

    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    article.update("New Title", null, null);

    assertThat(article.getTitle(), is("New Title"));
    assertThat(article.getSlug(), is("new-title"));
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt) || article.getUpdatedAt().isEqual(originalUpdatedAt), is(true));
  }

  @Test
  public void should_update_description() {
    Article article = new Article("Title", "Original Description", "body", Arrays.asList("java"), "user123");
    DateTime originalUpdatedAt = article.getUpdatedAt();

    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    article.update(null, "New Description", null);

    assertThat(article.getDescription(), is("New Description"));
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt) || article.getUpdatedAt().isEqual(originalUpdatedAt), is(true));
  }

  @Test
  public void should_update_body() {
    Article article = new Article("Title", "desc", "Original Body", Arrays.asList("java"), "user123");
    DateTime originalUpdatedAt = article.getUpdatedAt();

    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    article.update(null, null, "New Body");

    assertThat(article.getBody(), is("New Body"));
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt) || article.getUpdatedAt().isEqual(originalUpdatedAt), is(true));
  }

  @Test
  public void should_not_update_with_empty_strings() {
    Article article = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("java"), "user123");
    DateTime originalUpdatedAt = article.getUpdatedAt();

    article.update("", "", "");

    assertThat(article.getTitle(), is("Original Title"));
    assertThat(article.getDescription(), is("Original Description"));
    assertThat(article.getBody(), is("Original Body"));
    assertThat(article.getUpdatedAt(), is(originalUpdatedAt));
  }

  @Test
  public void should_update_multiple_fields() {
    Article article = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("java"), "user123");

    article.update("New Title", "New Description", "New Body");

    assertThat(article.getTitle(), is("New Title"));
    assertThat(article.getSlug(), is("new-title"));
    assertThat(article.getDescription(), is("New Description"));
    assertThat(article.getBody(), is("New Body"));
  }
}
