package io.spring.application.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateUserValidatorTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ConstraintValidatorContext context;

  @Mock
  private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  @Mock
  private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

  @InjectMocks
  private UpdateUserValidator validator;

  @Test
  public void should_be_valid_when_email_and_username_are_unique() {
    User targetUser = new User("old@example.com", "olduser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("new@example.com")
        .username("newuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_be_valid_when_email_belongs_to_same_user() {
    User targetUser = new User("test@example.com", "testuser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("test@example.com")
        .username("newuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_be_valid_when_username_belongs_to_same_user() {
    User targetUser = new User("test@example.com", "testuser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("new@example.com")
        .username("testuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_be_invalid_when_email_belongs_to_different_user() {
    User targetUser = new User("old@example.com", "olduser", "password", "bio", "image");
    User otherUser = new User("test@example.com", "testuser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("test@example.com")
        .username("newuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(otherUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("email already exist");
    verify(violationBuilder).addPropertyNode("email");
    verify(nodeBuilder).addConstraintViolation();
  }

  @Test
  public void should_be_invalid_when_username_belongs_to_different_user() {
    User targetUser = new User("old@example.com", "olduser", "password", "bio", "image");
    User otherUser = new User("test@example.com", "testuser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("new@example.com")
        .username("testuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(otherUser));
    when(context.buildConstraintViolationWithTemplate("username already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("username")).thenReturn(nodeBuilder);

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("username already exist");
    verify(violationBuilder).addPropertyNode("username");
    verify(nodeBuilder).addConstraintViolation();
  }

  @Test
  public void should_be_invalid_when_both_email_and_username_belong_to_different_users() {
    User targetUser = new User("old@example.com", "olduser", "password", "bio", "image");
    User emailUser = new User("test@example.com", "emailuser", "password", "bio", "image");
    User usernameUser = new User("other@example.com", "testuser", "password", "bio", "image");
    UpdateUserParam param = UpdateUserParam.builder()
        .email("test@example.com")
        .username("testuser")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(emailUser));
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(usernameUser));
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(context.buildConstraintViolationWithTemplate("username already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);
    when(violationBuilder.addPropertyNode("username")).thenReturn(nodeBuilder);

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("email already exist");
    verify(context).buildConstraintViolationWithTemplate("username already exist");
  }
}
