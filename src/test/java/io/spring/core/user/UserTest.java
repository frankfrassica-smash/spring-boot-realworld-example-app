package io.spring.core.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  public void should_create_user_with_valid_properties() {
    String email = "test@example.com";
    String username = "testuser";
    String password = "password123";
    String bio = "Test bio";
    String image = "http://example.com/image.jpg";

    User user = new User(email, username, password, bio, image);

    assertThat(user.getId(), notNullValue());
    assertThat(user.getEmail(), is(email));
    assertThat(user.getUsername(), is(username));
    assertThat(user.getPassword(), is(password));
    assertThat(user.getBio(), is(bio));
    assertThat(user.getImage(), is(image));
  }

  @Test
  public void should_update_email() {
    User user = new User("old@example.com", "username", "password", "bio", "image");
    String newEmail = "new@example.com";

    user.update(newEmail, null, null, null, null);

    assertThat(user.getEmail(), is(newEmail));
    assertThat(user.getUsername(), is("username"));
    assertThat(user.getPassword(), is("password"));
    assertThat(user.getBio(), is("bio"));
    assertThat(user.getImage(), is("image"));
  }

  @Test
  public void should_update_username() {
    User user = new User("email@example.com", "oldusername", "password", "bio", "image");
    String newUsername = "newusername";

    user.update(null, newUsername, null, null, null);

    assertThat(user.getUsername(), is(newUsername));
    assertThat(user.getEmail(), is("email@example.com"));
  }

  @Test
  public void should_update_password() {
    User user = new User("email@example.com", "username", "oldpassword", "bio", "image");
    String newPassword = "newpassword";

    user.update(null, null, newPassword, null, null);

    assertThat(user.getPassword(), is(newPassword));
  }

  @Test
  public void should_update_bio() {
    User user = new User("email@example.com", "username", "password", "old bio", "image");
    String newBio = "new bio";

    user.update(null, null, null, newBio, null);

    assertThat(user.getBio(), is(newBio));
  }

  @Test
  public void should_update_image() {
    User user = new User("email@example.com", "username", "password", "bio", "old-image.jpg");
    String newImage = "new-image.jpg";

    user.update(null, null, null, null, newImage);

    assertThat(user.getImage(), is(newImage));
  }

  @Test
  public void should_not_update_with_empty_strings() {
    User user = new User("email@example.com", "username", "password", "bio", "image.jpg");

    user.update("", "", "", "", "");

    assertThat(user.getEmail(), is("email@example.com"));
    assertThat(user.getUsername(), is("username"));
    assertThat(user.getPassword(), is("password"));
    assertThat(user.getBio(), is("bio"));
    assertThat(user.getImage(), is("image.jpg"));
  }

  @Test
  public void should_update_multiple_fields() {
    User user = new User("old@example.com", "oldusername", "oldpassword", "old bio", "old-image.jpg");

    user.update("new@example.com", "newusername", "newpassword", "new bio", "new-image.jpg");

    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("newusername"));
    assertThat(user.getPassword(), is("newpassword"));
    assertThat(user.getBio(), is("new bio"));
    assertThat(user.getImage(), is("new-image.jpg"));
  }

  @Test
  public void should_update_partial_fields() {
    User user = new User("old@example.com", "oldusername", "oldpassword", "old bio", "old-image.jpg");

    user.update("new@example.com", null, "newpassword", null, "new-image.jpg");

    assertThat(user.getEmail(), is("new@example.com"));
    assertThat(user.getUsername(), is("oldusername"));
    assertThat(user.getPassword(), is("newpassword"));
    assertThat(user.getBio(), is("old bio"));
    assertThat(user.getImage(), is("new-image.jpg"));
  }
}
