package br.com.juliocauan.authentication.domain.repository;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserRepository {
    Optional<User> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}
