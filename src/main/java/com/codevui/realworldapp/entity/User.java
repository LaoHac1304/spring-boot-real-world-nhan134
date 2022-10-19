package com.codevui.realworldapp.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_tbl")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String username;
    private String bio;
    private String image;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_follow", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "user_id")) // user
                                                                                                                                          // dang
                                                                                                                                          // follow
                                                                                                                                          // ai
    private Set<User> followers; // nhung ai dang follow toi

    @ManyToMany(mappedBy = "followers")
    private Set<User> followings; // dang follow ai

}
