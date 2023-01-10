package br.com.juliocauan.authentication.repository;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class UserRepositoryTest extends TestContext {

    private final String password = "12345678";
    private final String username = "testUsername";
    private final String email = "test@email.com";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();
    
    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        getRoleRepository().findAll().forEach(role -> roles.add(role));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .email(email)
            .keyPassword(password)
            .accessName(username)
            .roles(roles)
        .build();
    }

    @Test
    public void givenPresentUsername_WhenExistsByUsername_ThenTrue(){
        getUserRepository().save(entity);
        Assertions.assertTrue(getUserRepository().existsByAccessName(username));
    }

    @Test
    public void givenNotPresentUsername_WhenExistsByUsername_ThenFalse(){
        Assertions.assertFalse(getUserRepository().existsByAccessName(username));
    }

    @Test
    public void givenPresentEmail_WhenExistsByEmail_ThenTrue(){
        getUserRepository().save(entity);
        Assertions.assertTrue(getUserRepository().existsByEmail(email));
    }

    @Test
    public void givenNotPresentEmail_WhenExistsByEmail_ThenFalse(){
        Assertions.assertFalse(getUserRepository().existsByEmail(email));
    }

    @Test
    public void givenPresentUsername_WhenFindByUsername_ThenUser(){
        getUserRepository().save(entity);
        Assertions.assertEquals(entity, getUserRepository().findByAccessName(username).get());
    }

    @Test
    public void givenNotPresentUsername_WhenFindByUsername_ThenUserNotPresent(){
        Assertions.assertFalse(getUserRepository().findByAccessName(username).isPresent());
    }

}