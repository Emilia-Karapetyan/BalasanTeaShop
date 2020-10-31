package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findOneByEmail(String s);
    boolean existsUserByEmail(String email);
    boolean existsUserByPhone(String phone);
    User findOneByPhone(String phone);
    User findOneById(int id);

    @Transactional
    @Modifying
    @Query("delete from User u where u.id = ?1")
    void deleteUserById(int id);


    @Transactional
    @Modifying
    @Query("update User u set u.active=?1 where u.id=?2")
    void updateUserByActive(int active, int id);


    @Query("select u from User u where u.active>0")
    List<User> findAllByActive();
}
