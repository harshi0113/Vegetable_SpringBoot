package com.yash.Vegetabledeliveryonline.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yash.Vegetabledeliveryonline.service.UserService;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String phone;
    private String email;
    private String address;
    private String loginName;

    private String password;
    private Integer role;
    private Integer loginStatus;

    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] image;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            switch (role) {
                case 1:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    break;
                case 2:
                    authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                    break;

                case 3:
                    authorities.add(new SimpleGrantedAuthority("ROLE_BUYER"));
                    break;

            }
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.loginName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.getLoginStatus().equals(UserService.LOGIN_STATUS_BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}



