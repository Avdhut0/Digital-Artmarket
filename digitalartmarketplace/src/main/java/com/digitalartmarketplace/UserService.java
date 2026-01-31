package com.digitalartmarketplace;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER USER
    public void register(User user) {
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
    }

    // FIND USER BY EMAIL (LOGIN / PROFILE)
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    // ✅ SAVE USER (EDIT PROFILE)
    public void save(User user) {
        repo.save(user);
    }
}
