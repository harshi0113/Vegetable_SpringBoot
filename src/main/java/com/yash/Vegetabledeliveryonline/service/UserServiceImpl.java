package com.yash.Vegetabledeliveryonline.service;

import com.yash.Vegetabledeliveryonline.exception.UserBlockedException;
import com.yash.Vegetabledeliveryonline.repository.UserRepository;
import com.yash.Vegetabledeliveryonline.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        // Validate required fields
        if (user.getLoginName() == null || user.getLoginName().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getName() == null || user.getName().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getRole() == null) {
            throw new IllegalArgumentException("All required fields must be provided");
        }

        // Check for duplicate username
        if (userRepository.existsByLoginName(user.getLoginName())) {
            throw new DuplicateKeyException("Username already exists: " + user.getLoginName());
        }

        // Encode password and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // This method isn't needed anymore since we're using Spring Security's authentication
    @Deprecated
    @Override
    public User login(String loginName, String password) throws UserBlockedException {
        User user = userRepository.findByLoginName(loginName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getLoginStatus().equals(LOGIN_STATUS_BLOCKED)) {
            throw new UserBlockedException("Your account approval request is pending. Contact admin.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return user;
    }

    @Override
    public List<User> getBuyerList() {
        return userRepository.findByRole(ROLE_BUYER);
    }

    @Override
    public List<User> getSellerList() {
        return userRepository.findByRole(ROLE_SELLER);
    }

    @Override
    public void changeLoginStatus(Long userId, Integer loginStatus) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLoginStatus(loginStatus);
            userRepository.save(user);
        });
    }

    @Override
    public boolean isUsernameExist(String username) {
        return userRepository.existsByLoginName(username);
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public User findByLoginName(String loginName) {
        return userRepository.findByLoginName(loginName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login name: " + loginName));
    }
}