package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.WishList;
import com.sun.deploy.config.JREInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;
import java.util.List;

public interface WishlistRepository  extends JpaRepository<WishList,Integer> {
    int countByUserIdAndProductId(int id, int id1);
    @Transactional
    @Modifying
    @Query("delete from WishList w where w.id = ?1")
    void deleteWishlistById(int id);

    WishList findOneByUserIdAndProductId(int id,int id1);


}
