package com.yash.Vegetabledeliveryonline.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Shop shop;

    private String itemName;
    private String description;
    private Double price;
    private String vegetableCategory;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;


}