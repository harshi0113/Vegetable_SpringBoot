package com.yash.Vegetabledeliveryonline.repository;



import com.yash.Vegetabledeliveryonline.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginNameAndPassword(String loginName, String password);
    Optional<User> findByLoginName(String loginName);
    List<User> findByRole(Integer role);
    boolean existsByLoginName(String loginName);
}
