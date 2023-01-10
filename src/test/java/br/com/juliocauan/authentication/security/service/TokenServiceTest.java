package br.com.juliocauan.authentication.security.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SignupForm;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.service.TokenService;
import jakarta.persistence.EntityExistsException;

public class TokenServiceTest extends TestContext {

    private final TokenService tokenService;
    private final SignupForm signupForm = new SignupForm();

    private final String password = "12345678";
    private final String username = "testUsername";
    private final String usernameNotPresent = "testUsername2";
    private final String email = "test@email.com";
    private final String emailNotPresent = "test2@email.com";
    private final String errorUsernameDuplicated = "Username is already taken!";
    private final String errorEmailDuplicated = "Email is already in use!";

    private UserEntity entity;
    private Set<EnumRole> roles = new HashSet<>();

    public TokenServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, TokenService tokenService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.tokenService = tokenService;
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        getRoleRepository().findAll().forEach(role -> roles.add(role.getName()));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        signupForm.email(email).username(username).password(password).roles(roles);
        entity = UserEntity.builder()
            .email(email)
            .keyPassword(password)
            .accessName(username)
            .roles(null)
        .build();
    }

    @Test
    public void givenValidSignupForm_WhenValidateAndRegisterNewUser_ThenDoesNotThrow(){
        Assertions.assertDoesNotThrow(() -> tokenService.validateAndRegisterNewUser(signupForm));
        Assertions.assertEquals(1, getUserRepository().findAll().size());
    }
    
    @Test
    public void givenInvalidSignupForm_WhenValidateAndRegisterNewUser_ThenDuplicatedUsernameOrEmail(){
        getUserRepository().save(entity);

        signupForm.email(emailNotPresent);
        Assertions.assertThrows(EntityExistsException.class, () -> tokenService.validateAndRegisterNewUser(signupForm),
            errorUsernameDuplicated);

        signupForm.username(usernameNotPresent).email(email);
        Assertions.assertThrows(EntityExistsException.class, () -> tokenService.validateAndRegisterNewUser(signupForm),
            errorEmailDuplicated);
    }

}
