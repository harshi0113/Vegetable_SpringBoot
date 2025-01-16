package com.yash.Vegetabledeliveryonline.service;



import com.yash.Vegetabledeliveryonline.exception.UserBlockedException;
import com.yash.Vegetabledeliveryonline.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Integer ROLE_ADMIN = 1;
    Integer ROLE_SELLER = 2;
    Integer ROLE_BUYER = 3;

    Integer LOGIN_STATUS_ACTIVE = 1;
    Integer LOGIN_STATUS_BLOCKED = 2;

    User register(User user);
    User login(String loginName, String password) throws UserBlockedException;
    List<User> getBuyerList();
    List<User> getSellerList();
    void changeLoginStatus(Long userId, Integer loginStatus);
    boolean isUsernameExist(String username);
    long getTotalUsers();
    public User findById(Long userId);
     User findByLoginName(String loginName);

}