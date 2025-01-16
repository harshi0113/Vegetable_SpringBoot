//package com.yash.Vegetabledeliveryonline.domain;
//
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.io.Serializable;
//
//@Data
//@Entity
//@Table(name = "cart")
//public class Cart implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer cart_id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId", referencedColumnName = "userId")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "menuId", referencedColumnName = "menuId")
//    private Menu menu;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "contactId", referencedColumnName = "id")
//    private Shop shop;
//
//    @Column(name = "quantity")
//    private Integer quantity;
//
//    @Column(name = "total_price")
//    private Double totalPrice;
//
//
//}