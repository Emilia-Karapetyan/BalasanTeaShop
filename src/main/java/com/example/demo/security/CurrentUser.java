package com.example.demo.security;


import com.example.demo.model.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(),user.getPassword(),user.isVerify(),true,true,user.getActive()==0
                , AuthorityUtils.createAuthorityList(user.getType().name()));
        this.user=user;
    }

    public User getUser() {
        return user;
    }

}
