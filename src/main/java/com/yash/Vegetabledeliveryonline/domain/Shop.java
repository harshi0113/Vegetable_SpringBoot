package com.yash.Vegetabledeliveryonline.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String remark;
}