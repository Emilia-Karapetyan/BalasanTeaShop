package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private int age;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserType type;
    @Column
    private boolean verify;
    @Column
    private String token;
    @Column
    private int active;
    @Column(name = "pic_url")
    private String picUrl;
    @Column
    @Enumerated(EnumType.STRING)
    private UserGender gender;
    @Column
    private int code;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Product> productList = new ArrayList<>();
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "wishlist",
//            joinColumns = {@JoinColumn(name = "user_id")},
//            inverseJoinColumns = {@JoinColumn(name = "product_id")}
//    )
//    private Set<Product> productSet = new HashSet<>();


}
