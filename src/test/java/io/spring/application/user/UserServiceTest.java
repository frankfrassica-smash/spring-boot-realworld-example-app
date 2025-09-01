package io.spring.application.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserService userService;
  private final String defaultImage = "http://example.com/default.jpg";

  @BeforeEach
  public void setUp() {
    userService = new UserService(userRepository, defaultImage, passwordEncoder);
  }

  @Test
  public void should_create_user_successfully() {
    RegisterParam param = new RegisterParam("test@example.com", "testuser", "password123");

    String encodedPassword = "encoded_password";
    when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);

    User result = userService.createUser(param);

    assertThat(result, notNullValue());
    assertThat(result.getEmail(), is("test@example.com"));
    assertThat(result.getUsername(), is("testuser"));
    assertThat(result.getPassword(), is(encodedPassword));
    assertThat(result.getBio(), is(""));
    assertThat(result.getImage(), is(defaultImage));
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  public void should_update_user_successfully() {
    User existingUser = new User("old@example.com", "olduser", "oldpassword", "old bio", "old-image.jpg");
    
    UpdateUserParam param = UpdateUserParam.builder()
        .email("new@example.com")
        .username("newuser")
        .password("newpassword")
        .bio("new bio")
        .image("new-image.jpg")
        .build();

    UpdateUserCommand command = new UpdateUserCommand(existingUser, param);

    userService.updateUser(command);

    assertThat(existingUser.getEmail(), is("new@example.com"));
    assertThat(existingUser.getUsername(), is("newuser"));
    assertThat(existingUser.getPassword(), is("newpassword"));
    assertThat(existingUser.getBio(), is("new bio"));
    assertThat(existingUser.getImage(), is("new-image.jpg"));
    verify(userRepository).save(existingUser);
  }

  @Test
  public void should_update_user_with_partial_fields() {
    User existingUser = new User("old@example.com", "olduser", "oldpassword", "old bio", "old-image.jpg");
    
    UpdateUserParam param = UpdateUserParam.builder()
        .email("new@example.com")
        .bio("new bio")
        .build();

    UpdateUserCommand command = new UpdateUserCommand(existingUser, param);

    userService.updateUser(command);

    assertThat(existingUser.getEmail(), is("new@example.com"));
    assertThat(existingUser.getUsername(), is("olduser"));
    assertThat(existingUser.getPassword(), is("oldpassword"));
    assertThat(existingUser.getBio(), is("new bio"));
    assertThat(existingUser.getImage(), is("old-image.jpg"));
    verify(userRepository).save(existingUser);
  }
}
